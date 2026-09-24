# Big-O Runtime Estimator

A Java-based application for estimating the runtime of algorithms based on their Big-O notation complexity. It features both a command-line interface (CLI) and a graphical user interface (GUI) built with Swing.

## Overview

When designing algorithms, it is essential to understand how they will scale with larger input sizes. This tool allows you to:
1. Select an algorithm's time complexity class (e.g., `O(1)`, `O(n log n)`, `O(n^2)`).
2. Input the size of the dataset `n`.
3. Provide the estimated time per single operation (in milliseconds).

The estimator calculates the total number of operations and provides a theoretical runtime approximation.

## Supported Complexities

* `O(1)` - Constant time
* `O(log n)` - Logarithmic time
* `O(n)` - Linear time
* `O(n log n)` - Linearithmic time
* `O(n^2)` - Quadratic time
* `O(n^3)` - Cubic time
* `O(2^n)` - Exponential time
* `O(n!)` - Factorial time

## How to Run

### Command-line (CLI)
You can run the main CLI application by executing the `BigORuntimeEstimator` class:
```bash
java -cp build/classes bigoruntimeestimator.BigORuntimeEstimator
```

### Graphical User Interface (GUI)
To use the GUI version:
```bash
java -cp build/classes bigoruntimeestimator.BigORuntimeEstimatorGUI
```

## Requirements
* Java 8 or higher (Swing required for GUI)
* Any IDE supporting Java (e.g., NetBeans, IntelliJ, Eclipse) or CLI Java execution.

## License
MIT License. See source comments for more details.
