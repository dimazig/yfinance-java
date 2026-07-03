package io.ziggy.yfinance;

import static org.assertj.core.api.Assertions.assertThat;

import io.ziggy.yfinance.api.YahooApis;
import io.ziggy.yfinance.exception.YFDataException;
import io.ziggy.yfinance.testsupport.Fixtures;
import io.ziggy.yfinance.valueobject.Symbol;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TickersTest {

    private MockWebServer server;
    private YFinance yf;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        var retrofit = Fixtures.retrofit(server.url("/"));
        yf = YFinance.fromApis(YahooApis.create(retrofit, retrofit));
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void fanOutRespectsConcurrencyBound() {
        var inFlight = new java.util.concurrent.atomic.AtomicInteger();
        var maxInFlight = new java.util.concurrent.atomic.AtomicInteger();
        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                int current = inFlight.incrementAndGet();
                maxInFlight.accumulateAndGet(current, Math::max);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    inFlight.decrementAndGet();
                }
                return Fixtures.jsonResponse("quotesummary_aapl.json");
            }
        });

        var results = yf.tickers("AAPL", "MSFT", "GOOG", "AMZN", "META", "NFLX")
                .withConcurrency(2)
                .infos();

        assertThat(results).hasSize(6);
        assertThat(results.values()).allMatch(Tickers.Result::isSuccess);
        assertThat(maxInFlight.get()).isLessThanOrEqualTo(2);
    }

    @Test
    void infosReturnPerSymbolResultsAndNeverLoseSuccessesOnFailure() {
        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath() != null && request.getPath().contains("MSFT")) {
                    return new MockResponse().setResponseCode(200).setBody(
                            "{\"quoteSummary\":{\"result\":null,"
                                    + "\"error\":{\"code\":\"Not Found\",\"description\":\"boom\"}}}");
                }
                return Fixtures.jsonResponse("quotesummary_aapl.json");
            }
        });

        var results = yf.tickers("AAPL", "MSFT").infos();

        assertThat(results).containsOnlyKeys(Symbol.of("AAPL"), Symbol.of("MSFT"));

        var aapl = results.get(Symbol.of("AAPL"));
        assertThat(aapl.isSuccess()).isTrue();
        assertThat(aapl.value().profile().sector()).isEqualTo("Technology");
        assertThat(aapl.error()).isNull();

        var msft = results.get(Symbol.of("MSFT"));
        assertThat(msft.isSuccess()).isFalse();
        assertThat(msft.value()).isNull();
        assertThat(msft.error()).isInstanceOf(YFDataException.class);
    }

    @Test
    void resultOrElseThrowReturnsValueOrRaises() {
        var ok = Tickers.Result.success(Symbol.of("AAPL"), "v");
        assertThat(ok.orElseThrow()).isEqualTo("v");

        var bad = Tickers.Result.<String>failure(Symbol.of("X"), new YFDataException("nope"));
        assertThat(bad.isSuccess()).isFalse();
        org.assertj.core.api.Assertions.assertThatThrownBy(bad::orElseThrow)
                .isInstanceOf(YFDataException.class);
    }
}
