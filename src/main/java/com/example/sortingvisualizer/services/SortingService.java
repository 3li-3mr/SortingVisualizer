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

    public ComparisonResult runComparison(String algorithm, String mode, java.util.List<int[]> preloadedArrays){
        SortingStrategy strategy = SortingStrategyFactory.getStrategy(algorithm);

        double totalTime = 0;
        double minTime = Long.MAX_VALUE;
        double maxTime = Long.MIN_VALUE;
        int comparisons = 0;
        int interchanges = 0;

        int runs = preloadedArrays.size();

        for (int i = 0; i < runs; i++) {
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

        double avgTime = totalTime / runs / 1e6;
        minTime /= 1e6;
        maxTime /= 1e6;
        comparisons /= runs;
        interchanges /= runs;

        return new ComparisonResult(
                algorithm, preloadedArrays.get(0).length, mode, runs,
                avgTime, minTime, maxTime,
                comparisons, interchanges
        );
    }
}

