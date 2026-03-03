package com.example.sortingvisualizer.services;

import com.example.sortingvisualizer.algorithms.SortingStrategy;
import com.example.sortingvisualizer.algorithms.SortingStrategyFactory;
import com.example.sortingvisualizer.models.ComparisonResult;

import java.util.Random;

public class SortingService {
    private final Random random = new Random();

    public int[] generateArray(String mode, int size){
        int[] array = new int[size];
        switch (mode){
            case "Sorted":
                for(int i = 0; i < size; i++){
                    array[i] = i+1;
                }
                break;
            case "Inversely Sorted":
                for(int i = 0; i < size; i++){
                    array[i] = size - i;
                }
                break;
            case "Random":
            default:
                for(int i = 0; i < size; i++){
                    array[i] = random.nextInt(100000);
                }
                break;
        }
        return array;
    }

    public ComparisonResult runComparison(String algorithm, String mode, int size, int runs){
        SortingStrategy strategy = SortingStrategyFactory.getStrategy(algorithm);

        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;
        int comparisons = 0;
        int interchanges = 0;

        for (int i = 0; i < runs; i++) {
            int[] arrayToSort = generateArray(mode, size);
            long startTime = System.nanoTime();
            strategy.sort(arrayToSort);
            long endTime = System.nanoTime();
            long runTime = endTime - startTime;

            totalTime += runTime;
            if (runTime < minTime) minTime = runTime;
            if (runTime > maxTime) maxTime = runTime;

            comparisons += strategy.getComparisons();
            interchanges += strategy.getInterchanges();
        }

        double avgTime = (double) totalTime / runs / 1e6;
        minTime /= 1e6;
        maxTime /= 1e6;
        comparisons /= runs;
        interchanges /= runs;
        return new ComparisonResult(
                algorithm, size, mode, runs,
                avgTime, minTime, maxTime,
                comparisons, interchanges
        );
    }

    public ComparisonResult runComparison(String algorithm, String fileName, int[] preloadedArray, int runs){
        SortingStrategy strategy = SortingStrategyFactory.getStrategy(algorithm);

        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;
        int comparisons = 0;
        int interchanges = 0;

        for (int i = 0; i < runs; i++) {
            int[] arrayToSort = preloadedArray.clone();
            long startTime = System.nanoTime();
            strategy.sort(arrayToSort);
            long endTime = System.nanoTime();
            long runTime = endTime - startTime;

            totalTime += runTime;
            if (runTime < minTime) minTime = runTime;
            if (runTime > maxTime) maxTime = runTime;

            comparisons += strategy.getComparisons();
            interchanges += strategy.getInterchanges();
        }

        double avgTime = (double) totalTime / runs / 1e6;
        minTime /= 1e6;
        maxTime /= 1e6;
        comparisons /= runs;
        interchanges /= runs;
        return new ComparisonResult(
                algorithm, preloadedArray.length, fileName, runs,
                avgTime, minTime, maxTime,
                comparisons, interchanges
        );
    }
}

