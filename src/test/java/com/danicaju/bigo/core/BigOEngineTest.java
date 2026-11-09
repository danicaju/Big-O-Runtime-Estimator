package com.danicaju.bigo.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias del Motor BigOEngine")
class BigOEngineTest {

    private BigOEngine engine;

    @BeforeEach
    void setUp() {
        engine = new BigOEngine();
    }

    @Test
    @DisplayName("O(1) siempre produce 1 operación para cualquier n >= 0")
    void testConstantComplexity() {
        long[] testCases = {0, 1, 10, 100, 1_000, 1_000_000};
        for (long n : testCases) {
            BigDecimal ops = engine.computeOperations(ComplexityClass.O_1, n, null);
            assertEquals(BigDecimal.ONE, ops, "O(1) debe ser siempre 1");
        }
    }

    @Test
    @DisplayName("O(log n) calcula valores logarítmicos exactos en potencias de 2")
    void testLogNExactPowersOfTwo() {
        assertEquals(BigDecimal.ONE, engine.computeOperations(ComplexityClass.O_LOG_N, 1, null));
        assertEquals(BigDecimal.valueOf(1), engine.computeOperations(ComplexityClass.O_LOG_N, 2, null));
        assertEquals(BigDecimal.valueOf(2), engine.computeOperations(ComplexityClass.O_LOG_N, 4, null));
        assertEquals(BigDecimal.valueOf(4), engine.computeOperations(ComplexityClass.O_LOG_N, 16, null));
        assertEquals(BigDecimal.valueOf(10), engine.computeOperations(ComplexityClass.O_LOG_N, 1024, null));
        assertEquals(BigDecimal.valueOf(20), engine.computeOperations(ComplexityClass.O_LOG_N, 1048576, null));
    }

    @Test
    @DisplayName("O(n) escala linealmente con n")
    void testLinearComplexity() {
        assertEquals(BigDecimal.ZERO, engine.computeOperations(ComplexityClass.O_N, 0, null));
        assertEquals(BigDecimal.valueOf(100), engine.computeOperations(ComplexityClass.O_N, 100, null));
        assertEquals(BigDecimal.valueOf(1_000_000), engine.computeOperations(ComplexityClass.O_N, 1_000_000, null));
    }

    @Test
    @DisplayName("O(n log n) calcula valores exactos en potencias de 2")
    void testNLogNExact() {
        assertEquals(BigDecimal.valueOf(64), engine.computeOperations(ComplexityClass.O_N_LOG_N, 16, null)); // 16 * 4
        assertEquals(BigDecimal.valueOf(10240), engine.computeOperations(ComplexityClass.O_N_LOG_N, 1024, null)); // 1024 * 10
    }

    @Test
    @DisplayName("O(n²) y O(n³) calculan potencias polinomiales con BigInteger sin desbordamiento")
    void testPolynomialComplexity() {
        // n = 1,000,000 -> n^2 = 10^12
        BigDecimal n2 = engine.computeOperations(ComplexityClass.O_N_2, 1_000_000L, null);
        assertEquals(new BigDecimal("1000000000000"), n2);

        // n = 10,000 -> n^3 = 10^12
        BigDecimal n3 = engine.computeOperations(ComplexityClass.O_N_3, 10_000L, null);
        assertEquals(new BigDecimal("1000000000000"), n3);
    }

    @Test
    @DisplayName("O(2ⁿ) con n=100 genera el valor exacto de 2^100 con BigInteger")
    void testExponentialN100() {
        BigDecimal ops = engine.computeOperations(ComplexityClass.O_2_N, 100, null);
        BigInteger expected = BigInteger.TWO.pow(100);
        // 2^100 = 1267650600228229401496703205376
        assertEquals(new BigDecimal(expected), ops);
        assertEquals(31, ops.toBigInteger().toString().length());
    }

    @Test
    @DisplayName("O(n!) con n=5 y n=10 genera factoriales exactos")
    void testFactorialSmallN() {
        assertEquals(new BigDecimal("120"), engine.computeOperations(ComplexityClass.O_N_FACT, 5, null));
        assertEquals(new BigDecimal("3628800"), engine.computeOperations(ComplexityClass.O_N_FACT, 10, null));
    }

    @Test
    @DisplayName("O(n!) con n=50 no produce overflow y calcula sus 65 dígitos exactos")
    void testFactorialN50() {
        BigDecimal ops = engine.computeOperations(ComplexityClass.O_N_FACT, 50, null);
        BigInteger expected = BigInteger.ONE;
        for (long i = 2; i <= 50; i++) {
            expected = expected.multiply(BigInteger.valueOf(i));
        }
        assertEquals(new BigDecimal(expected), ops);
        assertEquals(65, ops.toBigInteger().toString().length());
    }

    @Test
    @DisplayName("Valores colosales para O(2ⁿ) y O(n!) calculan órdenes asintóticos reales sin truncamiento a 10.000 ni agotar memoria")
    void testColossalValuesSafety() {
        boolean[] isApprox = new boolean[]{false};
        BigDecimal opsExp = engine.computeOperations(ComplexityClass.O_2_N, 50_000L, isApprox);
        assertTrue(isApprox[0], "Debe activar el modo de aproximación asintótica para n > 10,000");
        assertNotNull(opsExp);
        // 2^50,000 tiene 15,052 dígitos (exponente 15,051)
        int exp2n = opsExp.precision() - opsExp.scale() - 1;
        assertEquals(15051, exp2n, "El exponente no puede estar truncado a 10,000");

        isApprox[0] = false;
        BigDecimal opsFact = engine.computeOperations(ComplexityClass.O_N_FACT, 20_000L, isApprox);
        assertTrue(isApprox[0], "Debe activar el modo de aproximación asintótica para n > 5,000");
        assertNotNull(opsFact);
        // 20,000! tiene 77,338 dígitos (exponente 77,337)
        int expFact = opsFact.precision() - opsFact.scale() - 1;
        assertEquals(77337, expFact, "El exponente factorial no puede estar truncado a 10,000");
    }

    @Test
    @DisplayName("Gestión coherente y matemáticamente rigurosa para n = 0 en todas las clases")
    void testNZeroHandling() {
        assertEquals(BigDecimal.ONE, engine.computeOperations(ComplexityClass.O_1, 0, null));
        assertEquals(BigDecimal.ZERO, engine.computeOperations(ComplexityClass.O_LOG_N, 0, null), "log(0) algorítmico sobre colección vacía es 0");
        assertEquals(BigDecimal.ZERO, engine.computeOperations(ComplexityClass.O_N, 0, null));
        assertEquals(BigDecimal.ZERO, engine.computeOperations(ComplexityClass.O_N_LOG_N, 0, null));
        assertEquals(BigDecimal.ZERO, engine.computeOperations(ComplexityClass.O_N_2, 0, null));
        assertEquals(BigDecimal.ZERO, engine.computeOperations(ComplexityClass.O_N_3, 0, null));
        assertEquals(BigDecimal.ONE, engine.computeOperations(ComplexityClass.O_2_N, 0, null)); // 2^0 = 1
        assertEquals(BigDecimal.ONE, engine.computeOperations(ComplexityClass.O_N_FACT, 0, null)); // 0! = 1
    }

    @Test
    @DisplayName("Evaluación completa de presets máximos (n = 100.000 y n = 1.000.000) es instantánea y consistente")
    void testFullCalculationPresetsLargeN() {
        // n = 100.000 (preset 5 de la GUI)
        List<ComplexityResult> res100k = engine.calculateAll(100_000L, new BigDecimal("0.001"), TimeUnit.MILLISECONDS);
        assertEquals(8, res100k.size());
        ComplexityResult fact100k = res100k.get(7);
        assertTrue(fact100k.formattedOperations().contains("456,574 dígitos"), "100.000! debe tener 456.574 dígitos");
        assertTrue(fact100k.formattedTime().contains("edad del Universo"), "El tiempo estimado debe reflejar eras cósmicas");

        // n = 1.000.000 (preset 6 de la GUI)
        List<ComplexityResult> res1M = engine.calculateAll(1_000_000L, new BigDecimal("1"), TimeUnit.NANOSECONDS);
        assertEquals(8, res1M.size());
        ComplexityResult fact1M = res1M.get(7);
        assertTrue(fact1M.formattedOperations().contains("5,565,709 dígitos"), "1.000.000! debe tener ~5.5 millones de dígitos");
        assertFalse(fact1M.formattedTime().contains("10⁹⁹⁹"), "El tiempo no debe sufrir artefactos de capping en 10.000");
    }

    @Test
    @DisplayName("Lanzar excepción ante tamaño n negativo")
    void testNegativeNThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                engine.calculate(ComplexityClass.O_N, -1, BigDecimal.ONE, TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("Lanzar excepción ante tiempo por operación negativo")
    void testNegativeTimePerOpThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                engine.calculate(ComplexityClass.O_N, 10, new BigDecimal("-0.5"), TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("calculateAll devuelve los 8 resultados canónicos en orden")
    void testCalculateAllReturnsEightClasses() {
        List<ComplexityResult> results = engine.calculateAll(100, new BigDecimal("0.001"), TimeUnit.MILLISECONDS);
        assertEquals(8, results.size());
        assertEquals(ComplexityClass.O_1, results.get(0).complexityClass());
        assertEquals(ComplexityClass.O_LOG_N, results.get(1).complexityClass());
        assertEquals(ComplexityClass.O_N, results.get(2).complexityClass());
        assertEquals(ComplexityClass.O_N_LOG_N, results.get(3).complexityClass());
        assertEquals(ComplexityClass.O_N_2, results.get(4).complexityClass());
        assertEquals(ComplexityClass.O_N_3, results.get(5).complexityClass());
        assertEquals(ComplexityClass.O_2_N, results.get(6).complexityClass());
        assertEquals(ComplexityClass.O_N_FACT, results.get(7).complexityClass());
    }

    @Test
    @DisplayName("Fachada ComplexityCalculator opera de forma consistente")
    void testComplexityCalculatorFacade() {
        ComplexityResult res = ComplexityCalculator.calculate(ComplexityClass.O_1, 100, BigDecimal.ONE, TimeUnit.SECONDS);
        assertEquals(BigDecimal.ONE, res.operations());
        assertEquals(BigDecimal.ONE, res.estimatedSeconds());
    }
}
