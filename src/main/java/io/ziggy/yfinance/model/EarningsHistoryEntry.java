package io.ziggy.yfinance.model;

import java.math.BigDecimal;
import java.time.Instant;

/** Actual-vs-estimate EPS for a past quarter, from the {@code earningsHistory} module. */
public record EarningsHistoryEntry(
        String period,
        Instant quarter,
        BigDecimal epsActual,
        BigDecimal epsEstimate,
        BigDecimal epsDifference,
        BigDecimal surprisePercent) {}
