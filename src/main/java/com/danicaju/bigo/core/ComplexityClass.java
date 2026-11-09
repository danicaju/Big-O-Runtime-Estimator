package com.danicaju.bigo.core;

/**
 * Representa las 8 clases canónicas de complejidad asintótica en notación Big-O.
 */
public enum ComplexityClass {

    O_1(
            1,
            "O(1)",
            "\\mathcal{O}(1)",
            "Constante",
            "f(n) = 1",
            EfficiencyLevel.EXCELENTE,
            "El tiempo de ejecución es independiente del tamaño de entrada.",
            "Acceso por índice en arrays, operaciones de pila (push/pop), inserción/búsqueda promedio en tablas hash."
    ),
    O_LOG_N(
            2,
            "O(log n)",
            "\\mathcal{O}(\\log n)",
            "Logarítmica",
            "f(n) = \\log_2(n)",
            EfficiencyLevel.EXCELENTE,
            "El tiempo crece logarítmicamente dividiendo el espacio de búsqueda a la mitad en cada iteración.",
            "Búsqueda binaria, operaciones en árboles binarios auto-balanceados (AVL, Red-Black), búsqueda en montículos."
    ),
    O_N(
            3,
            "O(n)",
            "\\mathcal{O}(n)",
            "Lineal",
            "f(n) = n",
            EfficiencyLevel.BUENO,
            "El tiempo de ejecución escala de forma proporcional directa al tamaño n.",
            "Búsqueda lineal en listas no ordenadas, conteo o suma de elementos en arrays, recorrido de cadenas."
    ),
    O_N_LOG_N(
            4,
            "O(n log n)",
            "\\mathcal{O}(n \\log n)",
            "Linealítmica",
            "f(n) = n \\cdot \\log_2(n)",
            EfficiencyLevel.ACEPTABLE,
            "Complejidad cuasi-lineal típica de algoritmos divide y vencerás óptimos para ordenación por comparación.",
            "Merge Sort, Quick Sort (caso promedio), Heap Sort, TimSort (ordenación estándar en Java y Python)."
    ),
    O_N_2(
            5,
            "O(n²)",
            "\\mathcal{O}(n^2)",
            "Cuadrática",
            "f(n) = n^2",
            EfficiencyLevel.MALO,
            "El tiempo es proporcional al cuadrado de n, frecuentemente originado por dos bucles anidados dependientes.",
            "Bubble Sort, Selection Sort, Insertion Sort, verificación ingenua de duplicados comparando todos contra todos."
    ),
    O_N_3(
            6,
            "O(n³)",
            "\\mathcal{O}(n^3)",
            "Cúbica",
            "f(n) = n^3",
            EfficiencyLevel.MALO,
            "El tiempo crece con el cubo de n, característico de tres bucles anidados sobre la estructura de datos.",
            "Multiplicación ingenua de matrices cuadradas, algoritmo de Floyd-Warshall para caminos mínimos."
    ),
    O_2_N(
            7,
            "O(2ⁿ)",
            "\\mathcal{O}(2^n)",
            "Exponencial",
            "f(n) = 2^n",
            EfficiencyLevel.HORRIBLE,
            "El tiempo se duplica con cada incremento unitario en n; rápidamente intratable para n > 40.",
            "Cálculo recursivo ingenuo de la sucesión de Fibonacci, generación de subconjuntos (potencia), problema de la mochila por fuerza bruta."
    ),
    O_N_FACT(
            8,
            "O(n!)",
            "\\mathcal{O}(n!)",
            "Factorial",
            "f(n) = n!",
            EfficiencyLevel.HORRIBLE,
            "Crecimiento factorial hiper-acelerado; prohibitivo computacionalmente incluso para n moderados (n > 15).",
            "Generación exhaustiva de permutaciones, problema del viajante de comercio (TSP) mediante fuerza bruta."
    );

    private final int id;
    private final String notation;
    private final String latexNotation;
    private final String name;
    private final String formulaDisplay;
    private final EfficiencyLevel efficiency;
    private final String summary;
    private final String realWorldExamples;

    ComplexityClass(int id, String notation, String latexNotation, String name, String formulaDisplay,
                    EfficiencyLevel efficiency, String summary, String realWorldExamples) {
        this.id = id;
        this.notation = notation;
        this.latexNotation = latexNotation;
        this.name = name;
        this.formulaDisplay = formulaDisplay;
        this.efficiency = efficiency;
        this.summary = summary;
        this.realWorldExamples = realWorldExamples;
    }

    public int getId() {
        return id;
    }

    public String getNotation() {
        return notation;
    }

    public String getLatexNotation() {
        return latexNotation;
    }

    public String getName() {
        return name;
    }

    public String getFormulaDisplay() {
        return formulaDisplay;
    }

    public EfficiencyLevel getEfficiency() {
        return efficiency;
    }

    public String getSummary() {
        return summary;
    }

    public String getRealWorldExamples() {
        return realWorldExamples;
    }

    public static ComplexityClass fromId(int id) {
        for (ComplexityClass c : values()) {
            if (c.id == id) {
                return c;
            }
        }
        throw new IllegalArgumentException("Identificador de complejidad no válido: " + id);
    }

    @Override
    public String toString() {
        return notation + " - " + name;
    }
}
