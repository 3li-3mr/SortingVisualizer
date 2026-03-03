package com.example.sortingvisualizer.algorithms;

import com.example.sortingvisualizer.models.SortFrame;

import java.util.ArrayList;
import java.util.List;

public class InsertionSort extends SortingStrategy{
    @Override
    public int[] sort(int[] array){
        comparisons = 0;
        interchanges = 0;
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
    public List<SortFrame> sortRecord(int[] array){
        List<SortFrame> frames = new ArrayList<>();
        frames.add(new SortFrame(array.clone(), comparisons, interchanges));
        for(int i = 1; i < array.length; i++){
            int key = array[i];
            int j = i;
            while(j > 0){
                comparisons++;
                if(key < array[j-1]){
                    array[j] = array[j-1];
                    j--;
                    interchanges++;
                    frames.add(new SortFrame(array.clone(), comparisons, interchanges));
                }
                else{
                    break;
                }
            }
            array[j] = key;
            frames.add(new SortFrame(array.clone(), comparisons, interchanges));
        }
        return frames;
    }
}
