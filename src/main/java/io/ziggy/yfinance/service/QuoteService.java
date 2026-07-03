package io.ziggy.yfinance.service;

import io.ziggy.yfinance.api.QuoteSummaryApi;
import io.ziggy.yfinance.dto.quotesummary.QuoteSummaryResponse;
import io.ziggy.yfinance.enums.QuoteSummaryModule;
import io.ziggy.yfinance.mapper.QuoteSummaryMapper;
import io.ziggy.yfinance.model.Info;
import io.ziggy.yfinance.valueobject.Symbol;
import java.util.List;
import java.util.Objects;

/** Retrieves consolidated company info via the quoteSummary endpoint. */
public final class QuoteService {

    /** Modules fetched to assemble {@link Info}. */
    public static final List<QuoteSummaryModule> INFO_MODULES = List.of(
            QuoteSummaryModule.ASSET_PROFILE,
            QuoteSummaryModule.QUOTE_TYPE,
            QuoteSummaryModule.PRICE,
            QuoteSummaryModule.SUMMARY_DETAIL,
            QuoteSummaryModule.FINANCIAL_DATA,
            QuoteSummaryModule.DEFAULT_KEY_STATISTICS,
            QuoteSummaryModule.CALENDAR_EVENTS,
            QuoteSummaryModule.SEC_FILINGS,
            QuoteSummaryModule.RECOMMENDATION_TREND,
            QuoteSummaryModule.UPGRADE_DOWNGRADE_HISTORY);

    private static final String CORS_DOMAIN = "finance.yahoo.com";

    private final QuoteSummaryApi api;

    public QuoteService(QuoteSummaryApi api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    public Info getInfo(Symbol symbol) {
        return QuoteSummaryMapper.toInfo(fetch(symbol, INFO_MODULES), symbol);
    }

    /** Fetches an arbitrary set of modules; used by holders/analysis services. */
    public QuoteSummaryResponse fetch(Symbol symbol, List<QuoteSummaryModule> modules) {
        return api.quoteSummary(
                symbol.value(), QuoteSummaryModule.toQueryParam(modules), false, CORS_DOMAIN);
    }
}
