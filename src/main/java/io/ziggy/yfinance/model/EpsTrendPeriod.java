package io.ziggy.yfinance.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** How the consensus EPS estimate for a period has drifted over the trailing 90 days. */
public record EpsTrendPeriod(
        String period,
        LocalDate endDate,
        BigDecimal current,
        BigDecimal sevenDaysAgo,
        BigDecimal thirtyDaysAgo,
        BigDecimal sixtyDaysAgo,
        BigDecimal ninetyDaysAgo) {}
