package io.ziggy.yfinance.exception;

/** Raised when Yahoo's cookie/crumb authentication handshake fails. */
public class YFAuthException extends YFinanceException {

    public YFAuthException(String message) {
        super(message);
    }

    public YFAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
