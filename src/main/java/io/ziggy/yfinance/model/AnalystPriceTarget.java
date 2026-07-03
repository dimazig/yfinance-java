package io.ziggy.yfinance.model;

import java.math.BigDecimal;

/** Consolidated analyst price-target figures. */
public record AnalystPriceTarget(
        BigDecimal current,
        BigDecimal low,
        BigDecimal high,
        BigDecimal mean,
        BigDecimal median,
        Integer numberOfAnalysts) {}
