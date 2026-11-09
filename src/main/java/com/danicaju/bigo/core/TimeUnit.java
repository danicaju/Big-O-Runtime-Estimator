package com.danicaju.bigo.core;

import java.math.BigDecimal;

/**
 * Unidades de tiempo para el coste por operación atómica.
 */
public enum TimeUnit {

    NANOSECONDS("ns", "Nanosegundos", new BigDecimal("0.000000001")),
    MICROSECONDS("µs", "Microsegundos", new BigDecimal("0.000001")),
    MILLISECONDS("ms", "Milisegundos", new BigDecimal("0.001")),
    SECONDS("s", "Segundos", BigDecimal.ONE);

    private final String symbol;
    private final String displayName;
    private final BigDecimal toSecondsMultiplier;

    TimeUnit(String symbol, String displayName, BigDecimal toSecondsMultiplier) {
        this.symbol = symbol;
        this.displayName = displayName;
        this.toSecondsMultiplier = toSecondsMultiplier;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getToSecondsMultiplier() {
        return toSecondsMultiplier;
    }

    /**
     * Convierte una cantidad dada en esta unidad a segundos.
     */
    public BigDecimal toSeconds(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.multiply(toSecondsMultiplier);
    }

    @Override
    public String toString() {
        return symbol + " (" + displayName + ")";
    }
}
