package com.example.sortingvisualizer.algorithms;

import java.util.ArrayList;
import java.util.List;

public class QuickSort extends SortingStrategy{
    private int partition(int[] array, int l, int r){
        int random = l + (int)(Math.random() * (r - l + 1));
        int temp = array[l];
        array[l] = array[random];
        array[random] = temp;
        if(random != 0) interchanges++;
        int i = l;
        for(int j = l+1; j < r; j++){
            comparisons++;
            if(array[j] <= array[l]){
                i++;
                temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                interchanges++;
            }
        }
        temp = array[0];
        array[0] = array[i];
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
        helper(array, 0, array.length - 1);
        return array;
    }

    private int partitionRecord(int[] array, int l, int r, List<int[]> frames){
        int random = l + (int)(Math.random() * (r - l + 1));
        int temp = array[l];
        array[l] = array[random];
        array[random] = temp;
        if(random != l){
            interchanges++;
            frames.add(array.clone());
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
                frames.add(array.clone());
            }
        }
        temp = array[l];
        array[l] = array[i];
        array[i] = temp;
        interchanges++;
        frames.add(array.clone());
        return i;
    }

    private void helperRecord(int[] array, int l, int r, List<int[]> frames){
        if(l >= r) return;
        int mid = partitionRecord(array, l, r, frames);
        helperRecord(array, l, mid - 1, frames);
        helperRecord(array, mid + 1, r, frames);
    }

    @Override
    public List<int[]> sortRecord(int[] array) {
        List<int[]> frames = new ArrayList<>();
        frames.add(array.clone());
        helperRecord(array, 0, array.length - 1, frames);
        return frames;
    }
}
