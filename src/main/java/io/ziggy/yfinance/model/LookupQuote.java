package io.ziggy.yfinance.model;

import io.ziggy.yfinance.valueobject.Symbol;
import java.math.BigDecimal;

/** A single instrument returned by the lookup endpoint. */
public record LookupQuote(
        Symbol symbol, String shortName, String quoteType, String exchange, BigDecimal regularMarketPrice) {}
