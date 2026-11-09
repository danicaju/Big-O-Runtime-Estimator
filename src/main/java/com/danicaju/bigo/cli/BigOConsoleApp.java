package com.danicaju.bigo.cli;

import com.danicaju.bigo.core.BigOEngine;
import com.danicaju.bigo.core.ComplexityClass;
import com.danicaju.bigo.core.ComplexityResult;
import com.danicaju.bigo.core.TimeUnit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Cliente de consola (CLI) interactivo para el estimador de runtime Big-O.
 */
public class BigOConsoleApp {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String BLUE = "\u001B[34m";

    private final BigOEngine engine = new BigOEngine();
    private final Scanner scanner = new Scanner(System.in);

    private BigDecimal timePerOp = new BigDecimal("0.001"); // 0.001 ms = 1 µs por defecto
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    public static void main(String[] args) {
        BigOConsoleApp app = new BigOConsoleApp();
        app.run();
    }

    public void run() {
        printBanner();

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print(CYAN + "Selecciona una opción [1-5]: " + RESET);
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> handleCompareAll();
                case "2" -> handleSingleComplexity();
                case "3" -> handleConfigureTimePerOp();
                case "4" -> handleShowTheoreticalGuide();
                case "5", "q", "exit" -> {
                    System.out.println(GREEN + "\n¡Gracias por utilizar Big-O Runtime Estimator! Hasta pronto." + RESET);
                    running = false;
                }
                default -> System.out.println(RED + "Opción inválida. Introduce un número del 1 al 5." + RESET);
            }
            if (running) {
                System.out.println();
            }
        }
    }

    private void printBanner() {
        System.out.println(BOLD + CYAN + "================================================================================" + RESET);
        System.out.println(BOLD + CYAN + "                   BIG-O RUNTIME ESTIMATOR — CONSOLA INTERACTIVA                 " + RESET);
        System.out.println(CYAN + "          Motor de precisión BigInteger/BigDecimal & Formateador Temporal        " + RESET);
        System.out.println(BOLD + CYAN + "================================================================================" + RESET);
    }

    private void printMenu() {
        System.out.println(BOLD + "Configuración actual:" + RESET + " Tiempo/op = " + GREEN + timePerOp + " " + timeUnit.getSymbol() + RESET);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" 1. Tabla comparativa simultánea de las 8 complejidades para un n");
        System.out.println(" 2. Estimación detallada de una complejidad individual");
        System.out.println(" 3. Cambiar tiempo por operación atómica (" + timeUnit.getSymbol() + ")");
        System.out.println(" 4. Consultar guía teórica de complejidades y ejemplos reales");
        System.out.println(" 5. Salir");
        System.out.println("--------------------------------------------------------------------------------");
    }

    private void handleCompareAll() {
        long n = askInputN();
        if (n < 0) return;

        System.out.println("\n" + BOLD + "Resultados para n = " + String.format(Locale.US, "%,d", n) + " (Tiempo/op: " + timePerOp + " " + timeUnit.getSymbol() + "):" + RESET);
        printSeparator();
        System.out.printf(BOLD + "%-12s | %-13s | %-10s | %-34s | %s%n" + RESET,
                "Complejidad", "Nombre", "Eficiencia", "Operaciones f(n)", "Tiempo Estimado");
        printSeparator();

        List<ComplexityResult> results = engine.calculateAll(n, timePerOp, timeUnit);
        for (ComplexityResult res : results) {
            String effColor = switch (res.complexityClass().getEfficiency()) {
                case EXCELENTE, BUENO -> GREEN;
                case ACEPTABLE -> YELLOW;
                case MALO, HORRIBLE -> RED;
            };

            System.out.printf("%-12s | %-13s | " + effColor + "%-10s" + RESET + " | %-34s | %s%n",
                res.complexityClass().getNotation(),
                res.complexityClass().getName(),
                res.complexityClass().getEfficiency().getLabel(),
                res.formattedOperations(),
                res.formattedTime());
        }
        printSeparator();
    }

    private void handleSingleComplexity() {
        System.out.println("\nSelecciona la clase de complejidad:");
        ComplexityClass[] classes = ComplexityClass.values();
        for (int i = 0; i < classes.length; i++) {
            System.out.printf("  %d. %-10s (%s)%n", i + 1, classes[i].getNotation(), classes[i].getName());
        }

        System.out.print(CYAN + "Elige clase [1-8]: " + RESET);
        String choice = scanner.nextLine().trim();
        int idx;
        try {
            idx = Integer.parseInt(choice) - 1;
            if (idx < 0 || idx >= classes.length) {
                System.out.println(RED + "Opción fuera de rango." + RESET);
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "Entrada inválida." + RESET);
            return;
        }

        ComplexityClass selected = classes[idx];
        long n = askInputN();
        if (n < 0) return;

        ComplexityResult res = engine.calculate(selected, n, timePerOp, timeUnit);

        System.out.println("\n" + BOLD + "===================== DETALLE DE EJECUCIÓN =====================" + RESET);
        System.out.println(BOLD + "Complejidad:       " + RESET + res.complexityClass().getNotation() + " (" + res.complexityClass().getName() + ")");
        System.out.println(BOLD + "Fórmula:           " + RESET + res.complexityClass().getFormulaDisplay());
        System.out.println(BOLD + "Eficiencia:        " + RESET + res.complexityClass().getEfficiency().getLabel());
        System.out.println(BOLD + "Tamaño entrada n:  " + RESET + String.format("%,d", n));
        System.out.println(BOLD + "Operaciones f(n):  " + RESET + GREEN + res.formattedOperations() + RESET);
        System.out.println(BOLD + "Tiempo Estimado:   " + RESET + GREEN + res.formattedTime() + RESET);
        System.out.println(BOLD + "Descripción:       " + RESET + res.complexityClass().getSummary());
        System.out.println(BOLD + "Ejemplos reales:   " + RESET + res.complexityClass().getRealWorldExamples());
        System.out.println(BOLD + "================================================================" + RESET);
    }

    private void handleConfigureTimePerOp() {
        System.out.println("\nConfigurar coste por operación atómica.");
        System.out.println("Unidades disponibles: 1. ns (nanosegundos), 2. µs (microsegundos), 3. ms (milisegundos), 4. s (segundos)");
        System.out.print(CYAN + "Selecciona unidad [1-4] (actual: " + timeUnit.getSymbol() + "): " + RESET);
        String uInput = scanner.nextLine().trim();
        if (!uInput.isEmpty()) {
            switch (uInput) {
                case "1" -> timeUnit = TimeUnit.NANOSECONDS;
                case "2" -> timeUnit = TimeUnit.MICROSECONDS;
                case "3" -> timeUnit = TimeUnit.MILLISECONDS;
                case "4" -> timeUnit = TimeUnit.SECONDS;
                default -> System.out.println(YELLOW + "Unidad no reconocida, manteniendo la actual." + RESET);
            }
        }

        System.out.print(CYAN + "Introduce el valor de tiempo por operación (ej. 1, 0.001, 10): " + RESET);
        String valInput = scanner.nextLine().trim().replace(',', '.').replace(" ", "");
        try {
            BigDecimal val = new BigDecimal(valInput);
            if (val.compareTo(BigDecimal.ZERO) < 0) {
                System.out.println(RED + "El tiempo por operación debe ser mayor o igual a cero." + RESET);
                return;
            }
            this.timePerOp = val;
            System.out.println(GREEN + "Tiempo configurado: " + timePerOp + " " + timeUnit.getSymbol() + RESET);
        } catch (Exception e) {
            System.out.println(RED + "Formato numérico no válido." + RESET);
        }
    }

    private void handleShowTheoreticalGuide() {
        System.out.println("\n" + BOLD + "========================= GUÍA TEÓRICA DE BIG-O =========================" + RESET);
        for (ComplexityClass c : ComplexityClass.values()) {
            System.out.printf(BOLD + "%-10s" + RESET + " [%-9s] -> %s%n",
                    c.getNotation(), c.getEfficiency().getLabel(), c.getName());
            System.out.println("  Fórmula:    " + c.getFormulaDisplay());
            System.out.println("  Concepto:   " + c.getSummary());
            System.out.println("  Ejemplos:   " + c.getRealWorldExamples());
            System.out.println();
        }
        System.out.println(BOLD + "==========================================================================" + RESET);
    }

    private long askInputN() {
        System.out.println("\nPresets rápidos: [1] 10  [2] 100  [3] 1.000  [4] 10.000  [5] 100.000  [6] 1.000.000");
        System.out.print(CYAN + "Introduce el valor de n (o número de preset [1-6]): " + RESET);
        String input = scanner.nextLine().trim();

        try {
            return switch (input) {
                case "1" -> 10L;
                case "2" -> 100L;
                case "3" -> 1_000L;
                case "4" -> 10_000L;
                case "5" -> 100_000L;
                case "6" -> 1_000_000L;
                default -> {
                    long val = Long.parseLong(input.replace(".", "").replace(",", "").replace(" ", ""));
                    if (val < 0) {
                        System.out.println(RED + "El valor n debe ser >= 0." + RESET);
                        yield -1L;
                    }
                    yield val;
                }
            };
        } catch (NumberFormatException e) {
            System.out.println(RED + "Número inválido ingresado." + RESET);
            return -1L;
        }
    }

    private void printSeparator() {
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
    }
}
