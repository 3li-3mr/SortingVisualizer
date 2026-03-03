package com.example.sortingvisualizer.algorithms;

import com.example.sortingvisualizer.models.SortFrame;

import java.util.ArrayList;
import java.util.List;

public class SelectionSort extends SortingStrategy{
    @Override
    public int[] sort(int[] array) {
        comparisons = 0;
        interchanges = 0;
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
    public List<SortFrame> sortRecord(int[] array){
        List<SortFrame> frames = new ArrayList<>();
        frames.add(new SortFrame(array.clone(), comparisons, interchanges));
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
                frames.add(new SortFrame(array.clone(), comparisons, interchanges));
            }
        }
        return frames;
    }
}
