package com.example.sortingvisualizer.algorithms;

import java.util.ArrayList;
import java.util.List;

public class SelectionSort extends SortingStrategy{
    @Override
    public int[] sort(int[] array) {
        for(int i = 0; i < array.length - 1; i++){
            int min_index = i;
            for(int j = i + 1; j < array.length; j++){
                comparisons++;
                if(array[j] < array[min_index]){
                    min_index = j;
                }
            }
            if(min_index != i){
                int temp = array[i];
                array[i] = array[min_index];
                array[min_index] = temp;
                interchanges++;
            }
        }
        return array;
    }
    @Override
    public List<int[]> sortRecord(int[] array){
        List<int[]> frames = new ArrayList<>();
        frames.add(array.clone());
        for (int i = 0; i < array.length - 1; i++) {
            int min_index = i;
            for (int j = i + 1; j < array.length; j++) {
                comparisons++;
                if (array[j] < array[min_index]) {
                    min_index = j;
                }
            }
            if (min_index != i) {
                int temp = array[i];
                array[i] = array[min_index];
                array[min_index] = temp;
                interchanges++;
                frames.add(array.clone());
            }
        }
        return frames;
    }
}
