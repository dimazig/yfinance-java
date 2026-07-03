package io.ziggy.yfinance.model;

import java.net.URI;
import java.time.Instant;

/** An SEC filing reference. */
public record SecFiling(Instant date, String type, String title, URI url) {}
