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

        double totalTime = 0;
        double minTime = Long.MAX_VALUE;
        double maxTime = Long.MIN_VALUE;
        int comparisons = 0;
        int interchanges = 0;

        for (int i = 0; i < runs; i++) {
            int[] arrayToSort = generateArray(mode, size);
            double startTime = System.nanoTime();
            strategy.sort(arrayToSort);
            double endTime = System.nanoTime();
            double runTime = endTime - startTime;

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

    public ComparisonResult runComparison(String algorithm, String fileName, java.util.List<int[]> preloadedArrays){
        SortingStrategy strategy = SortingStrategyFactory.getStrategy(algorithm);

        double totalTime = 0;
        double minTime = Long.MAX_VALUE;
        double maxTime = Long.MIN_VALUE;
        int comparisons = 0;
        int interchanges = 0;

        // The number of runs is simply the amount of files loaded
        int runs = preloadedArrays.size();

        for (int i = 0; i < runs; i++) {
            // Get the specific array for this run and clone it
            int[] arrayToSort = preloadedArrays.get(i).clone();

            double startTime = System.nanoTime();
            strategy.sort(arrayToSort);
            double endTime = System.nanoTime();
            double runTime = endTime - startTime;

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
                algorithm, preloadedArrays.get(0).length, fileName, runs,
                avgTime, minTime, maxTime,
                comparisons, interchanges
        );
    }
}

