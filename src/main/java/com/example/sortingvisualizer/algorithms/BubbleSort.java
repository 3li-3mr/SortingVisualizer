package com.example.sortingvisualizer.algorithms;

import java.util.ArrayList;
import java.util.List;

public class BubbleSort extends SortingStrategy{
    @Override
    public int[] sort(int[] array){
        for(int i = 0; i < array.length - 1; i++){
            boolean swap = false;
            for(int j = 0; j < array.length - i - 1; j++){
                comparisons++;
                if(array[j] > array[j+1]){
                    swap = true;
                    int temp = array[j];
                    array[j] = array[j+1];
                    array[j+1] = temp;
                    interchanges++;
                }
            }
            if(!swap) break;
        }
        return array;
    }

    @Override
    public List<int[]> sortRecord(int[] array){
        List<int[]> frames = new ArrayList<>();
        frames.add(array.clone());
        for(int i = 0; i < array.length - 1; i++){
            boolean swap = false;
            for(int j = 0; j < array.length - i - 1; j++){
                comparisons++;
                if(array[j] > array[j+1]){
                    swap = true;
                    int temp = array[j];
                    array[j] = array[j+1];
                    array[j+1] = temp;
                    interchanges++;
                    frames.add(array.clone());
                }
            }
            if(!swap) break;
        }
        return frames;
    }
}
