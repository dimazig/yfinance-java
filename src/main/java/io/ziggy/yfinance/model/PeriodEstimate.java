package io.ziggy.yfinance.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * An analyst estimate (earnings or revenue) for a forward/trailing period such as {@code 0q}
 * or {@code +1y}.
 */
public record PeriodEstimate(
        String period,
        LocalDate endDate,
        BigDecimal average,
        BigDecimal low,
        BigDecimal high,
        Integer numberOfAnalysts,
        BigDecimal yearAgoEps) {}
