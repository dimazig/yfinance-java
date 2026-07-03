package io.ziggy.yfinance.http;

import io.ziggy.yfinance.valueobject.Crumb;
import java.io.IOException;
import java.util.function.Supplier;
import okhttp3.Interceptor;
import okhttp3.Response;

/** Appends the {@code crumb} query parameter to every outgoing request. */
public final class CrumbInterceptor implements Interceptor {

    private final Supplier<Crumb> crumbSupplier;

    public CrumbInterceptor(Supplier<Crumb> crumbSupplier) {
        this.crumbSupplier = crumbSupplier;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        var original = chain.request();
        var url = original.url().newBuilder()
                .setQueryParameter("crumb", crumbSupplier.get().value())
                .build();
        return chain.proceed(original.newBuilder().url(url).build());
    }
}
