package com.example.sortingvisualizer.models;

public record SortFrame(int[] array, int comparisons, int interchanges, int[] comparedIndices, int[] swappedIndices) {
}