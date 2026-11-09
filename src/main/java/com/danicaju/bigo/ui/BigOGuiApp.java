package com.danicaju.bigo.ui;

import com.danicaju.bigo.core.BigOEngine;
import com.danicaju.bigo.core.ComplexityClass;
import com.danicaju.bigo.core.ComplexityResult;
import com.danicaju.bigo.core.EfficiencyLevel;
import com.danicaju.bigo.core.TimeUnit;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.math.BigDecimal;
import java.util.List;

/**
 * Aplicación gráfica moderna Swing basada en el tema FlatLaf Dark.
 * Proporciona una tabla comparativa simultánea de complejidades y un explorador detallado.
 */
public class BigOGuiApp extends JFrame {

    private final BigOEngine engine = new BigOEngine();

    private JTextField fieldN;
    private JTextField fieldTimePerOp;
    private JComboBox<TimeUnit> comboUnits;
    private JTable tableComparison;
    private DefaultTableModel tableModel;

    // Componentes de la pestaña individual
    private JComboBox<ComplexityClass> comboDetailClass;
    private JLabel labelDetailNotation;
    private JLabel labelDetailName;
    private JLabel labelDetailEfficiency;
    private JLabel labelDetailFormula;
    private JTextArea textDetailSummary;
    private JTextArea textDetailExamples;
    private JLabel labelDetailOpsResult;
    private JLabel labelDetailTimeResult;

    private JLabel labelStatusBar;

    public static void main(String[] args) {
        // Inicializar Look & Feel moderno FlatLaf Dark
        try {
            FlatDarkLaf.setup();
        } catch (Exception e) {
            System.err.println("No se pudo iniciar FlatDarkLaf: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            BigOGuiApp app = new BigOGuiApp();
            app.setVisible(true);
        });
    }

    public BigOGuiApp() {
        setTitle("Big-O Runtime Estimator — Análisis y Simulación Asintótica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1020, 720);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);

        initComponents();
        recalculate();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 1. Header Banner
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center Content with TabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(10, 15, 10, 15));
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel panelComparison = createComparisonTab();
        JPanel panelDetail = createDetailTab();
        JPanel panelGuide = createGuideTab();

        tabbedPane.addTab("Tabla Comparativa Simultánea", panelComparison);
        tabbedPane.addTab("Análisis Individual Detallado", panelDetail);
        tabbedPane.addTab("Guía de Complejidades", panelGuide);

        add(tabbedPane, BorderLayout.CENTER);

        // 3. Footer Status Bar
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBorder(new EmptyBorder(15, 20, 10, 20));
        panel.setBackground(new Color(28, 30, 36));

        // Título y subtítulo
        JPanel titleBox = new JPanel(new GridLayout(2, 1, 2, 2));
        titleBox.setOpaque(false);

        JLabel titleLabel = new JLabel("Big-O Runtime Estimator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(240, 243, 246));

        JLabel subtitleLabel = new JLabel("Simulador teórico de complejidades algorítmicas con motor BigInteger de alta precisión");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(160, 166, 178));

        titleBox.add(titleLabel);
        titleBox.add(subtitleLabel);

        // Panel de Controles (Inputs)
        JPanel controlsBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        controlsBox.setOpaque(false);

        JLabel lblN = new JLabel("Tamaño n:");
        lblN.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblN.setForeground(Color.WHITE);

        fieldN = new JTextField("100", 8);
        fieldN.setFont(new Font("Consolas", Font.BOLD, 14));
        fieldN.addActionListener(e -> recalculate());

        // Botones de presets rápidos
        JPanel presetsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        presetsPanel.setOpaque(false);
        long[] presets = {10, 100, 1_000, 10_000, 100_000, 1_000_000};
        for (long p : presets) {
            JButton btnPreset = new JButton(formatPresetLabel(p));
            btnPreset.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnPreset.setFocusable(false);
            btnPreset.setMargin(new Insets(2, 6, 2, 6));
            btnPreset.addActionListener(e -> {
                fieldN.setText(String.valueOf(p));
                recalculate();
            });
            presetsPanel.add(btnPreset);
        }

        JLabel lblTime = new JLabel("Tiempo/op:");
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTime.setForeground(Color.WHITE);

        fieldTimePerOp = new JTextField("0.001", 6);
        fieldTimePerOp.setFont(new Font("Consolas", Font.BOLD, 14));
        fieldTimePerOp.addActionListener(e -> recalculate());

        comboUnits = new JComboBox<>(TimeUnit.values());
        comboUnits.setSelectedItem(TimeUnit.MILLISECONDS);
        comboUnits.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboUnits.addActionListener(e -> recalculate());

        JButton btnCalculate = new JButton("Calcular");
        btnCalculate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCalculate.setBackground(new Color(37, 99, 235));
        btnCalculate.setForeground(Color.WHITE);
        btnCalculate.setFocusable(false);
        btnCalculate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCalculate.addActionListener(e -> recalculate());

        controlsBox.add(lblN);
        controlsBox.add(fieldN);
        controlsBox.add(presetsPanel);
        controlsBox.add(Box.createHorizontalStrut(10));
        controlsBox.add(lblTime);
        controlsBox.add(fieldTimePerOp);
        controlsBox.add(comboUnits);
        controlsBox.add(btnCalculate);

        panel.add(titleBox, BorderLayout.NORTH);
        panel.add(controlsBox, BorderLayout.SOUTH);
        return panel;
    }

    private String formatPresetLabel(long p) {
        if (p >= 1_000_000) return (p / 1_000_000) + "M";
        if (p >= 1_000) return (p / 1_000) + "k";
        return String.valueOf(p);
    }

    private JPanel createComparisonTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {"Complejidad", "LaTeX", "Nombre", "Eficiencia", "Operaciones f(n)", "Tiempo Estimado"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableComparison = new JTable(tableModel);
        tableComparison.setRowHeight(36);
        tableComparison.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableComparison.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableComparison.getTableHeader().setReorderingAllowed(false);
        tableComparison.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Renderers de celdas
        // Columna Complejidad: Negrita
        tableComparison.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableComparison.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        tableComparison.getColumnModel().getColumn(1).setPreferredWidth(120);
        tableComparison.getColumnModel().getColumn(2).setPreferredWidth(130);

        // Columna Eficiencia: Pill Badge coloreado
        tableComparison.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableComparison.getColumnModel().getColumn(3).setCellRenderer(new EfficiencyBadgeRenderer());

        // Columna Operaciones: Monospace alineado a la derecha
        tableComparison.getColumnModel().getColumn(4).setPreferredWidth(260);
        tableComparison.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Consolas", Font.BOLD, 13));
                setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        });

        // Columna Tiempo: Monospace destacado
        tableComparison.getColumnModel().getColumn(5).setPreferredWidth(260);
        tableComparison.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Consolas", Font.BOLD, 13));
                setForeground(new Color(56, 189, 248)); // Azul cyan luminoso FlatLaf
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableComparison);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(45, 52, 64)));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDetailTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Selector superior
        JPanel topSelect = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        topSelect.setOpaque(false);
        JLabel lblSelect = new JLabel("Seleccionar clase de complejidad para inspección:");
        lblSelect.setFont(new Font("Segoe UI", Font.BOLD, 13));

        comboDetailClass = new JComboBox<>(ComplexityClass.values());
        comboDetailClass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboDetailClass.addActionListener(e -> updateDetailTab());

        topSelect.add(lblSelect);
        topSelect.add(comboDetailClass);

        // Panel de información técnica (Grid)
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        infoPanel.setOpaque(false);

        // Tarjeta 1: Metadatos y Fórmula
        JPanel card1 = createCardPanel("Definición y Metadatos");
        card1.setLayout(new GridLayout(4, 2, 8, 8));
        labelDetailNotation = new JLabel();
        labelDetailName = new JLabel();
        labelDetailEfficiency = new JLabel();
        labelDetailFormula = new JLabel();

        labelDetailNotation.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelDetailFormula.setFont(new Font("Consolas", Font.BOLD, 14));

        card1.add(new JLabel("Notación:"));
        card1.add(labelDetailNotation);
        card1.add(new JLabel("Nombre canónico:"));
        card1.add(labelDetailName);
        card1.add(new JLabel("Nivel de eficiencia:"));
        card1.add(labelDetailEfficiency);
        card1.add(new JLabel("Fórmula matemática:"));
        card1.add(labelDetailFormula);

        // Tarjeta 2: Resultados calculados para n actual
        JPanel card2 = createCardPanel("Resultados para n actual");
        card2.setLayout(new GridLayout(2, 1, 10, 10));

        JPanel boxOps = new JPanel(new BorderLayout(5, 5));
        boxOps.setOpaque(false);
        JLabel lblOpsTitle = new JLabel("Operaciones estimadas f(n):");
        lblOpsTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelDetailOpsResult = new JLabel("-");
        labelDetailOpsResult.setFont(new Font("Consolas", Font.BOLD, 16));
        labelDetailOpsResult.setForeground(new Color(34, 197, 94));
        boxOps.add(lblOpsTitle, BorderLayout.NORTH);
        boxOps.add(labelDetailOpsResult, BorderLayout.CENTER);

        JPanel boxTime = new JPanel(new BorderLayout(5, 5));
        boxTime.setOpaque(false);
        JLabel lblTimeTitle = new JLabel("Tiempo estimado de ejecución:");
        lblTimeTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelDetailTimeResult = new JLabel("-");
        labelDetailTimeResult.setFont(new Font("Consolas", Font.BOLD, 16));
        labelDetailTimeResult.setForeground(new Color(56, 189, 248));
        boxTime.add(lblTimeTitle, BorderLayout.NORTH);
        boxTime.add(labelDetailTimeResult, BorderLayout.CENTER);

        card2.add(boxOps);
        card2.add(boxTime);

        // Tarjeta 3: Fundamento teórico y comportamiento asintótico
        JPanel card3 = createCardPanel("Concepto y Comportamiento Asintótico");
        card3.setLayout(new BorderLayout());
        textDetailSummary = new JTextArea();
        textDetailSummary.setWrapStyleWord(true);
        textDetailSummary.setLineWrap(true);
        textDetailSummary.setEditable(false);
        textDetailSummary.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textDetailSummary.setOpaque(false);
        card3.add(textDetailSummary, BorderLayout.CENTER);

        // Tarjeta 4: Algoritmos representativos
        JPanel card4 = createCardPanel("Casos de Uso y Algoritmos en la Industria");
        card4.setLayout(new BorderLayout());
        textDetailExamples = new JTextArea();
        textDetailExamples.setWrapStyleWord(true);
        textDetailExamples.setLineWrap(true);
        textDetailExamples.setEditable(false);
        textDetailExamples.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textDetailExamples.setOpaque(false);
        card4.add(textDetailExamples, BorderLayout.CENTER);

        infoPanel.add(card1);
        infoPanel.add(card2);
        infoPanel.add(card3);
        infoPanel.add(card4);

        panel.add(topSelect, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCardPanel(String title) {
        JPanel card = new JPanel();
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(60, 68, 80)),
                        title
                ),
                new EmptyBorder(10, 12, 10, 12)
        ));
        card.setBackground(new Color(32, 36, 44));
        return card;
    }

    private JPanel createGuideTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JTextArea guideText = new JTextArea();
        guideText.setEditable(false);
        guideText.setFont(new Font("Consolas", Font.PLAIN, 13));
        guideText.setMargin(new Insets(15, 15, 15, 15));
        guideText.setBackground(new Color(24, 27, 34));
        guideText.setForeground(new Color(225, 230, 240));

        guideText.setText(
                """
                JERARQUÍA DE COMPLEJIDAD ASINTÓTICA (De mejor a peor):
                ========================================================================================
                O(1) < O(log n) < O(n) < O(n log n) < O(n²) < O(n³) < O(2ⁿ) < O(n!)
                ----------------------------------------------------------------------------------------
                
                1. O(1) - Constante (Excelente)
                   - Tiempo de cómputo invariable frente al volumen de datos.
                   - Ejemplos: Indexación en arrays por puntero, inserción en cabeza en LinkedList, push/pop en pila.
                
                2. O(log n) - Logarítmica (Excelente)
                   - Reduce a la mitad el espacio de búsqueda en cada paso iterativo o recursivo.
                   - Con n = 1.000.000 requiere apenas ~20 comparaciones.
                   - Ejemplos: Búsqueda binaria en listas ordenadas, balanceo en árboles AVL o Red-Black.
                
                3. O(n) - Lineal (Bueno)
                   - Crece en proporción 1:1 con el tamaño de la entrada.
                   - Con n = 1.000.000 realiza exactamente 1.000.000 de operaciones.
                   - Ejemplos: Recorrido secuencial de un array, filtrado lineal, copia de buffer.
                
                4. O(n log n) - Linealítmica (Aceptable)
                   - Límite inferior óptimo para algoritmos de ordenación basados en comparaciones.
                   - Con n = 1.000.000 requiere ~20 millones de operaciones (totalmente abordable).
                   - Ejemplos: Merge Sort, Quick Sort (caso promedio), Heap Sort, TimSort.
                
                5. O(n²) - Cuadrática (Malo)
                   - Típico de bucles anidados sobre toda la colección.
                   - Con n = 100.000 genera 10.000.000.000 operaciones (10 mil millones).
                   - Ejemplos: Bubble Sort, Selection Sort, comparación de pares por fuerza bruta.
                
                6. O(n³) - Cúbica (Malo)
                   - Típico de algoritmos con 3 niveles de anidación profunda.
                   - Con n = 1.000 genera 1.000.000.000 operaciones.
                   - Ejemplos: Multiplicación tradicional de matrices, algoritmo de Floyd-Warshall.
                
                7. O(2ⁿ) - Exponencial (Horrible)
                   - El tiempo se duplica con cada nuevo elemento sumado a n.
                   - Con n = 100 supera el número de átomos en el universo visible (~1.27 × 10³⁰).
                   - Ejemplos: Sucesión de Fibonacci recursiva sin memorización, generación de conjunto potencia.
                
                8. O(n!) - Factorial (Horrible)
                   - Genera todas las permutaciones posibles de n elementos.
                   - Con n = 20 requeriría 2.43 × 10¹⁸ operaciones (siglos de supercómputo).
                   - Ejemplos: Búsqueda exhaustiva del Viajante de Comercio (TSP), permutaciones de strings.
                """
        );

        JScrollPane scroll = new JScrollPane(guideText);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(45, 52, 64)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout(10, 5));
        footer.setBorder(new EmptyBorder(8, 20, 8, 20));
        footer.setBackground(new Color(24, 27, 34));

        labelStatusBar = new JLabel("Listo.");
        labelStatusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelStatusBar.setForeground(new Color(160, 166, 178));

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonBar.setOpaque(false);

        JButton btnCopy = new JButton("Copiar Resumen");
        btnCopy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnCopy.setFocusable(false);
        btnCopy.addActionListener(e -> copySummaryToClipboard());

        JButton btnReset = new JButton("Restablecer");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnReset.setFocusable(false);
        btnReset.addActionListener(e -> {
            fieldN.setText("100");
            fieldTimePerOp.setText("0.001");
            comboUnits.setSelectedItem(TimeUnit.MILLISECONDS);
            recalculate();
        });

        buttonBar.add(btnCopy);
        buttonBar.add(btnReset);

        footer.add(labelStatusBar, BorderLayout.WEST);
        footer.add(buttonBar, BorderLayout.EAST);
        return footer;
    }

    private void recalculate() {
        long n;
        BigDecimal timePerOp;
        TimeUnit unit = (TimeUnit) comboUnits.getSelectedItem();

        try {
            String nStr = fieldN.getText().trim().replace(".", "").replace(",", "");
            n = Long.parseLong(nStr);
            if (n < 0) {
                showError("El tamaño n debe ser mayor o igual a 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("Introduce un número entero válido para n.");
            return;
        }

        try {
            String timeStr = fieldTimePerOp.getText().trim().replace(',', '.');
            timePerOp = new BigDecimal(timeStr);
            if (timePerOp.compareTo(BigDecimal.ZERO) < 0) {
                showError("El tiempo por operación no puede ser negativo.");
                return;
            }
        } catch (Exception ex) {
            showError("Introduce un número válido para el tiempo por operación.");
            return;
        }

        long start = System.nanoTime();
        List<ComplexityResult> results = engine.calculateAll(n, timePerOp, unit);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        // Actualizar Tabla Comparativa
        tableModel.setRowCount(0);
        for (ComplexityResult res : results) {
            tableModel.addRow(new Object[]{
                    res.complexityClass().getNotation(),
                    res.complexityClass().getLatexNotation(),
                    res.complexityClass().getName(),
                    res.complexityClass().getEfficiency(),
                    res.formattedOperations(),
                    res.formattedTime()
            });
        }

        // Actualizar Pestaña Individual
        updateDetailTab();

        labelStatusBar.setText(String.format("Cálculo completado para n = %,d (%s %s) en %d ms | 8 complejidades evaluadas.",
                n, timePerOp, unit.getSymbol(), elapsedMs));
    }

    private void updateDetailTab() {
        if (comboDetailClass == null) return;
        ComplexityClass selected = (ComplexityClass) comboDetailClass.getSelectedItem();
        if (selected == null) return;

        labelDetailNotation.setText(selected.getNotation());
        labelDetailName.setText(selected.getName());
        labelDetailEfficiency.setText(selected.getEfficiency().getLabel());
        labelDetailEfficiency.setForeground(selected.getEfficiency().getColor());
        labelDetailFormula.setText(selected.getFormulaDisplay());
        textDetailSummary.setText(selected.getSummary());
        textDetailExamples.setText(selected.getRealWorldExamples());

        try {
            String nStr = fieldN.getText().trim().replace(".", "").replace(",", "");
            long n = Long.parseLong(nStr);
            String timeStr = fieldTimePerOp.getText().trim().replace(',', '.');
            BigDecimal timePerOp = new BigDecimal(timeStr);
            TimeUnit unit = (TimeUnit) comboUnits.getSelectedItem();

            ComplexityResult res = engine.calculate(selected, n, timePerOp, unit);
            labelDetailOpsResult.setText(res.formattedOperations());
            labelDetailTimeResult.setText(res.formattedTime());
        } catch (Exception ignored) {
            labelDetailOpsResult.setText("-");
            labelDetailTimeResult.setText("-");
        }
    }

    private void copySummaryToClipboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Big-O Runtime Estimator — Resumen de Simulación ===\n");
        sb.append("Tamaño n: ").append(fieldN.getText()).append("\n");
        sb.append("Tiempo por operación: ").append(fieldTimePerOp.getText()).append(" ").append(comboUnits.getSelectedItem()).append("\n\n");
        sb.append(String.format("%-12s | %-12s | %-10s | %-25s | %s%n",
                "Complejidad", "Nombre", "Eficiencia", "Operaciones", "Tiempo Estimado"));
        sb.append("-----------------------------------------------------------------------------------------\n");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            sb.append(String.format("%-12s | %-12s | %-10s | %-25s | %s%n",
                    tableModel.getValueAt(i, 0),
                    tableModel.getValueAt(i, 2),
                    tableModel.getValueAt(i, 3),
                    tableModel.getValueAt(i, 4),
                    tableModel.getValueAt(i, 5)));
        }

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
        JOptionPane.showMessageDialog(this, "El resumen se ha copiado al portapapeles con éxito.", "Portapapeles", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Parámetro Inválido", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Renderer visual para renderizar badges de píldora coloreados para la eficiencia.
     */
    private static class EfficiencyBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof EfficiencyLevel eff) {
                label.setText(eff.getLabel());
                label.setForeground(eff.getColor());
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setHorizontalAlignment(SwingConstants.CENTER);
            }
            return label;
        }
    }
}
