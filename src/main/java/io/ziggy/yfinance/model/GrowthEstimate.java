package io.ziggy.yfinance.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Consensus growth estimate for a period (e.g. {@code 0q}, {@code +1y}). */
public record GrowthEstimate(String period, LocalDate endDate, BigDecimal growth) {}
