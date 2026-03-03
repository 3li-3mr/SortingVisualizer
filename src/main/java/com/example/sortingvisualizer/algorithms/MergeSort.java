package com.example.sortingvisualizer.algorithms;

import java.util.ArrayList;
import java.util.List;

public class MergeSort extends SortingStrategy{
    private int[] helper(int[] array, int l, int r){
        if(l == r) {
            int[] merged = new int[1];
            merged[0] = array[l];
            return merged;
        }

        int mid = (l+r) / 2;
        int[] left = helper(array, l, mid);
        int[] right = helper(array, mid + 1, r);

        int[] merged = new int[left.length + right.length];
        int left_index = 0, right_index = 0, i = 0;
        while(left_index < left.length && right_index < right.length){
            comparisons++;
            if(left[left_index] <= right[right_index]){
                merged[i++] = left[left_index++];
            }
            else{
                merged[i++] = right[right_index++];
            }
            interchanges++;
        }

        while(left_index < left.length){
            merged[i++] = left[left_index++];
            interchanges++;
        }

        while(right_index < right.length){
            merged[i++] = right[right_index++];
            interchanges++;
        }

        for(int j = 0; j < merged.length; j++){
            array[l + j] = merged[j];
        }

        return merged;
    }

    @Override
    public int[] sort(int[] array) {
        return helper(array, 0, array.length - 1);
    }

    private int[] helperRecord(int[] array, int l, int r, List<int[]> frames){
        if(l == r) {
            int[] merged = new int[1];
            merged[0] = array[l];
            return merged;
        }
        int mid = (l+r) / 2;
        int[] left = helperRecord(array, l, mid, frames);
        int[] right = helperRecord(array, mid + 1, r, frames);

        int[] merged = new int[left.length + right.length];
        int left_index = 0, right_index = 0, i = 0;
        while(left_index < left.length && right_index < right.length){
            comparisons++;
            if(left[left_index] <= right[right_index]){
                merged[i++] = left[left_index++];
            }
            else{
                merged[i++] = right[right_index++];
            }
            interchanges++;

        }

        while(left_index < left.length){
            merged[i++] = left[left_index++];
            interchanges++;
        }

        while(right_index < right.length){
            merged[i++] = right[right_index++];
            interchanges++;
        }

        for(int j = 0; j < merged.length; j++){
            array[l + j] = merged[j];
            frames.add(array.clone());
        }

        return merged;
    }

    @Override
    public List<int[]> sortRecord(int[] array) {
        List<int[]> frames = new ArrayList<>();
        frames.add(array.clone());
        helperRecord(array, 0, array.length - 1, frames);
        return frames;
    }
}
