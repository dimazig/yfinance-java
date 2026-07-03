package io.ziggy.yfinance.model;

import java.time.LocalDate;

/** Counts of analyst EPS estimate revisions for a period. */
public record EpsRevisionsPeriod(
        String period,
        LocalDate endDate,
        Integer upLast7Days,
        Integer upLast30Days,
        Integer downLast30Days,
        Integer downLast90Days) {}
