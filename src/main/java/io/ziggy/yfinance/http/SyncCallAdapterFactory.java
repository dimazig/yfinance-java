package io.ziggy.yfinance.http;

import io.ziggy.yfinance.exception.YFDataException;
import io.ziggy.yfinance.exception.YFRateLimitException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.Duration;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A Retrofit {@link CallAdapter.Factory} that lets API methods declare the DTO body as their return
 * type (e.g. {@code ChartResponse chart(...)}) instead of {@code Call<ChartResponse>}. The adapter
 * executes the call synchronously and translates transport-level failures into yfinance exceptions,
 * so services receive a ready-to-map DTO.
 */
public final class SyncCallAdapterFactory extends CallAdapter.Factory {

    public static SyncCallAdapterFactory create() {
        return new SyncCallAdapterFactory();
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        // Leave raw Call<T> return types to Retrofit's default adapter.
        if (getRawType(returnType) == Call.class) {
            return null;
        }
        return new CallAdapter<Object, Object>() {
            @Override
            public Type responseType() {
                return returnType;
            }

            @Override
            public Object adapt(Call<Object> call) {
                return execute(call);
            }
        };
    }

    private static Object execute(Call<Object> call) {
        Response<Object> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new YFDataException("I/O error calling Yahoo Finance", e);
        }
        if (response.code() == 429) {
            throw new YFRateLimitException(
                    "Yahoo Finance rate limit hit (HTTP 429)" + errorDetail(response),
                    parseRetryAfter(response.headers().get("Retry-After")));
        }
        if (!response.isSuccessful()) {
            throw new YFDataException("Yahoo Finance returned HTTP " + response.code() + errorDetail(response));
        }
        Object body = response.body();
        if (body == null) {
            throw new YFDataException("Yahoo Finance returned an empty body");
        }
        return body;
    }

    /** Reads the (already-buffered) error body for a clearer message; tolerant of read failures. */
    private static String errorDetail(Response<?> response) {
        try (var errorBody = response.errorBody()) {
            if (errorBody == null) {
                return "";
            }
            String text = errorBody.string().strip();
            return text.isEmpty() ? "" : ": " + text;
        } catch (IOException e) {
            return "";
        }
    }

    /** Parses a {@code Retry-After} header expressed as a whole number of seconds. */
    private static Duration parseRetryAfter(String headerValue) {
        if (headerValue == null) {
            return null;
        }
        try {
            return Duration.ofSeconds(Long.parseLong(headerValue.strip()));
        } catch (NumberFormatException e) {
            return null; // HTTP-date form is not supported; treat as absent
        }
    }
}
