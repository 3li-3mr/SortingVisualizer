package com.example.sortingvisualizer.algorithms;

public class SortingStrategyFactory {

    public static SortingStrategy getStrategy(String algorithmName) {
        switch (algorithmName) {
            case "Selection Sort": return new SelectionSort();
            case "Insertion Sort": return new InsertionSort();
            case "Bubble Sort":    return new BubbleSort();
            case "Merge Sort":     return new MergeSort();
            case "Heap Sort":      return new HeapSort();
            case "Quick Sort":     return new QuickSort();
            default: throw new IllegalArgumentException("Unknown Algorithm: " + algorithmName);
        }
    }
}