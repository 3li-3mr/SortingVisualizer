package com.example.sortingvisualizer.models;

public class ComparisonResult {
    private String algorithm;
    private int size;
    private String mode;
    private int runs;
    private double avgTime;
    private long minTime;
    private long maxTime;
    private int comparisons;
    private int interchanges;

    public ComparisonResult(String algorithm, int size, String mode, int runs, double avgTime, long minTime, long maxTime, int comparisons, int interchanges) {
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