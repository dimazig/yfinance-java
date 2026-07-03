package io.ziggy.yfinance.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** Raw deserialization of the {@code /v1/finance/search} response. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SearchResponse(List<Quote> quotes, List<News> news) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Quote(String symbol, String shortname, String longname, String exchange, String exchDisp, String quoteType) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record News(String uuid, String title, String publisher, String link, Long providerPublishTime, String type) {}
}
