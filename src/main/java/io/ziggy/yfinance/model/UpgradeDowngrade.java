package io.ziggy.yfinance.model;

import java.time.Instant;

/** A single analyst rating change. */
public record UpgradeDowngrade(
        Instant gradeDate, String firm, String toGrade, String fromGrade, String action) {}
