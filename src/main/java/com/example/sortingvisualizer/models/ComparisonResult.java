package com.example.sortingvisualizer.models;

public class ComparisonResult {
    private final String algorithm;
    private final int size;
    private final String mode;
    private final int runs;
    private final double avgTime;
    private final long minTime;
    private final long maxTime;
    private final int comparisons;
    private final int interchanges;

    public ComparisonResult(String algorithm, int size, String mode, int runs,
                            double avgTime, long minTime, long maxTime,
                            int comparisons, int interchanges) {
        this.algorithm = algorithm;
        this.size = size;
        this.mode = mode;
        this.runs = runs;
        this.avgTime = avgTime;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.comparisons = comparisons;
        this.interchanges = interchanges;
    }

    public String getAlgorithm() { return algorithm; }
    public int getSize() { return size; }
    public String getMode() { return mode; }
    public int getRuns() { return runs; }
    public double getAvgTime() { return avgTime; }
    public long getMinTime() { return minTime; }
    public long getMaxTime() { return maxTime; }
    public int getComparisons() { return comparisons; }
    public int getInterchanges() { return interchanges; }
}