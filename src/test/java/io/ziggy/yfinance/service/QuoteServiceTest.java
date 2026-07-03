package io.ziggy.yfinance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.ziggy.yfinance.api.QuoteSummaryApi;
import io.ziggy.yfinance.exception.YFDataException;
import io.ziggy.yfinance.testsupport.Fixtures;
import io.ziggy.yfinance.valueobject.Symbol;
import java.net.URI;
import java.time.Instant;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuoteServiceTest {

    private MockWebServer server;
    private QuoteService service;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        service = new QuoteService(Fixtures.api(server, QuoteSummaryApi.class));
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void parsesProfileAndQuote() throws Exception {
        server.enqueue(Fixtures.jsonResponse("quotesummary_aapl.json"));

        var info = service.getInfo(Symbol.of("AAPL"));

        assertThat(info.profile().sector()).isEqualTo("Technology");
        assertThat(info.profile().industry()).isEqualTo("Consumer Electronics");
        assertThat(info.profile().website()).isEqualTo(URI.create("https://www.apple.com"));
        assertThat(info.profile().fullTimeEmployees()).isEqualTo(161000);
        assertThat(info.profile().officers()).singleElement()
                .satisfies(o -> assertThat(o.name()).isEqualTo("Mr. Timothy D. Cook"));

        var quote = info.quote();
        assertThat(quote.longName()).isEqualTo("Apple Inc.");
        assertThat(quote.currency()).isEqualTo(java.util.Currency.getInstance("USD"));
        assertThat(quote.price().regularMarketPrice()).isEqualByComparingTo("190.5");
        assertThat(quote.price().marketCap()).isEqualByComparingTo("2950000000000");
        assertThat(quote.price().volume()).isEqualTo(52_000_000L);
        assertThat(quote.keyStats().trailingPe()).isEqualByComparingTo("31.2");
        assertThat(quote.keyStats().trailingEps()).isEqualByComparingTo("6.13");
        assertThat(quote.analyst().recommendationKey()).isEqualTo("buy");
        assertThat(quote.analyst().targetMeanPrice()).isEqualByComparingTo("205.0");

        RecordedRequest req = server.takeRequest();
        assertThat(req.getRequestUrl().encodedPath()).isEqualTo("/v10/finance/quoteSummary/AAPL");
        assertThat(req.getRequestUrl().queryParameter("modules")).contains("financialData", "assetProfile");
        assertThat(req.getRequestUrl().queryParameter("formatted")).isEqualTo("false");
    }

    @Test
    void parsesRecommendationsUpgradesCalendarAndFilings() {
        server.enqueue(Fixtures.jsonResponse("quotesummary_aapl.json"));

        var info = service.getInfo(Symbol.of("AAPL"));

        assertThat(info.recommendationTrend()).hasSize(2);
        assertThat(info.recommendationTrend().getFirst().strongBuy()).isEqualTo(11);

        assertThat(info.upgradesDowngrades()).singleElement().satisfies(u -> {
            assertThat(u.firm()).isEqualTo("Morgan Stanley");
            assertThat(u.toGrade()).isEqualTo("Overweight");
            assertThat(u.gradeDate()).isEqualTo(Instant.ofEpochSecond(1706832000));
        });

        assertThat(info.earningsDates()).containsExactly(
                Instant.ofEpochSecond(1714752000), Instant.ofEpochSecond(1715011200));

        assertThat(info.secFilings()).singleElement().satisfies(f -> {
            assertThat(f.type()).isEqualTo("10-Q");
            assertThat(f.url()).isEqualTo(URI.create("https://www.sec.gov/x.htm"));
        });
    }

    @Test
    void throwsOnErrorEnvelope() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "{\"quoteSummary\":{\"result\":null,\"error\":{\"code\":\"Not Found\",\"description\":\"Quote not found\"}}}"));

        assertThatThrownBy(() -> service.getInfo(Symbol.of("NOPE")))
                .isInstanceOf(YFDataException.class)
                .hasMessageContaining("Quote not found");
    }
}
