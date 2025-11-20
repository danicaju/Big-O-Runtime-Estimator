/*
 * BigORuntimeEstimator.java
 * Calculadora de tiempo estimado para algoritmos segun notacion Big O
 * Autor: Daniel Casado Juan
 * GitHub: https://github.com/danicaju/Big-O-Runtime-Estimator
 * Ultimo cambio: 20/11/2025
 * Licencia: MIT
 */
package bigoruntimeestimator;

public class BigORuntimeEstimator {

    int opcion;
    int valorN;
    double operaciones;
    double tiempo;
    double tiempoPorOperacion; //ms
    char caracter;
    boolean terminado = false;

    final String ROJO = "\u001B[31m";
    final String VERDE = "\u001B[32m";
    final String AMARILLO = "\u001B[33m";
    final String RESET = "\u001B[0m";
    final String CYAN = "\u001B[36m";
    final String MORADO = "\u001B[35m";

    LT lector = new LT();

    public static void main(String[] args) {
        BigORuntimeEstimator m = new BigORuntimeEstimator();
        m.menuPrincipal();
    }

    public void menuPrincipal() {
        System.out.println("----------Calculadora de tiempo para un algoritmo----------");
        System.out.println("1. O(1)\n2. (O(log n)\n3. O(n)\n4. O(n log n)\n5. O(n^2)\n6. O(n^3)\n7. O(2^n)\n8. O(n!)\n");

        try {

            System.out.print(AMARILLO + "Introduce tiempo por operacion en ms (1e-3 por defecto): " + RESET);
            tiempoPorOperacion = lector.llegirReal();
            System.out.print(AMARILLO + "Elige un algoritmo: " + RESET);
            opcion = lector.llegirEnter();
            switch (opcion) {
                case 1 -> {
                    System.out.print(CYAN + "Introduce n: " + RESET);
                    valorN = lector.llegirEnter();
                    System.out.println("Has elegido O(1)");

                    operaciones = operacionesO1(valorN);
                    tiempo = operaciones * tiempoPorOperacion;

                }

                case 2 -> {
                    System.out.print(CYAN + "Introduce n: " + RESET);
                    valorN = lector.llegirEnter();
                    System.out.println("Has elegido O(log n)");
                    if (valorN < 1) {
                        valorN = 1;
                    }

                    operaciones = operacionesOlogn(valorN);
                    tiempo = operaciones * tiempoPorOperacion;

                }

                case 3 -> {
                    System.out.print(CYAN + "Introduce n: " + RESET);
                    valorN = lector.llegirEnter();
                    System.out.println("Has elegido O(n)");

                    operaciones = operacionesOn(valorN);
                    tiempo = operaciones * tiempoPorOperacion;
                }

                case 4 -> {
                    System.out.print(CYAN + "Introduce n: " + RESET);
                    valorN = lector.llegirEnter();
                    System.out.println("Has elegido O(n log n)");

                    operaciones = operacionesOnLogN(valorN);
                    tiempo = operaciones * tiempoPorOperacion;
                }

                case 5 -> {
                    System.out.print(CYAN + "Introduce n: " + RESET);
                    valorN = lector.llegirEnter();
                    System.out.println("Has elegido O(n^2)");

                    operaciones = operacionesOn2(valorN);
                    tiempo = operaciones * tiempoPorOperacion;
                }

                case 6 -> {
                    System.out.print(CYAN + "Introduce n: " + RESET);
                    valorN = lector.llegirEnter();
                    System.out.println("Has elegido O(n^3)");

                    operaciones = operacionesOn3(valorN);
                    tiempo = operaciones * tiempoPorOperacion;
                }

                case 7 -> {
                    System.out.print(CYAN + "Introduce n: " + RESET);
                    valorN = lector.llegirEnter();
                    System.out.println("Has elegido O(2^n)");
                    if (valorN > 170) { // 170! cabe en double
                        System.out.println("n demasiado grande para factorial, usando notacion aproximada");
                    }

                    operaciones = operacionesO2n(valorN);
                    tiempo = operaciones * tiempoPorOperacion;

                }

                case 8 -> {
                    System.out.print(CYAN + "Introduce n: " + RESET);
                    valorN = lector.llegirEnter();
                    System.out.println("Has elegido O(n!)");
                    if (valorN > 170) { // 170! cabe en double
                        System.out.println("n demasiado grande para factorial, usando notacion aproximada");
                    }

                    operaciones = operacionesOnFactorial(valorN);
                    tiempo = operaciones * tiempoPorOperacion;
                }

            }

        } catch (Exception e) {
            System.out.println(ROJO + "ERROR. Ingrese un numero valido" + RESET);
            menuPrincipal();
        } finally {
            // Mostrar resultados
            System.out.printf(VERDE + "Numero estimado de operaciones: %.3e\n" + RESET, operaciones);
            System.out.printf(VERDE + "Tiempo aproximado: %.3e ms\n" + RESET, tiempo);

            do {
                System.out.print(MORADO + "Quieres seguir calculando? (s/n): " + RESET);
                caracter = lector.llegirCaracter();
                if (caracter == 's' || (caracter == 'S')) {
                    menuPrincipal();
                } else if (caracter == 'n' || caracter == 'N') {
                    terminado = true;
                } else {
                    System.out.println(ROJO + "Opcion no valida. Introduce 's' o 'n'." + RESET);
                }
                if (terminado) {
                    System.out.println(VERDE + "Gracias por usar nuestro servicio de calculo de algoritmos!" + RESET);
                }
            } while (!terminado);

        }
    }

    private double operacionesO1(int n) {
        return 1;
    }

    private double operacionesOlogn(int n) {
        return Math.log(n) / Math.log(2);  // log base 2
    }

    private double operacionesOn(int n) {
        return n;
    }

    private double operacionesOnLogN(int n) {
        return n * (Math.log(n) / Math.log(2));
    }

    private double operacionesOn2(int n) {
        return (double) n * n;
    }

    private double operacionesOn3(int n) {
        return (double) n * n * n;
    }

    private double operacionesO2n(int n) {
        return Math.pow(2, n);
    }

    private double operacionesOnFactorial(int n) {
        double resultado = 1;
        for (int i = 1; i <= n; i++) {
            resultado *= i;
        }
        return resultado;
    }

    // -------------------------
    // Método nuevo para GUI
    // -------------------------
    public void menuPrincipalSinLoop() {
        switch (opcion) {
            case 1 ->
                operaciones = operacionesO1(valorN);
            case 2 ->
                operaciones = operacionesOlogn(valorN < 1 ? 1 : valorN);
            case 3 ->
                operaciones = operacionesOn(valorN);
            case 4 ->
                operaciones = operacionesOnLogN(valorN);
            case 5 ->
                operaciones = operacionesOn2(valorN);
            case 6 ->
                operaciones = operacionesOn3(valorN);
            case 7 ->
                operaciones = operacionesO2n(valorN);
            case 8 ->
                operaciones = operacionesOnFactorial(valorN);
        }
        tiempo = operaciones * tiempoPorOperacion;
    }

}
