package io.ziggy.yfinance.mapper;

import io.ziggy.yfinance.dto.quotesummary.QuoteSummaryResponse.EarningsHistory;
import io.ziggy.yfinance.dto.quotesummary.QuoteSummaryResponse.EarningsTrend.Estimate;
import io.ziggy.yfinance.dto.quotesummary.QuoteSummaryResponse.EarningsTrend.TrendRow;
import io.ziggy.yfinance.dto.quotesummary.QuoteSummaryResponse.FinancialData;
import io.ziggy.yfinance.dto.quotesummary.QuoteSummaryResponse.Result;
import io.ziggy.yfinance.model.AnalystPriceTarget;
import io.ziggy.yfinance.model.EarningsHistoryEntry;
import io.ziggy.yfinance.model.EpsRevisionsPeriod;
import io.ziggy.yfinance.model.EpsTrendPeriod;
import io.ziggy.yfinance.model.GrowthEstimate;
import io.ziggy.yfinance.model.PeriodEstimate;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/** Maps analysis-related quoteSummary modules into analyst models. */
public final class AnalysisMapper {

    private AnalysisMapper() {}

    public static AnalystPriceTarget toPriceTarget(Result r) {
        FinancialData fd = r.financialData();
        if (fd == null) {
            return null;
        }
        return new AnalystPriceTarget(
                fd.currentPrice(), fd.targetLowPrice(), fd.targetHighPrice(),
                fd.targetMeanPrice(), fd.targetMedianPrice(), fd.numberOfAnalystOpinions());
    }

    public static List<PeriodEstimate> toEarningsEstimate(Result r) {
        return estimates(r, TrendRow::earningsEstimate);
    }

    public static List<PeriodEstimate> toRevenueEstimate(Result r) {
        return estimates(r, TrendRow::revenueEstimate);
    }

    public static List<EarningsHistoryEntry> toEarningsHistory(Result r) {
        EarningsHistory history = r.earningsHistory();
        if (history == null || history.history() == null) {
            return List.of();
        }
        return history.history().stream()
                .map(h -> new EarningsHistoryEntry(
                        h.period(),
                        MapperSupport.epochSecond(h.quarter()),
                        h.epsActual(), h.epsEstimate(), h.epsDifference(), h.surprisePercent()))
                .toList();
    }

    public static List<EpsTrendPeriod> toEpsTrend(Result r) {
        return trendRows(r).stream()
                .filter(row -> row.epsTrend() != null)
                .map(row -> new EpsTrendPeriod(
                        row.period(), endDate(row),
                        row.epsTrend().current(),
                        row.epsTrend().sevenDaysAgo(),
                        row.epsTrend().thirtyDaysAgo(),
                        row.epsTrend().sixtyDaysAgo(),
                        row.epsTrend().ninetyDaysAgo()))
                .toList();
    }

    public static List<EpsRevisionsPeriod> toEpsRevisions(Result r) {
        return trendRows(r).stream()
                .filter(row -> row.epsRevisions() != null)
                .map(row -> new EpsRevisionsPeriod(
                        row.period(), endDate(row),
                        row.epsRevisions().upLast7Days(),
                        row.epsRevisions().upLast30Days(),
                        row.epsRevisions().downLast30Days(),
                        row.epsRevisions().downLast90Days()))
                .toList();
    }

    public static List<GrowthEstimate> toGrowthEstimates(Result r) {
        return trendRows(r).stream()
                .filter(row -> row.growth() != null)
                .map(row -> new GrowthEstimate(row.period(), endDate(row), row.growth()))
                .toList();
    }

    private static List<TrendRow> trendRows(Result r) {
        if (r.earningsTrend() == null || r.earningsTrend().trend() == null) {
            return List.of();
        }
        return r.earningsTrend().trend();
    }

    private static LocalDate endDate(TrendRow row) {
        return row.endDate() != null ? LocalDate.parse(row.endDate()) : null;
    }

    private static List<PeriodEstimate> estimates(Result r, Function<TrendRow, Estimate> pick) {
        return trendRows(r).stream()
                .map(row -> {
                    Estimate e = pick.apply(row);
                    return new PeriodEstimate(
                            row.period(),
                            endDate(row),
                            e != null ? e.avg() : null,
                            e != null ? e.low() : null,
                            e != null ? e.high() : null,
                            e != null ? e.numberOfAnalysts() : null,
                            e != null ? e.yearAgoEps() : null);
                })
                .toList();
    }
}
