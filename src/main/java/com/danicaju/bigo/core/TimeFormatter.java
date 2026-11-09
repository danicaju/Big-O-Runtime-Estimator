package com.danicaju.bigo.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Formateador de tiempo y magnitudes algorítmicas con soporte para escalas
 * desde sub-nanosegundos hasta eras cosmológicas (>13.8 Ga).
 */
public final class TimeFormatter {

    private static final BigDecimal SECONDS_PER_MINUTE = new BigDecimal("60");
    private static final BigDecimal SECONDS_PER_HOUR = new BigDecimal("3600");
    private static final BigDecimal SECONDS_PER_DAY = new BigDecimal("86400");
    private static final BigDecimal SECONDS_PER_WEEK = new BigDecimal("604800");
    private static final BigDecimal SECONDS_PER_YEAR = new BigDecimal("31557600"); // Año juliano (365.25 días)
    private static final BigDecimal SECONDS_PER_CENTURY = new BigDecimal("3155760000");
    private static final BigDecimal SECONDS_AGE_OF_UNIVERSE = new BigDecimal("435494880000000000"); // ~13.8 Ga

    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    private TimeFormatter() {
        // Clase de utilidades estáticas
    }

    /**
     * Formatea una duración en segundos a una representación legible con unidad óptima.
     *
     * @param seconds Duración en segundos (BigDecimal para soporte de magnitudes masivas).
     * @return Cadena formateada amigable.
     */
    public static String formatSeconds(BigDecimal seconds) {
        if (seconds == null || seconds.compareTo(BigDecimal.ZERO) < 0) {
            return "0 ns";
        }
        if (seconds.compareTo(BigDecimal.ZERO) == 0) {
            return "0.000 s";
        }

        // Sub-microsegundos: < 1 µs (< 1e-6 s)
        if (seconds.compareTo(new BigDecimal("0.000001")) < 0) {
            BigDecimal nanos = seconds.multiply(new BigDecimal("1000000000"), MC);
            if (nanos.compareTo(new BigDecimal("0.001")) < 0) {
                return "< 0.001 ns";
            }
            return String.format(Locale.US, "%.3f ns", nanos.doubleValue());
        }

        // Sub-milisegundos: < 1 ms (< 1e-3 s)
        if (seconds.compareTo(new BigDecimal("0.001")) < 0) {
            BigDecimal micros = seconds.multiply(new BigDecimal("1000000"), MC);
            return String.format(Locale.US, "%.3f µs", micros.doubleValue());
        }

        // Sub-segundos: < 1 s
        if (seconds.compareTo(BigDecimal.ONE) < 0) {
            BigDecimal millis = seconds.multiply(new BigDecimal("1000"), MC);
            return String.format(Locale.US, "%.3f ms", millis.doubleValue());
        }

        // Segundos: < 60 s
        if (seconds.compareTo(SECONDS_PER_MINUTE) < 0) {
            return String.format(Locale.US, "%.3f s", seconds.doubleValue());
        }

        // Minutos: < 1 hora
        if (seconds.compareTo(SECONDS_PER_HOUR) < 0) {
            long totalSec = seconds.longValue();
            long mins = totalSec / 60;
            double remSec = seconds.remainder(SECONDS_PER_MINUTE).doubleValue();
            return String.format(Locale.US, "%d min %.1f s", mins, remSec);
        }

        // Horas: < 1 día
        if (seconds.compareTo(SECONDS_PER_DAY) < 0) {
            long totalSec = seconds.longValue();
            long hours = totalSec / 3600;
            long mins = (totalSec % 3600) / 60;
            return String.format(Locale.US, "%d h %d min", hours, mins);
        }

        // Días: < 1 semana (7 días)
        if (seconds.compareTo(SECONDS_PER_WEEK) < 0) {
            double days = seconds.divide(SECONDS_PER_DAY, 2, RoundingMode.HALF_UP).doubleValue();
            long d = seconds.longValue() / 86400;
            long h = (seconds.longValue() % 86400) / 3600;
            return String.format(Locale.US, "%.2f días (%d d %d h)", days, d, h);
        }

        // Semanas: < 1 año
        if (seconds.compareTo(SECONDS_PER_YEAR) < 0) {
            double weeks = seconds.divide(SECONDS_PER_WEEK, 2, RoundingMode.HALF_UP).doubleValue();
            double days = seconds.divide(SECONDS_PER_DAY, 1, RoundingMode.HALF_UP).doubleValue();
            return String.format(Locale.US, "%.2f semanas (~%.1f días)", weeks, days);
        }

        // Años: < 100 años (1 siglo)
        if (seconds.compareTo(SECONDS_PER_CENTURY) < 0) {
            double years = seconds.divide(SECONDS_PER_YEAR, 2, RoundingMode.HALF_UP).doubleValue();
            return String.format(Locale.US, "%.2f años", years);
        }

        // Siglos: < 100.000 años
        BigDecimal years = seconds.divide(SECONDS_PER_YEAR, MC);
        if (years.compareTo(new BigDecimal("100000")) < 0) {
            double centuries = years.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP).doubleValue();
            return String.format(Locale.US, "%.2f siglos (~%,d años)", centuries, years.longValue());
        }

        // Millones de años hasta la edad del Universo (~13.8 Ga)
        if (seconds.compareTo(SECONDS_AGE_OF_UNIVERSE) < 0) {
            BigDecimal millions = years.divide(new BigDecimal("1000000"), 2, RoundingMode.HALF_UP);
            return String.format(Locale.US, "%s millones de años", formatWithThousands(millions.toBigInteger()));
        }

        // Escalas cósmicas (> 13.8 mil millones de años = Edad del Universo)
        BigDecimal universeAges = seconds.divide(SECONDS_AGE_OF_UNIVERSE, 2, RoundingMode.HALF_UP);
        return formatCosmicEra(years, universeAges);
    }

    private static String formatCosmicEra(BigDecimal years, BigDecimal universeAges) {
        String formattedYears = toPrettyScientific(years) + " años";
        String formattedEras = toPrettyScientific(universeAges) + " × edad del Universo (13.8 Ga)";
        return formattedYears + " (~" + formattedEras + ")";
    }

    /**
     * Formatea un número de operaciones enteras en formato legible con separadores o notación científica unicode.
     */
    public static String formatOperations(BigInteger ops) {
        if (ops == null) {
            return "0";
        }
        if (ops.compareTo(BigInteger.ZERO) == 0) {
            return "0";
        }

        // Hasta 1 billón (10^12) o 16 dígitos, mostramos con separadores de miles
        if (ops.toString().length() <= 12) {
            return formatWithThousands(ops);
        }

        // Para números astronómicos, combinamos notación científica Unicode elegante y conteo de dígitos
        return toPrettyScientific(new BigDecimal(ops)) + " (" + ops.toString().length() + " dígitos)";
    }

    /**
     * Formateo con separadores de miles estándar.
     */
    public static String formatWithThousands(BigInteger value) {
        if (value == null) {
            return "0";
        }
        return String.format(Locale.US, "%,d", value);
    }

    /**
     * Convierte un BigDecimal a notación científica con superíndices Unicode (ej. 1.25 × 10¹⁴).
     */
    public static String toPrettyScientific(BigDecimal val) {
        if (val == null || val.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        // Usar formato científico estándar y transformar
        String eng = String.format(Locale.US, "%.3e", val.doubleValue());
        if (val.compareTo(new BigDecimal(Double.MAX_VALUE)) > 0) {
            // Manejar valores que exceden Double.MAX_VALUE
            BigInteger bi = val.toBigInteger();
            String s = bi.toString();
            int exp = s.length() - 1;
            String mantissa = s.charAt(0) + "." + (s.length() > 1 ? s.substring(1, Math.min(4, s.length())) : "000");
            return mantissa + " × 10" + toSuperscript(exp);
        }

        int eIndex = eng.indexOf('e');
        if (eIndex == -1) {
            eIndex = eng.indexOf('E');
        }
        if (eIndex != -1) {
            String mantissa = eng.substring(0, eIndex);
            int exp = Integer.parseInt(eng.substring(eIndex + 1));
            return mantissa + " × 10" + toSuperscript(exp);
        }
        return eng;
    }

    /**
     * Convierte un exponente entero a superíndices Unicode.
     */
    public static String toSuperscript(int exponent) {
        String s = Integer.toString(exponent);
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '-' -> sb.append('⁻');
                case '+' -> sb.append('⁺');
                case '0' -> sb.append('⁰');
                case '1' -> sb.append('¹');
                case '2' -> sb.append('²');
                case '3' -> sb.append('³');
                case '4' -> sb.append('⁴');
                case '5' -> sb.append('⁵');
                case '6' -> sb.append('⁶');
                case '7' -> sb.append('⁷');
                case '8' -> sb.append('⁸');
                case '9' -> sb.append('⁹');
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
