package io.ziggy.yfinance.model;

import io.ziggy.yfinance.valueobject.Symbol;
import java.net.URI;
import java.time.Instant;
import java.util.List;

/** Result of a Yahoo Finance search: matching quotes and related news. */
public record SearchResult(List<SearchQuote> quotes, List<NewsArticle> news) {

    public SearchResult {
        quotes = quotes == null ? List.of() : List.copyOf(quotes);
        news = news == null ? List.of() : List.copyOf(news);
    }

    /** A quote match from search. */
    public record SearchQuote(Symbol symbol, String shortName, String longName, String exchange, String quoteType) {}

    /** A news article from search. */
    public record NewsArticle(String uuid, String title, String publisher, URI link, Instant publishTime, String type) {}
}
