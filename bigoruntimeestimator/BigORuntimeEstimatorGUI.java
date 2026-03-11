/*
BigORuntimeEstimatorGUI.java
Calculadora de tiempo estimado para algoritmos segun notacion Big O
Autor: Daniel Casado Juan
GitHub: https://github.com/danicaju/Big-O-Runtime-Estimator
Ultimo cambio: 17/02/2026
Licencia: MIT
*/
package bigoruntimeestimator;

import javax.swing.*;
import java.awt.*;

public class BigORuntimeEstimatorGUI {

    private BigORuntimeEstimator calculadora = new BigORuntimeEstimator();

    public static void main(String[] args) {
        // Ejecutar en el hilo de eventos de Swing (Buenas prácticas)
        SwingUtilities.invokeLater(() -> new BigORuntimeEstimatorGUI().crearGUI());
    }

    public void crearGUI() {
        JFrame frame = new JFrame("Estimador de Big-O");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // Panel de entradas
        JPanel panelInputs = new JPanel(new GridLayout(4, 2, 5, 5));
        panelInputs.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margen
        
        JLabel labelN = new JLabel("Introduce n (tamaño input):");
        JTextField campoN = new JTextField();
        
        JLabel labelTiempo = new JLabel("Tiempo por operación (ms):");
        JTextField campoTiempo = new JTextField("0.001");
        
        JLabel labelAlg = new JLabel("Selecciona algoritmo:");
        String[] opciones = {"O(1) - Constante", "O(log n) - Logarítmico", "O(n) - Lineal", "O(n log n)", "O(n²) - Cuadrático", "O(n³) - Cúbico", "O(2ⁿ) - Exponencial", "O(n!) - Factorial"};
        JComboBox<String> comboAlg = new JComboBox<>(opciones);

        panelInputs.add(labelN);
        panelInputs.add(campoN);
        panelInputs.add(labelTiempo);
        panelInputs.add(campoTiempo);
        panelInputs.add(labelAlg);
        panelInputs.add(comboAlg);

        // Panel de resultados
        JPanel panelResultados = new JPanel(new GridLayout(2, 1));
        panelResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));
        
        JLabel resultadoOp = new JLabel("Operaciones: -");
        JLabel resultadoTiempo = new JLabel("Tiempo estimado: -");
        
        resultadoOp.setForeground(new Color(0, 100, 0));  // Verde oscuro
        resultadoTiempo.setForeground(new Color(0, 0, 139)); // Azul oscuro
        resultadoOp.setFont(new Font("Consolas", Font.BOLD, 14));
        resultadoTiempo.setFont(new Font("Consolas", Font.BOLD, 14));
        
        panelResultados.add(resultadoOp);
        panelResultados.add(resultadoTiempo);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton botonCalcular = new JButton("Calcular");
        JButton botonSalir = new JButton("Salir");
        
        botonCalcular.setBackground(new Color(70, 130, 180));
        botonCalcular.setForeground(Color.WHITE);
        
        panelBotones.add(botonCalcular);
        panelBotones.add(botonSalir);

        frame.add(panelInputs, BorderLayout.NORTH);
        frame.add(panelResultados, BorderLayout.CENTER);
        frame.add(panelBotones, BorderLayout.SOUTH);

        // -------------------------
        // Lógica de los botones
        // -------------------------
        botonCalcular.addActionListener(e -> {
            try {
                // Validación básica
                if(campoN.getText().isEmpty() || campoTiempo.getText().isEmpty()){
                    JOptionPane.showMessageDialog(frame, "Por favor rellena todos los campos", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int n = Integer.parseInt(campoN.getText());
                double tiempoPorOp = Double.parseDouble(campoTiempo.getText());
                int opcionSeleccionada = comboAlg.getSelectedIndex() + 1; // +1 porque el array empieza en 0 y tu switch en 1

                // Usamos los SETTERS ahora, ya que las variables son privadas
                calculadora.setValorN(n);
                calculadora.setTiempoPorOperacion(tiempoPorOp);
                calculadora.setOcion(opcionSeleccionada);

                // Ejecutar logica
                calculadora.calcular();

                // Obtener resultados con GETTERS
                resultadoOp.setText("Operaciones: " + String.format("%.3e", calculadora.getOperaciones()));
                resultadoTiempo.setText("Tiempo estimado: " + String.format("%.3e ms", calculadora.getTiempo()));
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Entrada no válida. Asegúrate de usar punto para decimales.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        botonSalir.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }
}