package com.danicaju.bigo.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de TimeUnit")
class TimeUnitConverterTest {

    @Test
    @DisplayName("Conversión de unidades temporales a segundos con BigDecimal")
    void testToSeconds() {
        // 1.000.000.000 ns = 1 s
        assertEquals(new BigDecimal("1.000000000"),
                TimeUnit.NANOSECONDS.toSeconds(new BigDecimal("1000000000")));

        // 1.000.000 µs = 1 s
        assertEquals(new BigDecimal("1.000000"),
                TimeUnit.MICROSECONDS.toSeconds(new BigDecimal("1000000")));

        // 1.000 ms = 1 s
        assertEquals(new BigDecimal("1.000"),
                TimeUnit.MILLISECONDS.toSeconds(new BigDecimal("1000")));

        // 5 s = 5 s
        assertEquals(new BigDecimal("5"),
                TimeUnit.SECONDS.toSeconds(new BigDecimal("5")));
    }

    @Test
    @DisplayName("Manejo de valor nulo retorna cero")
    void testNullValueReturnsZero() {
        assertEquals(BigDecimal.ZERO, TimeUnit.MILLISECONDS.toSeconds(null));
    }
}
