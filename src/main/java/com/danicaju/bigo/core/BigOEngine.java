package com.danicaju.bigo.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Motor central de cálculo algorítmico y estimación de tiempos de ejecución Big-O.
 * Diseñado con precisión arbitraria (BigInteger y BigDecimal) para evitar overflow
 * aritmético en funciones de crecimiento exponencial y factorial.
 */
public class BigOEngine {

    public static final long MAX_EXACT_POWER_OF_TWO = 10_000L;
    public static final long MAX_EXACT_FACTORIAL = 5_000L;

    private static final MathContext MC = MathContext.DECIMAL128;
    private static final double LN_2 = Math.log(2.0);
    private static final double LN_10 = Math.log(10.0);
    private static final double LOG10_2 = Math.log10(2.0);

    /**
     * Calcula el resultado de estimación para una clase de complejidad específica.
     *
     * @param complexity Clase de complejidad analizada.
     * @param n          Tamaño de entrada (n >= 0).
     * @param timePerOp  Coste temporal por operación atómica.
     * @param unit       Unidad temporal del parámetro timePerOp.
     * @return Objeto inmutable con los resultados calculados y formateados.
     */
    public ComplexityResult calculate(ComplexityClass complexity, long n, BigDecimal timePerOp, TimeUnit unit) {
        if (n < 0) {
            throw new IllegalArgumentException("El tamaño de entrada n debe ser mayor o igual a 0. Recibido: " + n);
        }
        if (timePerOp == null || timePerOp.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El tiempo por operación debe ser mayor o igual a cero.");
        }
        if (unit == null) {
            unit = TimeUnit.MILLISECONDS;
        }

        boolean[] isApprox = new boolean[]{false};
        BigDecimal operations = computeOperations(complexity, n, isApprox);
        BigDecimal timeInSeconds = operations.multiply(unit.toSeconds(timePerOp), MC);

        String formattedOps = formatOperationsString(complexity, n, operations, isApprox[0]);
        String formattedTime = TimeFormatter.formatSeconds(timeInSeconds);

        return new ComplexityResult(
                complexity,
                n,
                operations,
                timeInSeconds,
                formattedOps,
                formattedTime,
                isApprox[0]
        );
    }

    /**
     * Calcula simultáneamente las 8 clases de complejidad para un tamaño n dado.
     *
     * @param n         Tamaño de entrada.
     * @param timePerOp Tiempo por operación.
     * @param unit      Unidad del tiempo por operación.
     * @return Lista inmutable ordenada de los 8 resultados.
     */
    public List<ComplexityResult> calculateAll(long n, BigDecimal timePerOp, TimeUnit unit) {
        List<ComplexityResult> list = new ArrayList<>();
        for (ComplexityClass cc : ComplexityClass.values()) {
            list.add(calculate(cc, n, timePerOp, unit));
        }
        return List.copyOf(list);
    }

    /**
     * Calcula el número de operaciones teóricas para una complejidad y tamaño n dados.
     */
    public BigDecimal computeOperations(ComplexityClass complexity, long n, boolean[] isApproxHolder) {
        if (n < 0) {
            throw new IllegalArgumentException("n no puede ser negativo: " + n);
        }

        return switch (complexity) {
            case O_1 -> BigDecimal.ONE;

            case O_LOG_N -> {
                if (n <= 1) {
                    yield BigDecimal.ONE;
                }
                // Si es potencia exacta de 2, devolver entero exacto sin error de punto flotante
                if ((n & (n - 1)) == 0) {
                    int power = 63 - Long.numberOfLeadingZeros(n);
                    yield BigDecimal.valueOf(power);
                }
                double log2 = Math.log(n) / LN_2;
                yield BigDecimal.valueOf(log2).setScale(4, RoundingMode.HALF_UP);
            }

            case O_N -> BigDecimal.valueOf(n);

            case O_N_LOG_N -> {
                if (n <= 1) {
                    yield (n == 0) ? BigDecimal.ZERO : BigDecimal.ONE;
                }
                if ((n & (n - 1)) == 0) {
                    int power = 63 - Long.numberOfLeadingZeros(n);
                    yield BigDecimal.valueOf(n).multiply(BigDecimal.valueOf(power));
                }
                double log2 = Math.log(n) / LN_2;
                BigDecimal log2Bd = BigDecimal.valueOf(log2);
                yield BigDecimal.valueOf(n).multiply(log2Bd, MC).setScale(4, RoundingMode.HALF_UP);
            }

            case O_N_2 -> {
                BigInteger bi = BigInteger.valueOf(n);
                yield new BigDecimal(bi.multiply(bi));
            }

            case O_N_3 -> {
                BigInteger bi = BigInteger.valueOf(n);
                yield new BigDecimal(bi.pow(3));
            }

            case O_2_N -> {
                if (n == 0) {
                    yield BigDecimal.ONE;
                }
                if (n <= MAX_EXACT_POWER_OF_TWO) {
                    BigInteger bi = BigInteger.TWO.pow((int) n);
                    yield new BigDecimal(bi);
                } else {
                    if (isApproxHolder != null && isApproxHolder.length > 0) {
                        isApproxHolder[0] = true;
                    }
                    // log10(2^n) = n * log10(2)
                    double totalLog10 = n * LOG10_2;
                    double exp = Math.floor(totalLog10);
                    double mantissa = Math.pow(10, totalLog10 - exp);
                    yield BigDecimal.valueOf(mantissa).multiply(BigDecimal.TEN.pow((int) Math.min(exp, 10000), MC));
                }
            }

            case O_N_FACT -> {
                if (n <= 1) {
                    yield BigDecimal.ONE;
                }
                if (n <= MAX_EXACT_FACTORIAL) {
                    BigInteger result = BigInteger.ONE;
                    for (long i = 2; i <= n; i++) {
                        result = result.multiply(BigInteger.valueOf(i));
                    }
                    yield new BigDecimal(result);
                } else {
                    if (isApproxHolder != null && isApproxHolder.length > 0) {
                        isApproxHolder[0] = true;
                    }
                    // Aproximación de Stirling: ln(n!) ~ n*ln(n) - n + 0.5*ln(2*pi*n)
                    double nd = (double) n;
                    double lnFact = nd * Math.log(nd) - nd + 0.5 * Math.log(2 * Math.PI * nd);
                    double log10Fact = lnFact / LN_10;
                    double exp = Math.floor(log10Fact);
                    double mantissa = Math.pow(10, log10Fact - exp);
                    yield BigDecimal.valueOf(mantissa).multiply(BigDecimal.TEN.pow((int) Math.min(exp, 10000), MC));
                }
            }
        };
    }

    private String formatOperationsString(ComplexityClass complexity, long n, BigDecimal ops, boolean isApprox) {
        if (complexity == ComplexityClass.O_LOG_N || complexity == ComplexityClass.O_N_LOG_N) {
            // Si tiene parte decimal
            if (ops.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
                if (ops.compareTo(new BigDecimal("1000000")) < 0) {
                    return String.format(Locale.US, "%,.2f", ops.doubleValue());
                }
            }
        }

        if (isApprox) {
            // Representación asintótica para órdenes colosales
            if (complexity == ComplexityClass.O_2_N) {
                double totalLog10 = n * LOG10_2;
                long exp = (long) Math.floor(totalLog10);
                double mantissa = Math.pow(10, totalLog10 - exp);
                return String.format(Locale.US, "~%.3f × 10%s (%,d dígitos)", mantissa, TimeFormatter.toSuperscript((int) exp), exp + 1);
            } else if (complexity == ComplexityClass.O_N_FACT) {
                double nd = (double) n;
                double lnFact = nd * Math.log(nd) - nd + 0.5 * Math.log(2 * Math.PI * nd);
                double log10Fact = lnFact / LN_10;
                long exp = (long) Math.floor(log10Fact);
                double mantissa = Math.pow(10, log10Fact - exp);
                return String.format(Locale.US, "~%.3f × 10%s (%,d dígitos)", mantissa, TimeFormatter.toSuperscript((int) exp), exp + 1);
            }
        }

        return TimeFormatter.formatOperations(ops.toBigInteger());
    }
}
