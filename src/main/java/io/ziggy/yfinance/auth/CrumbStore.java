package io.ziggy.yfinance.auth;

import io.ziggy.yfinance.exception.YFAuthException;
import io.ziggy.yfinance.http.EndpointConfig;
import io.ziggy.yfinance.valueobject.Crumb;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Performs and caches Yahoo's cookie + crumb handshake.
 *
 * <p>The handshake is: (1) hit {@code fc.yahoo.com} to let Yahoo set a session cookie, then
 * (2) request a crumb from {@code /v1/test/getcrumb} (sent with that cookie). The crumb is then
 * attached to every authenticated data request. The result is cached until {@link #invalidate()}.
 *
 * <p>TODO: the EU-consent (guce/collectConsent) CSRF cookie fallback that Python yfinance uses is
 * not yet implemented; only the {@code fc.yahoo.com} cookie strategy is attempted.
 */
public final class CrumbStore {

    private final OkHttpClient client;
    private final EndpointConfig config;
    private volatile Crumb cached;

    public CrumbStore(OkHttpClient client, EndpointConfig config) {
        this.client = client;
        this.config = config;
    }

    public Crumb getCrumb() {
        Crumb local = cached;
        if (local != null) {
            return local;
        }
        synchronized (this) {
            if (cached == null) {
                cached = fetch();
            }
            return cached;
        }
    }

    /**
     * Drops the cached crumb so the next {@link #getCrumb()} repeats the handshake. Call this when
     * Yahoo rejects the crumb (HTTP 401/403) so a long-running client can recover from rotation.
     */
    public void invalidate() {
        synchronized (this) {
            cached = null;
        }
    }

    private Crumb fetch() {
        seedCookie();
        var request = new Request.Builder().url(config.crumbUrl()).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new YFAuthException(
                        "Failed to obtain crumb: HTTP " + response.code() + " from " + config.crumbUrl());
            }
            var body = response.body();
            String crumb = body == null ? null : body.string();
            if (crumb == null || crumb.isBlank() || crumb.contains("<html")) {
                throw new YFAuthException("Yahoo returned an empty or invalid crumb");
            }
            return Crumb.of(crumb.strip());
        } catch (IOException e) {
            throw new YFAuthException("I/O error while obtaining crumb", e);
        }
    }

    /** Hits {@code fc.yahoo.com} purely so Yahoo sets the session cookie; failures are tolerated. */
    private void seedCookie() {
        var request = new Request.Builder().url(config.cookieUrl()).get().build();
        try (Response response = client.newCall(request).execute()) {
            response.body(); // drain; status (often 404) is irrelevant, the Set-Cookie matters
        } catch (IOException e) {
            throw new YFAuthException("I/O error while seeding Yahoo cookie", e);
        }
    }
}
