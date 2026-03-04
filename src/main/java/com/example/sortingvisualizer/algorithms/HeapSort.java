package com.example.sortingvisualizer.algorithms;

import com.example.sortingvisualizer.models.SortFrame;

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
            maxHeapify(array, i, array.length - 1);
        }
    }

    @Override
    public int[] sort(int[] array) {
        comparisons = 0;
        interchanges = 0;
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

    private void maxHeapifyRecord(int[] array, int i, int heapsize, List<SortFrame> frames){
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        int largest = i;
        comparisons++;
        if(l <= heapsize && array[l] > array[i]){
            largest = l;
            frames.add(new SortFrame(array.clone(), comparisons, interchanges, new int[]{l, i}, new int[0]));
        }
        comparisons++;
        if(r <= heapsize && array[r] > array[largest]){
            largest = r;
            frames.add(new SortFrame(array.clone(), comparisons, interchanges, new int[]{r, largest}, new int[0]));
        }
        if(largest != i){
            int temp = array[i];
            array[i] = array[largest];
            array[largest] = temp;
            interchanges++;
            frames.add(new SortFrame(array.clone(), comparisons, interchanges, new int[0], new int[]{i, largest}));
            maxHeapifyRecord(array, largest, heapsize, frames);
        }
    }

    private void buildMaxHeapRecord(int[] array, List<SortFrame> frames){
        for(int i = array.length / 2 - 1; i >= 0; i--){
            maxHeapifyRecord(array, i, array.length - 1, frames);
        }
    }

    @Override
    public List<SortFrame> sortRecord(int[] array) {
        List<SortFrame> frames = new ArrayList<>();
        frames.add(new SortFrame(array.clone(), comparisons, interchanges, new int[0], new int[0]));
        buildMaxHeapRecord(array, frames);
        int heapsize = array.length - 1;
        while(heapsize > 0){
            int temp = array[0];
            array[0] = array[heapsize];
            array[heapsize--] = temp;
            interchanges++;
            frames.add(new SortFrame(array.clone(), comparisons, interchanges, new int[0], new int[]{0, heapsize + 1}));
            maxHeapifyRecord(array, 0, heapsize, frames);
        }
        return frames;
    }
}
