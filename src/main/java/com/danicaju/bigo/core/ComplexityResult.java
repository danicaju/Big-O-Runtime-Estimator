package com.danicaju.bigo.core;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Contenedor inmutable del resultado de cálculo para una clase de complejidad dada.
 */
public record ComplexityResult(
        ComplexityClass complexityClass,
        long inputN,
        BigDecimal operations,
        BigDecimal estimatedSeconds,
        String formattedOperations,
        String formattedTime,
        boolean isApproximation
) {
    /**
     * Devuelve una representación textual resumida para logs o consola.
     */
    public String toConsoleRow() {
        return String.format("%-12s | %-12s | %-10s | %-25s | %s",
                complexityClass.getNotation(),
                complexityClass.getName(),
                complexityClass.getEfficiency().getLabel(),
                formattedOperations,
                formattedTime);
    }
}
