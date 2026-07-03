package io.ziggy.yfinance.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Ownership and insider data assembled from the holder-related quoteSummary modules. */
public record Holders(
        MajorHoldersBreakdown breakdown,
        List<InstitutionalHolder> institutional,
        List<InstitutionalHolder> mutualFund,
        List<InsiderTransaction> insiderTransactions,
        List<InsiderRosterEntry> insiderRoster,
        NetSharePurchaseActivity netSharePurchaseActivity) {

    public Holders {
        institutional = institutional == null ? List.of() : List.copyOf(institutional);
        mutualFund = mutualFund == null ? List.of() : List.copyOf(mutualFund);
        insiderTransactions = insiderTransactions == null ? List.of() : List.copyOf(insiderTransactions);
        insiderRoster = insiderRoster == null ? List.of() : List.copyOf(insiderRoster);
    }

    /** Aggregate ownership percentages. */
    public record MajorHoldersBreakdown(
            BigDecimal insidersPercentHeld,
            BigDecimal institutionsPercentHeld,
            BigDecimal institutionsFloatPercentHeld,
            Integer institutionsCount) {}

    /** A single institutional or mutual-fund holder. */
    public record InstitutionalHolder(
            Instant reportDate,
            String organization,
            BigDecimal pctHeld,
            Long position,
            Long value,
            BigDecimal pctChange) {}

    /** A single insider transaction. */
    public record InsiderTransaction(
            Instant date,
            String filerName,
            String filerRelation,
            String transactionText,
            Long shares,
            Long value) {}

    /** A named insider's current position, from the {@code insiderHolders} module. */
    public record InsiderRosterEntry(
            String name,
            String relation,
            String latestTransactionDescription,
            Instant latestTransactionDate,
            Long positionDirect,
            Instant positionDirectDate) {}

    /** Aggregated insider buy/sell activity over a trailing period (e.g. {@code 6m}). */
    public record NetSharePurchaseActivity(
            String period,
            Integer buyCount,
            Long buyShares,
            BigDecimal buyPercentInsiderShares,
            Integer sellCount,
            Long sellShares,
            BigDecimal sellPercentInsiderShares,
            Integer netCount,
            Long netShares,
            BigDecimal netPercentInsiderShares,
            Long totalInsiderShares) {}
}
