package io.ziggy.yfinance.exception;

/** Base type for all yfinance errors. */
public class YFinanceException extends RuntimeException {

    public YFinanceException(String message) {
        super(message);
    }

    public YFinanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
