package com.example.sortingvisualizer.algorithms;

import com.example.sortingvisualizer.models.SortFrame;
import java.util.List;

public abstract class SortingStrategy {
    protected int comparisons = 0;
    protected int interchanges = 0;
    public abstract int[] sort(int[] array);
    public abstract List<SortFrame> sortRecord(int[] array);
    public int getComparisons() { return comparisons; }
    public int getInterchanges() { return interchanges; }
}