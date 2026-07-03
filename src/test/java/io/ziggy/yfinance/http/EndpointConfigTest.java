package io.ziggy.yfinance.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import okhttp3.HttpUrl;
import org.junit.jupiter.api.Test;

class EndpointConfigTest {

    private static final HttpUrl URL = HttpUrl.get("https://example.test/");

    @Test
    void fourArgConstructorDefaultsTimeout() {
        var config = new EndpointConfig(URL, URL, URL, "ua");
        assertThat(config.callTimeout()).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    void timeoutIsConfigurable() {
        var config = new EndpointConfig(URL, URL, URL, "ua", Duration.ofSeconds(5));
        assertThat(config.callTimeout()).isEqualTo(Duration.ofSeconds(5));
    }

    @Test
    void productionHasDefaults() {
        var config = EndpointConfig.production();
        assertThat(config.callTimeout()).isEqualTo(Duration.ofSeconds(30));
        assertThat(config.query1Base().host()).isEqualTo("query1.finance.yahoo.com");
        assertThat(config.adaptiveRateLimit().enabled()).isTrue();
        assertThat(config.adaptiveRateLimit().maxDelay()).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    void adaptiveRateLimitIsConfigurable() {
        var config = new EndpointConfig(URL, URL, URL, "ua")
                .withAdaptiveRateLimit(AdaptiveRateLimitConfig.disabled());

        assertThat(config.adaptiveRateLimit().enabled()).isFalse();
    }
}
