package com.danicaju.bigo.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de ComplexityClass")
class ComplexityClassTest {

    @Test
    @DisplayName("Existen exactamente 8 clases de complejidad canónicas con ids del 1 al 8")
    void testEnumCompletenessAndIds() {
        ComplexityClass[] classes = ComplexityClass.values();
        assertEquals(8, classes.length);

        for (int i = 0; i < classes.length; i++) {
            assertEquals(i + 1, classes[i].getId());
            assertNotNull(classes[i].getNotation());
            assertNotNull(classes[i].getLatexNotation());
            assertNotNull(classes[i].getName());
            assertNotNull(classes[i].getFormulaDisplay());
            assertNotNull(classes[i].getEfficiency());
            assertNotNull(classes[i].getSummary());
            assertNotNull(classes[i].getRealWorldExamples());
        }
    }

    @Test
    @DisplayName("fromId mapea adecuadamente y lanza excepción ante id inexistente")
    void testFromId() {
        assertEquals(ComplexityClass.O_1, ComplexityClass.fromId(1));
        assertEquals(ComplexityClass.O_N_FACT, ComplexityClass.fromId(8));
        assertThrows(IllegalArgumentException.class, () -> ComplexityClass.fromId(0));
        assertThrows(IllegalArgumentException.class, () -> ComplexityClass.fromId(9));
    }

    @Test
    @DisplayName("EfficiencyLevel posee colores y etiquetas no vacíos")
    void testEfficiencyLevels() {
        for (EfficiencyLevel eff : EfficiencyLevel.values()) {
            assertNotNull(eff.getLabel());
            assertNotNull(eff.getHexColor());
            assertNotNull(eff.getColor());
            assertNotNull(eff.getDescription());
        }
    }
}
