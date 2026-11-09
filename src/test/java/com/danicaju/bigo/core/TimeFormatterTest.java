package com.danicaju.bigo.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de TimeFormatter")
class TimeFormatterTest {

    @Test
    @DisplayName("Valores nulos o negativos se gestionan de forma segura")
    void testNullAndNegative() {
        assertEquals("0 ns", TimeFormatter.formatSeconds(null));
        assertEquals("0 ns", TimeFormatter.formatSeconds(new BigDecimal("-10")));
        assertEquals("0.000 s", TimeFormatter.formatSeconds(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Formateo de escalas sub-microsegundos (ns)")
    void testNanoseconds() {
        // 50 ns = 0.000000050 s
        String result = TimeFormatter.formatSeconds(new BigDecimal("0.000000050"));
        assertTrue(result.contains("ns"));
        assertEquals("50.000 ns", result);

        // Menor a 0.001 ns
        assertEquals("< 0.001 ns", TimeFormatter.formatSeconds(new BigDecimal("0.0000000000001")));
    }

    @Test
    @DisplayName("Formateo de escalas sub-milisegundos (µs)")
    void testMicroseconds() {
        // 25.5 µs = 0.0000255 s
        String result = TimeFormatter.formatSeconds(new BigDecimal("0.0000255"));
        assertTrue(result.contains("µs"));
        assertEquals("25.500 µs", result);
    }

    @Test
    @DisplayName("Formateo de escalas sub-segundos (ms)")
    void testMilliseconds() {
        // 150 ms = 0.150 s
        String result = TimeFormatter.formatSeconds(new BigDecimal("0.150"));
        assertTrue(result.contains("ms"));
        assertEquals("150.000 ms", result);
    }

    @Test
    @DisplayName("Formateo de segundos regulares")
    void testSeconds() {
        String result = TimeFormatter.formatSeconds(new BigDecimal("42.500"));
        assertTrue(result.contains("s"));
        assertEquals("42.500 s", result);
    }

    @Test
    @DisplayName("Formateo de minutos y horas")
    void testMinutesAndHours() {
        // 90 segundos = 1 min 30.0 s
        String resMins = TimeFormatter.formatSeconds(new BigDecimal("90"));
        assertTrue(resMins.contains("min"));

        // 3660 segundos = 1 h 1 min
        String resHours = TimeFormatter.formatSeconds(new BigDecimal("3660"));
        assertTrue(resHours.contains("h"));
        assertTrue(resHours.contains("min"));
    }

    @Test
    @DisplayName("Formateo de días y semanas")
    void testDaysAndWeeks() {
        // 2 días = 172800 s
        String resDays = TimeFormatter.formatSeconds(new BigDecimal("172800"));
        assertTrue(resDays.contains("días"));

        // 2 semanas = 1209600 s
        String resWeeks = TimeFormatter.formatSeconds(new BigDecimal("1209600"));
        assertTrue(resWeeks.contains("semanas"));
    }

    @Test
    @DisplayName("Formateo de años y siglos")
    void testYearsAndCenturies() {
        // 5 años (5 * 31557600 = 157788000 s)
        String resYears = TimeFormatter.formatSeconds(new BigDecimal("157788000"));
        assertTrue(resYears.contains("años"));

        // 500 años (500 * 31557600 = 15778800000 s)
        String resCenturies = TimeFormatter.formatSeconds(new BigDecimal("15778800000"));
        assertTrue(resCenturies.contains("siglos"));
    }

    @Test
    @DisplayName("Formateo de eras cósmicas superando la edad del Universo (13.8 Ga)")
    void testCosmicEras() {
        // 100 veces la edad del Universo
        BigDecimal universeAgeSec = new BigDecimal("435494880000000000");
        BigDecimal cosmicSec = universeAgeSec.multiply(new BigDecimal("100"));

        String result = TimeFormatter.formatSeconds(cosmicSec);
        assertTrue(result.contains("edad del Universo"), "Debe referenciar la edad cosmológica del Universo");
        assertTrue(result.contains("13.8 Ga"));
    }

    @Test
    @DisplayName("Superíndices Unicode se convierten adecuadamente")
    void testSuperScripts() {
        assertEquals("⁰", TimeFormatter.toSuperscript(0));
        assertEquals("¹²³", TimeFormatter.toSuperscript(123));
        assertEquals("⁻⁵", TimeFormatter.toSuperscript(-5));
    }

    @Test
    @DisplayName("Formateo de número de operaciones con separadores y notación científica")
    void testFormatOperations() {
        assertEquals("0", TimeFormatter.formatOperations(null));
        assertEquals("0", TimeFormatter.formatOperations(BigInteger.ZERO));
        assertEquals("1,000", TimeFormatter.formatOperations(BigInteger.valueOf(1000)));
        assertEquals("1,000,000", TimeFormatter.formatOperations(BigInteger.valueOf(1000000)));

        // Número astronómico con más de 15 dígitos
        BigInteger colossal = BigInteger.TEN.pow(25);
        String formatted = TimeFormatter.formatOperations(colossal);
        assertTrue(formatted.contains("dígitos"), "Debe indicar conteo de dígitos para números astronómicos");
        assertTrue(formatted.contains("10"));
    }
}
