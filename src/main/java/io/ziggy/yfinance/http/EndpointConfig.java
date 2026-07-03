package io.ziggy.yfinance.http;

import java.time.Duration;
import java.util.Objects;
import okhttp3.HttpUrl;

/**
 * Host roots and HTTP identity used to reach Yahoo Finance.
 *
 * @param query1Base  primary API host ({@code https://query1.finance.yahoo.com/})
 * @param query2Base  secondary API host ({@code https://query2.finance.yahoo.com/}), used for
 *     fundamentals timeseries
 * @param cookieUrl   URL hit purely to seed session cookies ({@code https://fc.yahoo.com/})
 * @param userAgent   the {@code User-Agent} header sent on every request
 * @param callTimeout overall per-call timeout applied to the OkHttp clients
 * @param adaptiveRateLimit adaptive client-side throttling after HTTP 429 responses
 */
public record EndpointConfig(
        HttpUrl query1Base,
        HttpUrl query2Base,
        HttpUrl cookieUrl,
        String userAgent,
        Duration callTimeout,
        AdaptiveRateLimitConfig adaptiveRateLimit) {

    private static final String DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 "
                    + "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

    private static final Duration DEFAULT_CALL_TIMEOUT = Duration.ofSeconds(30);

    public EndpointConfig {
        Objects.requireNonNull(query1Base, "query1Base");
        Objects.requireNonNull(query2Base, "query2Base");
        Objects.requireNonNull(cookieUrl, "cookieUrl");
        Objects.requireNonNull(userAgent, "userAgent");
        Objects.requireNonNull(callTimeout, "callTimeout");
        Objects.requireNonNull(adaptiveRateLimit, "adaptiveRateLimit");
    }

    /** Convenience constructor using the default adaptive rate-limit config. */
    public EndpointConfig(
            HttpUrl query1Base, HttpUrl query2Base, HttpUrl cookieUrl, String userAgent, Duration callTimeout) {
        this(query1Base, query2Base, cookieUrl, userAgent, callTimeout, AdaptiveRateLimitConfig.defaults());
    }

    /** Convenience constructor using the default 30s call timeout. */
    public EndpointConfig(HttpUrl query1Base, HttpUrl query2Base, HttpUrl cookieUrl, String userAgent) {
        this(query1Base, query2Base, cookieUrl, userAgent, DEFAULT_CALL_TIMEOUT);
    }

    /** The production Yahoo Finance configuration. */
    public static EndpointConfig production() {
        return new EndpointConfig(
                HttpUrl.get("https://query1.finance.yahoo.com/"),
                HttpUrl.get("https://query2.finance.yahoo.com/"),
                HttpUrl.get("https://fc.yahoo.com/"),
                DEFAULT_USER_AGENT,
                DEFAULT_CALL_TIMEOUT,
                AdaptiveRateLimitConfig.defaults());
    }

    /** Returns a copy with a different call timeout. */
    public EndpointConfig withCallTimeout(Duration timeout) {
        return new EndpointConfig(query1Base, query2Base, cookieUrl, userAgent, timeout, adaptiveRateLimit);
    }

    /** Returns a copy with a different adaptive rate-limit config. */
    public EndpointConfig withAdaptiveRateLimit(AdaptiveRateLimitConfig config) {
        return new EndpointConfig(query1Base, query2Base, cookieUrl, userAgent, callTimeout, config);
    }

    /** URL of the crumb-issuing endpoint on the primary host. */
    public HttpUrl crumbUrl() {
        return query1Base.newBuilder().addPathSegments("v1/test/getcrumb").build();
    }
}
