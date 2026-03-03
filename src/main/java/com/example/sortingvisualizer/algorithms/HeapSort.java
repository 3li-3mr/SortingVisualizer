package com.example.sortingvisualizer.algorithms;

import java.util.ArrayList;
import java.util.List;

public class HeapSort extends SortingStrategy{
    private void maxHeapify(int[] array, int i, int heapsize){
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        int largest = i;
        comparisons++;
        if(l <= heapsize && array[l] > array[i]){
            largest = l;
        }
        comparisons++;
        if(r <= heapsize && array[r] > array[largest]){
            largest = r;
        }
        if(largest != i){
            int temp = array[i];
            array[i] = array[largest];
            array[largest] = temp;
            interchanges++;
            maxHeapify(array, largest, heapsize);
        }
    }

    private void buildMaxHeap(int[] array){
        for(int i = array.length / 2 - 1; i >= 0; i--){
            maxHeapify(array, i, array.length);
        }
    }

    @Override
    public int[] sort(int[] array) {
        buildMaxHeap(array);
        int heapsize = array.length - 1;
        while(heapsize > 0){
            int temp = array[0];
            array[0] = array[heapsize];
            array[heapsize--] = temp;
            interchanges++;
            maxHeapify(array, 0, heapsize);
        }
        return array;
    }

    private void maxHeapifyRecord(int[] array, int i, int heapsize, List<int[]> frames){
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        int largest = i;
        comparisons++;
        if(l <= heapsize && array[l] > array[i]){
            largest = l;
        }
        comparisons++;
        if(r <= heapsize && array[r] > array[largest]){
            largest = r;
        }
        if(largest != i){
            int temp = array[i];
            array[i] = array[largest];
            array[largest] = temp;
            interchanges++;
            frames.add(array.clone());
            maxHeapifyRecord(array, largest, heapsize, frames);
        }
    }

    private void buildMaxHeapRecord(int[] array, List<int[]> frames){
        for(int i = array.length / 2 - 1; i >= 0; i--){
            maxHeapifyRecord(array, i, array.length, frames);
        }
    }

    @Override
    public List<int[]> sortRecord(int[] array) {
        List<int[]> frames = new ArrayList<>();
        buildMaxHeapRecord(array, frames);
        int heapsize = array.length - 1;
        while(heapsize > 0){
            int temp = array[0];
            array[0] = array[heapsize];
            array[heapsize--] = temp;
            interchanges++;
            frames.add(array.clone());
            maxHeapifyRecord(array, 0, heapsize, frames);
        }
        return frames;
    }
}
