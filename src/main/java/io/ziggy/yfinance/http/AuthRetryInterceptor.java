package io.ziggy.yfinance.http;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Recovers from an expired/rotated crumb. When Yahoo answers an authenticated request with HTTP 401
 * or 403, this runs {@code onAuthFailure} (which should invalidate the cached crumb) and retries the
 * request exactly once, letting the downstream {@link CrumbInterceptor} attach a fresh crumb.
 *
 * <p>Must be installed <em>before</em> {@link CrumbInterceptor} so the retry re-runs crumb injection.
 */
public final class AuthRetryInterceptor implements Interceptor {

    private final Runnable onAuthFailure;

    public AuthRetryInterceptor(Runnable onAuthFailure) {
        this.onAuthFailure = onAuthFailure;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        var request = chain.request();
        Response response = chain.proceed(request);
        if (response.code() == 401 || response.code() == 403) {
            response.close();
            onAuthFailure.run();
            return chain.proceed(request);
        }
        return response;
    }
}
