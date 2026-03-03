package com.example.sortingvisualizer.algorithms;

import com.example.sortingvisualizer.models.SortFrame;

import java.util.ArrayList;
import java.util.List;

public class QuickSort extends SortingStrategy{
    private int partition(int[] array, int l, int r){
        int random = l + (int)(Math.random() * (r - l + 1));
        int temp = array[l];
        array[l] = array[random];
        array[random] = temp;
        if(random != l) interchanges++;
        int i = l;
        for(int j = l+1; j <= r; j++){
            comparisons++;
            if(array[j] <= array[l]){
                i++;
                temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                interchanges++;
            }
        }
        temp = array[l];
        array[l] = array[i];
        array[i] = temp;
        interchanges++;
        return i;
    }

    private void helper(int[] array, int l, int r){
        if(l >= r) return;
        int mid = partition(array, l, r);
        helper(array, l, mid - 1);
        helper(array, mid + 1, r);
    }

    @Override
    public int[] sort(int[] array) {
        comparisons = 0;
        interchanges = 0;
        helper(array, 0, array.length - 1);
        return array;
    }

    private int partitionRecord(int[] array, int l, int r, List<SortFrame> frames){
        int random = l + (int)(Math.random() * (r - l + 1));
        int temp = array[l];
        array[l] = array[random];
        array[random] = temp;
        if(random != l){
            interchanges++;
            frames.add(new SortFrame(array.clone(), comparisons, interchanges));
        }
        int i = l;
        for(int j = l+1; j <= r; j++){
            comparisons++;
            if(array[j] <= array[l]){
                i++;
                temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                interchanges++;
                frames.add(new SortFrame(array.clone(), comparisons, interchanges));
            }
        }
        temp = array[l];
        array[l] = array[i];
        array[i] = temp;
        interchanges++;
        frames.add(new SortFrame(array.clone(), comparisons, interchanges));
        return i;
    }

    private void helperRecord(int[] array, int l, int r, List<SortFrame> frames){
        if(l >= r) return;
        int mid = partitionRecord(array, l, r, frames);
        helperRecord(array, l, mid - 1, frames);
        helperRecord(array, mid + 1, r, frames);
    }

    @Override
    public List<SortFrame> sortRecord(int[] array) {
        List<SortFrame> frames = new ArrayList<>();
        frames.add(new SortFrame(array.clone(), comparisons, interchanges));
        helperRecord(array, 0, array.length - 1, frames);
        return frames;
    }
}
