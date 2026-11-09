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
    @DisplayName("Valores colosales para O(2ⁿ) y O(n!) activan modo asintótico sin agotar memoria ni colgarse")
    void testColossalValuesSafety() {
        boolean[] isApprox = new boolean[]{false};
        BigDecimal opsExp = engine.computeOperations(ComplexityClass.O_2_N, 50_000L, isApprox);
        assertTrue(isApprox[0], "Debe activar el modo de aproximación asintótica para n > 10,000");
        assertNotNull(opsExp);

        isApprox[0] = false;
        BigDecimal opsFact = engine.computeOperations(ComplexityClass.O_N_FACT, 20_000L, isApprox);
        assertTrue(isApprox[0], "Debe activar el modo de aproximación asintótica para n > 5,000");
        assertNotNull(opsFact);
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
