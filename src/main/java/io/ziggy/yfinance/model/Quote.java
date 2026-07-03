package io.ziggy.yfinance.model;

import io.ziggy.yfinance.valueobject.Symbol;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * A consolidated market quote drawn from the {@code price}, {@code summaryDetail},
 * {@code financialData}, {@code defaultKeyStatistics} and {@code quoteType} modules,
 * grouped into identity, {@link PriceSnapshot}, {@link KeyStats} and {@link AnalystSummary}.
 */
public record Quote(
        Symbol symbol,
        String longName,
        String shortName,
        String quoteType,
        String exchange,
        Currency currency,
        String marketState,
        PriceSnapshot price,
        KeyStats keyStats,
        AnalystSummary analyst) {

    /** Current market prices and trading ranges. */
    public record PriceSnapshot(
            BigDecimal regularMarketPrice,
            BigDecimal regularMarketChange,
            BigDecimal regularMarketChangePercent,
            BigDecimal previousClose,
            BigDecimal open,
            BigDecimal dayLow,
            BigDecimal dayHigh,
            Long volume,
            BigDecimal fiftyTwoWeekLow,
            BigDecimal fiftyTwoWeekHigh,
            BigDecimal marketCap) {}

    /** Valuation and share statistics. */
    public record KeyStats(
            BigDecimal trailingPe,
            BigDecimal trailingEps,
            BigDecimal forwardEps,
            BigDecimal bookValue,
            BigDecimal priceToBook,
            BigDecimal beta,
            Long sharesOutstanding,
            BigDecimal dividendYield) {}

    /** Analyst consensus figures. */
    public record AnalystSummary(
            BigDecimal targetMeanPrice,
            BigDecimal recommendationMean,
            String recommendationKey,
            Integer numberOfAnalystOpinions,
            BigDecimal totalRevenue,
            BigDecimal profitMargins) {}
}
