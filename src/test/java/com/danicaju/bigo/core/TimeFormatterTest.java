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

    @Test
    @DisplayName("Formateo de cientos de miles y millones de años sin pérdida de precisión ni gramática incorrecta")
    void testThousandsAndMillionsOfYears() {
        // 150.000 años no debe formatearse jamás como '0 millones de años'
        BigDecimal sec150k = new BigDecimal("31557600").multiply(new BigDecimal("150000"));
        String res150k = TimeFormatter.formatSeconds(sec150k);
        assertFalse(res150k.contains("0 millones"), "150.000 años no puede ser '0 millones'");
        assertTrue(res150k.contains("siglos") || res150k.contains("años"));

        // 500.000 años no debe ser '0 millones de años'
        BigDecimal sec500k = new BigDecimal("31557600").multiply(new BigDecimal("500000"));
        String res500k = TimeFormatter.formatSeconds(sec500k);
        assertFalse(res500k.contains("0 millones"), "500.000 años no puede ser '0 millones'");

        // 1.000.000 años: singular gramatical '1.00 millón'
        BigDecimal sec1M = new BigDecimal("31557600").multiply(new BigDecimal("1000000"));
        String res1M = TimeFormatter.formatSeconds(sec1M);
        assertTrue(res1M.contains("1.00 millón de años"));

        // 1.500.000 años: 1.50 millones de años (no truncar a 1)
        BigDecimal sec1_5M = new BigDecimal("31557600").multiply(new BigDecimal("1500000"));
        String res1_5M = TimeFormatter.formatSeconds(sec1_5M);
        assertTrue(res1_5M.contains("1.50 millones de años"));
    }

    @Test
    @DisplayName("Múltiplos pequeños de la edad cósmica evitan superíndice redundante 10⁰")
    void testSmallCosmicMultiplesNoZeroExponent() {
        BigDecimal universeAgeSec = new BigDecimal("435494880000000000");
        BigDecimal cosmicSec = universeAgeSec.multiply(new BigDecimal("2.5"));
        String result = TimeFormatter.formatSeconds(cosmicSec);

        assertTrue(result.contains("2.50 × edad del Universo (13.8 Ga)"));
        assertFalse(result.contains("10⁰"), "No debe contener 10⁰ para múltiplos directos legibles");
    }

    @Test
    @DisplayName("toPrettyScientific procesa escalas colosales con O(1) memoria")
    void testToPrettyScientificMassiveExponent() {
        BigDecimal massive = new BigDecimal("8.263").scaleByPowerOfTen(5565708);
        String pretty = TimeFormatter.toPrettyScientific(massive);
        assertEquals("8.263 × 10⁵⁵⁶⁵⁷⁰⁸", pretty);
    }
}
