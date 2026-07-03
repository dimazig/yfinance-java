package io.ziggy.yfinance.api;

import io.ziggy.yfinance.dto.chart.ChartResponse;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/** Retrofit binding for Yahoo's price-history (chart) endpoint. */
public interface ChartApi {

    @GET("v8/finance/chart/{symbol}")
    ChartResponse chart(
            @Path("symbol") String symbol,
            @Query("interval") String interval,
            @Query("range") String range,
            @Query("period1") Long period1,
            @Query("period2") Long period2,
            @Query("includePrePost") boolean includePrePost,
            @Query("events") String events);
}
