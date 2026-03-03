package com.example.sortingvisualizer.algorithms;

import com.example.sortingvisualizer.models.SortFrame;

import java.util.ArrayList;
import java.util.List;

public class BubbleSort extends SortingStrategy{
    @Override
    public int[] sort(int[] array){
        comparisons = 0;
        interchanges = 0;
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
    public List<SortFrame> sortRecord(int[] array){
        List<SortFrame> frames = new ArrayList<>();
        frames.add(new SortFrame(array.clone(), comparisons, interchanges));
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
                    frames.add(new SortFrame(array.clone(), comparisons, interchanges));
                }
            }
            if(!swap) break;
        }
        return frames;
    }
}
