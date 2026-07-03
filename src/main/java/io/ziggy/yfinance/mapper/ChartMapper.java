package io.ziggy.yfinance.mapper;

import io.ziggy.yfinance.dto.chart.ChartResponse;
import io.ziggy.yfinance.dto.chart.ChartResponse.ChartEvents;
import io.ziggy.yfinance.dto.chart.ChartResponse.ChartMeta;
import io.ziggy.yfinance.dto.chart.ChartResponse.ChartResult;
import io.ziggy.yfinance.dto.chart.ChartResponse.Quote;
import io.ziggy.yfinance.exception.YFDataException;
import io.ziggy.yfinance.model.CapitalGain;
import io.ziggy.yfinance.model.Dividend;
import io.ziggy.yfinance.model.HistoryMetadata;
import io.ziggy.yfinance.model.PriceBar;
import io.ziggy.yfinance.model.PriceHistory;
import io.ziggy.yfinance.model.Split;
import io.ziggy.yfinance.valueobject.Symbol;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Maps the raw {@link ChartResponse} into the clean {@link PriceHistory} model. */
public final class ChartMapper {

    private ChartMapper() {}

    public static PriceHistory toPriceHistory(ChartResponse response, Symbol requested) {
        var chart = response.chart();
        if (chart == null) {
            throw new YFDataException("Malformed chart response for " + requested);
        }
        ChartResult result = MapperSupport.firstResult(chart.result(), chart.error(), "chart data", requested);
        return new PriceHistory(
                mapMetadata(result.meta(), requested),
                mapBars(result),
                mapDividends(result.events()),
                mapSplits(result.events()),
                mapCapitalGains(result.events()));
    }

    private static HistoryMetadata mapMetadata(ChartMeta meta, Symbol requested) {
        if (meta == null) {
            return new HistoryMetadata(requested, null, null, null, null, null, null, null, null);
        }
        return new HistoryMetadata(
                meta.symbol() != null ? Symbol.of(meta.symbol()) : requested,
                MapperSupport.currency(meta.currency()),
                meta.exchangeName(),
                meta.fullExchangeName(),
                meta.instrumentType(),
                MapperSupport.zoneId(meta.exchangeTimezoneName()),
                MapperSupport.epochSecond(meta.firstTradeDate()),
                meta.regularMarketPrice(),
                meta.chartPreviousClose());
    }

    private static List<PriceBar> mapBars(ChartResult result) {
        var timestamps = result.timestamp();
        if (timestamps == null || result.indicators() == null
                || result.indicators().quote() == null
                || result.indicators().quote().isEmpty()) {
            return List.of();
        }
        Quote quote = result.indicators().quote().getFirst();
        List<BigDecimal> adjClose = result.indicators().adjclose() != null
                && !result.indicators().adjclose().isEmpty()
                ? result.indicators().adjclose().getFirst().adjclose()
                : null;

        var bars = new ArrayList<PriceBar>(timestamps.size());
        for (int i = 0; i < timestamps.size(); i++) {
            BigDecimal close = at(quote.close(), i);
            if (close == null) {
                // Yahoo pads intraday series with all-null rows (halts, pre-open); skip them.
                continue;
            }
            bars.add(new PriceBar(
                    Instant.ofEpochSecond(timestamps.get(i)),
                    at(quote.open(), i),
                    at(quote.high(), i),
                    at(quote.low(), i),
                    close,
                    at(adjClose, i),
                    at(quote.volume(), i)));
        }
        return bars;
    }

    private static List<Dividend> mapDividends(ChartEvents events) {
        if (events == null || events.dividends() == null) {
            return List.of();
        }
        return events.dividends().values().stream()
                .map(d -> new Dividend(MapperSupport.epochSecond(d.date()), d.amount()))
                .sorted(Comparator.comparing(Dividend::date))
                .toList();
    }

    private static List<Split> mapSplits(ChartEvents events) {
        if (events == null || events.splits() == null) {
            return List.of();
        }
        return events.splits().values().stream()
                .map(s -> new Split(MapperSupport.epochSecond(s.date()), s.numerator(), s.denominator(), s.splitRatio()))
                .sorted(Comparator.comparing(Split::date))
                .toList();
    }

    private static List<CapitalGain> mapCapitalGains(ChartEvents events) {
        if (events == null || events.capitalGains() == null) {
            return List.of();
        }
        return events.capitalGains().values().stream()
                .map(c -> new CapitalGain(MapperSupport.epochSecond(c.date()), c.amount()))
                .sorted(Comparator.comparing(CapitalGain::date))
                .toList();
    }

    private static <T> T at(List<T> list, int index) {
        return list != null && index < list.size() ? list.get(index) : null;
    }
}
