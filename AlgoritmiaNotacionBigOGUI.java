/*
 * AlgoritmiaNotacionBigOGUI.java
 * Calculadora de tiempo estimado para algoritmos según notación Big O
 * Autor: Daniel Casado Juan
 * GitHub: https://github.com/danicaju/Big-O-Runtime-Estimator
 * Ultimo cambio: 20/11/2025
 * Licencia: MIT
 */
package algoritmia.notacion.bigo;

import javax.swing.*;
import java.awt.*;

public class AlgoritmiaNotacionBigOGUI {

    AlgoritmiaNotacionBigO calculadora = new AlgoritmiaNotacionBigO();

    public static void main(String[] args) {
        new AlgoritmiaNotacionBigOGUI().crearGUI();
    }

    public void crearGUI() {
        JFrame frame = new JFrame("Estimador de Big-O");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // Panel de entradas
        JPanel panelInputs = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel labelN = new JLabel("Introduce n:");
        JTextField campoN = new JTextField();
        JLabel labelTiempo = new JLabel("Tiempo por operación (ms):");
        JTextField campoTiempo = new JTextField("1e-3");
        JLabel labelAlg = new JLabel("Selecciona algoritmo:");
        String[] opciones = {"O(1)", "O(log n)", "O(n)", "O(n log n)", "O(n²)", "O(n³)", "O(2ⁿ)", "O(n!)"};
        JComboBox<String> comboAlg = new JComboBox<>(opciones);

        panelInputs.add(labelN);
        panelInputs.add(campoN);
        panelInputs.add(labelTiempo);
        panelInputs.add(campoTiempo);
        panelInputs.add(labelAlg);
        panelInputs.add(comboAlg);

        // Panel de resultados
        JPanel panelResultados = new JPanel(new GridLayout(2, 1));
        JLabel resultadoOp = new JLabel("Operaciones: ");
        JLabel resultadoTiempo = new JLabel("Tiempo aproximado (ms): ");
        resultadoOp.setForeground(new Color(0, 128, 0));  // Verde oscuro
        resultadoTiempo.setForeground(new Color(0, 128, 0));
        resultadoOp.setFont(new Font("Consolas", Font.BOLD, 14));
        resultadoTiempo.setFont(new Font("Consolas", Font.BOLD, 14));
        panelResultados.add(resultadoOp);
        panelResultados.add(resultadoTiempo);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton botonCalcular = new JButton("Calcular");
        JButton botonSalir = new JButton("Salir");
        panelBotones.add(botonCalcular);
        panelBotones.add(botonSalir);

        frame.add(panelInputs, BorderLayout.NORTH);
        frame.add(panelResultados, BorderLayout.CENTER);
        frame.add(panelBotones, BorderLayout.SOUTH);

        // ActionListener para calcular
        botonCalcular.addActionListener(e -> {
            try {
                int n = Integer.parseInt(campoN.getText());
                double tiempoPorOp = Double.parseDouble(campoTiempo.getText());
                int opcion = comboAlg.getSelectedIndex() + 1;

                calculadora.valorN = n;
                calculadora.tiempoPorOperacion = tiempoPorOp;
                calculadora.opcion = opcion;

                // Ejecutar logica de cálculo
                calculadora.menuPrincipalSinLoop();

                // Mostrar resultados
                resultadoOp.setText("Operaciones: " + String.format("%.3e", calculadora.operaciones));
                resultadoTiempo.setText("Tiempo aproximado (ms): " + String.format("%.3e", calculadora.tiempo));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Entrada no válida", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Salir del GUI
        botonSalir.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }
}
