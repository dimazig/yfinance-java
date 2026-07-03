package io.ziggy.yfinance.mapper;

import io.ziggy.yfinance.dto.quotesummary.QuoteSummaryResponse.InsiderHolders;
import io.ziggy.yfinance.dto.quotesummary.QuoteSummaryResponse.InsiderTransactions;
import io.ziggy.yfinance.dto.quotesummary.QuoteSummaryResponse.Ownership;
import io.ziggy.yfinance.dto.quotesummary.QuoteSummaryResponse.Result;
import io.ziggy.yfinance.model.Holders;
import io.ziggy.yfinance.model.Holders.InsiderRosterEntry;
import io.ziggy.yfinance.model.Holders.InsiderTransaction;
import io.ziggy.yfinance.model.Holders.InstitutionalHolder;
import io.ziggy.yfinance.model.Holders.MajorHoldersBreakdown;
import io.ziggy.yfinance.model.Holders.NetSharePurchaseActivity;
import java.util.List;

/** Maps holder-related quoteSummary modules into {@link Holders}. */
public final class HoldersMapper {

    private HoldersMapper() {}

    public static Holders toHolders(Result r) {
        return new Holders(
                mapBreakdown(r),
                mapOwnership(r.institutionOwnership()),
                mapOwnership(r.fundOwnership()),
                mapInsiderTransactions(r.insiderTransactions()),
                mapInsiderRoster(r.insiderHolders()),
                mapNetSharePurchaseActivity(r.netSharePurchaseActivity()));
    }

    private static MajorHoldersBreakdown mapBreakdown(Result r) {
        var b = r.majorHoldersBreakdown();
        if (b == null) {
            return null;
        }
        return new MajorHoldersBreakdown(
                b.insidersPercentHeld(), b.institutionsPercentHeld(),
                b.institutionsFloatPercentHeld(), b.institutionsCount());
    }

    private static List<InstitutionalHolder> mapOwnership(Ownership ownership) {
        if (ownership == null || ownership.ownershipList() == null) {
            return List.of();
        }
        return ownership.ownershipList().stream()
                .map(o -> new InstitutionalHolder(
                        MapperSupport.epochSecond(o.reportDate()), o.organization(),
                        o.pctHeld(), o.position(), o.value(), o.pctChange()))
                .toList();
    }

    private static List<InsiderTransaction> mapInsiderTransactions(InsiderTransactions tx) {
        if (tx == null || tx.transactions() == null) {
            return List.of();
        }
        return tx.transactions().stream()
                .map(t -> new InsiderTransaction(
                        MapperSupport.epochSecond(t.startDate()), t.filerName(), t.filerRelation(),
                        t.transactionText(), t.shares(), t.value()))
                .toList();
    }

    private static List<InsiderRosterEntry> mapInsiderRoster(InsiderHolders insiderHolders) {
        if (insiderHolders == null || insiderHolders.holders() == null) {
            return List.of();
        }
        return insiderHolders.holders().stream()
                .map(h -> new InsiderRosterEntry(
                        h.name(), h.relation(), h.transactionDescription(),
                        MapperSupport.epochSecond(h.latestTransDate()),
                        h.positionDirect(),
                        MapperSupport.epochSecond(h.positionDirectDate())))
                .toList();
    }

    private static NetSharePurchaseActivity mapNetSharePurchaseActivity(
            io.ziggy.yfinance.dto.quotesummary.QuoteSummaryResponse.NetSharePurchaseActivity a) {
        if (a == null) {
            return null;
        }
        return new NetSharePurchaseActivity(
                a.period(),
                a.buyInfoCount(), a.buyInfoShares(), a.buyPercentInsiderShares(),
                a.sellInfoCount(), a.sellInfoShares(), a.sellPercentInsiderShares(),
                a.netInfoCount(), a.netInfoShares(), a.netPercentInsiderShares(),
                a.totalInsiderShares());
    }
}
