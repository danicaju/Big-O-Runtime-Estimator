/*
BigORuntimeEstimator.java
Calculadora de tiempo estimado para algoritmos segun notacion Big O
Autor: Daniel Casado Juan
GitHub: https://github.com/danicaju/Big-O-Runtime-Estimator
Ultimo cambio: 17/02/2026
Licencia: MIT
*/
package bigoruntimeestimator;

import java.util.Scanner;

public class BigORuntimeEstimator {

    private int opcion;
    private int valorN;
    private double operaciones;
    private double tiempo;
    private double tiempoPorOperacion; //ms
    private char caracter;
    private boolean terminado = false;

    // Colores para consola
    private final String ROJO = "\u001B[31m";
    private final String VERDE = "\u001B[32m";
    private final String AMARILLO = "\u001B[33m";
    private final String RESET = "\u001B[0m";
    private final String CYAN = "\u001B[36m";
    private final String MORADO = "\u001B[35m";

    // Reemplazo de LT por Scanner estándar para portabilidad
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        BigORuntimeEstimator m = new BigORuntimeEstimator();
        m.menuPrincipal();
    }

    public void menuPrincipal() {
        System.out.println("----------Calculadora de tiempo para un algoritmo----------");
        System.out.println("1. O(1)\n2. O(log n)\n3. O(n)\n4. O(n log n)\n5. O(n^2)\n6. O(n^3)\n7. O(2^n)\n8. O(n!)\n");

        try {
            // Lógica de consola usando Scanner
            System.out.print(AMARILLO + "Introduce tiempo por operacion en ms (usa coma para decimales, ej: 0,001): " + RESET);
            // Manejo simple para evitar errores de locale con puntos/comas
            String tiempoInput = scanner.next().replace('.', ',');
            tiempoPorOperacion = Double.parseDouble(tiempoInput.replace(',', '.'));

            System.out.print(AMARILLO + "Elige un algoritmo (1-8): " + RESET);
            opcion = scanner.nextInt();

            System.out.print(CYAN + "Introduce n: " + RESET);
            valorN = scanner.nextInt();

            // Ejecutamos el cálculo
            calcular();
            
            // Mensajes específicos de consola
            System.out.println("Has elegido la opción: " + opcion);
            if (opcion == 7 && valorN > 170) System.out.println("Nota: n demasiado grande para factorial preciso.");
            if (opcion == 8 && valorN > 170) System.out.println("Nota: n demasiado grande para factorial preciso.");

        } catch (Exception e) {
            System.out.println(ROJO + "ERROR. Ingrese un numero valido. " + e.getMessage() + RESET);
            scanner.nextLine(); // Limpiar buffer
            menuPrincipal();
            return;
        }

        // Mostrar resultados
        System.out.printf(VERDE + "Numero estimado de operaciones: %.3e\n" + RESET, operaciones);
        System.out.printf(VERDE + "Tiempo aproximado: %.3e ms\n" + RESET, tiempo);

        // Bucle de repetición
        do {
            System.out.print(MORADO + "Quieres seguir calculando? (s/n): " + RESET);
            String entrada = scanner.next();
            caracter = entrada.charAt(0);
            
            if (caracter == 's' || caracter == 'S') {
                menuPrincipal();
            } else if (caracter == 'n' || caracter == 'N') {
                terminado = true;
            } else {
                System.out.println(ROJO + "Opcion no valida." + RESET);
            }
        } while (!terminado && caracter != 's' && caracter != 'S');
        
        if (terminado) {
             System.out.println(VERDE + "Gracias por usar nuestro servicio!" + RESET);
        }
    }

    // -------------------------
    // Lógica Matemática
    // -------------------------

    private double operacionesO1(int n) { return 1; }
    private double operacionesOlogn(int n) { return Math.log(n) / Math.log(2); }
    private double operacionesOn(int n) { return n; }
    private double operacionesOnLogN(int n) { return n * (Math.log(n) / Math.log(2)); }
    private double operacionesOn2(int n) { return (double) n * n; }
    private double operacionesOn3(int n) { return (double) n * n * n; }
    private double operacionesO2n(int n) { return Math.pow(2, n); }
    
    private double operacionesOnFactorial(int n) {
        if (n > 170) return Double.POSITIVE_INFINITY; // Aproximación para evitar overflow feo
        double resultado = 1;
        for (int i = 1; i <= n; i++) {
            resultado *= i;
        }
        return resultado;
    }

    // -------------------------
    // Método centralizado de cálculo (Útil para GUI y Consola)
    // -------------------------
    public void calcular() {
        switch (opcion) {
            case 1 -> operaciones = operacionesO1(valorN);
            case 2 -> operaciones = operacionesOlogn(valorN < 1 ? 1 : valorN);
            case 3 -> operaciones = operacionesOn(valorN);
            case 4 -> operaciones = operacionesOnLogN(valorN);
            case 5 -> operaciones = operacionesOn2(valorN);
            case 6 -> operaciones = operacionesOn3(valorN);
            case 7 -> operaciones = operacionesO2n(valorN);
            case 8 -> operaciones = operacionesOnFactorial(valorN);
            default -> operaciones = 0;
        }
        tiempo = operaciones * tiempoPorOperacion;
    }

    // -------------------------
    // Getters y Setters (Necesarios para la GUI)
    // -------------------------
    public void setOcion(int opcion) { this.opcion = opcion; }
    public void setValorN(int valorN) { this.valorN = valorN; }
    public void setTiempoPorOperacion(double tiempo) { this.tiempoPorOperacion = tiempo; }
    
    public double getOperaciones() { return operaciones; }
    public double getTiempo() { return tiempo; }
}