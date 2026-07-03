package io.ziggy.yfinance.service;

import io.ziggy.yfinance.enums.LineItem;
import io.ziggy.yfinance.enums.StatementType;
import java.util.List;

/**
 * Resolves the fundamental line-item keys requested per statement. Backed by the public
 * {@link LineItem} enum so the typed and string APIs share one source of truth. These are the
 * un-prefixed keys; the {@link io.ziggy.yfinance.enums.Frequency} prefix is applied per request.
 */
final class FundamentalKeys {

    private FundamentalKeys() {}

    static List<String> forStatement(StatementType type) {
        return LineItem.forStatement(type).stream().map(LineItem::key).toList();
    }
}
