package com.danicaju.bigo.core;

import java.awt.Color;

/**
 * Nivel de eficiencia cualitativo para una clase de complejidad algorítmica.
 */
public enum EfficiencyLevel {

    EXCELENTE("Excelente", "#22C55E", new Color(34, 197, 94), "Rendimiento óptimo e instantáneo para prácticamente cualquier tamaño de entrada."),
    BUENO("Bueno", "#84CC16", new Color(132, 204, 22), "Excelente escalabilidad con degradación mínima."),
    ACEPTABLE("Aceptable", "#EAB308", new Color(234, 179, 8), "Escalabilidad adecuada para conjuntos de datos grandes o moderados."),
    MALO("Malo", "#F97316", new Color(249, 115, 22), "Crecimiento rápido; se vuelve costoso o inasumible con entradas medianas/grandes."),
    HORRIBLE("Horrible", "#EF4444", new Color(239, 68, 68), "Intratable; solo ejecutable para valores extremadamente pequeños de n.");

    private final String label;
    private final String hexColor;
    private final Color color;
    private final String description;

    EfficiencyLevel(String label, String hexColor, Color color, String description) {
        this.label = label;
        this.hexColor = hexColor;
        this.color = color;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getHexColor() {
        return hexColor;
    }

    public Color getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return label;
    }
}
