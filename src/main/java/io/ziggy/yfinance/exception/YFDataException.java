package io.ziggy.yfinance.exception;

/** Raised when Yahoo returns an error envelope or unparseable/empty data. */
public class YFDataException extends YFinanceException {

    public YFDataException(String message) {
        super(message);
    }

    public YFDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
