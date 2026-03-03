package com.example.sortingvisualizer.algorithms;

import java.util.ArrayList;
import java.util.List;

public class InsertionSort extends SortingStrategy{
    @Override
    public int[] sort(int[] array){
        for(int i = 1; i < array.length; i++){
            int key = array[i];
            int j = i;
            while(j > 0){
                comparisons++;
                if(key < array[j-1]){
                    array[j] = array[j-1];
                    j--;
                    interchanges++;
                }
                else{
                    break;
                }
            }
            array[j] = key;
        }
        return array;
    }

    @Override
    public List<int[]> sortRecord(int[] array){
        List<int[]> frames = new ArrayList<>();
        frames.add(array.clone());
        for(int i = 1; i < array.length; i++){
            int key = array[i];
            int j = i;
            while(j > 0){
                comparisons++;
                if(key < array[j-1]){
                    array[j] = array[j-1];
                    j--;
                    interchanges++;
                    frames.add(array.clone());
                }
                else{
                    break;
                }
            }
            array[j] = key;
            frames.add(array.clone());
        }
        return frames;
    }
}
