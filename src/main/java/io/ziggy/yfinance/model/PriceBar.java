package io.ziggy.yfinance.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A single OHLCV candle.
 *
 * @param adjClose split/dividend-adjusted close, or {@code null} if not provided by Yahoo
 * @param volume   traded volume, or {@code null} when Yahoo reported none — a missing value is
 *                 deliberately not coerced to zero so stored data stays honest
 */
public record PriceBar(
        Instant timestamp,
        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        BigDecimal close,
        BigDecimal adjClose,
        Long volume) {}
