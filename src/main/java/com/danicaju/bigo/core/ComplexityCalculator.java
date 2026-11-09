package com.danicaju.bigo.core;

import java.math.BigDecimal;
import java.util.List;

/**
 * Fachada estática y de conveniencia para invocar las capacidades de {@link BigOEngine}.
 */
public final class ComplexityCalculator {

    private static final BigOEngine INSTANCE = new BigOEngine();

    private ComplexityCalculator() {
    }

    public static ComplexityResult calculate(ComplexityClass complexity, long n, BigDecimal timePerOp, TimeUnit unit) {
        return INSTANCE.calculate(complexity, n, timePerOp, unit);
    }

    public static List<ComplexityResult> calculateAll(long n, BigDecimal timePerOp, TimeUnit unit) {
        return INSTANCE.calculateAll(n, timePerOp, unit);
    }

    public static BigOEngine getEngine() {
        return INSTANCE;
    }
}
