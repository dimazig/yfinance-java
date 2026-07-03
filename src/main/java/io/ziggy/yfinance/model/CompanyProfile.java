package io.ziggy.yfinance.model;

import java.net.URI;
import java.util.List;

/** Company profile data from the {@code assetProfile} module. */
public record CompanyProfile(
        String address,
        String city,
        String state,
        String zip,
        String country,
        String phone,
        URI website,
        String industry,
        String sector,
        String longBusinessSummary,
        Integer fullTimeEmployees,
        List<CompanyOfficer> officers) {

    public CompanyProfile {
        officers = officers == null ? List.of() : List.copyOf(officers);
    }

    /** A named company officer. */
    public record CompanyOfficer(String name, String title, Integer age, Long totalPay) {}
}
