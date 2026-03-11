<h1 align="center">⏱️ Big-O Runtime Estimator</h1>

<p align="center">
  <strong>Estimación técnica del número de operaciones y tiempo de ejecución basado en complejidad algorítmica.</strong><br>
  Implementado en Java · Entrada interactiva por consola · Cálculo en notación científica
</p>

---

## 📘 Descripción

Este proyecto implementa una herramienta en **Java** para estimar de forma aproximada:

- El **número de operaciones** teóricas según la complejidad Big-O de un algoritmo.
- El **tiempo total de ejecución**, dado un tiempo por operación configurado por el usuario.

Es útil como apoyo académico para comprender el crecimiento de funciones de complejidad:

> O(1), O(log n), O(n), O(n log n), O(n²), O(n³), O(2ⁿ), O(n!)

---

## 📊 Complejidades soportadas

| Complejidad | Función utilizada |
|-------------|------------------|
| **O(1)**        | `1` |
| **O(log n)**    | `log₂(n)` |
| **O(n)**        | `n` |
| **O(n log n)**  | `n · log₂(n)` |
| **O(n²)**       | `n²` |
| **O(n³)**       | `n³` |
| **O(2ⁿ)**       | `2ⁿ` |
| **O(n!)**       | `factorial(n)` |

---

## 🧩 Características principales

- ✔ **Entrada interactiva** mediante clase `LT`.
- ✔ **Control robusto de errores** para prevenir entradas inválidas.
- ✔ **Resultados en notación científica** para grandes magnitudes.
- ✔ **Soporte ANSI** para coloreado de salida en terminal.
- ✔ **Código modular** con una función por complejidad.
- ✔ **Factorial y potencias seguras dentro del rango double**.

---

## 🚀 Requisitos

- **Java 17** o superior.
- Archivo `LT.java` (lector personalizado).
