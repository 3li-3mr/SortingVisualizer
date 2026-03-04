package com.example.sortingvisualizer.services;

import com.example.sortingvisualizer.models.ComparisonResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvExportService {

    public void exportToCsv(List<ComparisonResult> results, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Algorithm,Array Size,Generation Mode,Runs,Avg Time (ms),Min Time (ms),Max Time (ms),Comparisons,Interchanges");

            for (ComparisonResult result : results) {
                writer.printf("%s,%d,%s,%d,%.6f,%.6f,%.6f,%d,%d%n",
                        result.getAlgorithm(),
                        result.getSize(),
                        result.getMode().replace(",", ";"),
                        result.getRuns(),
                        result.getAvgTime(),
                        result.getMinTime(),
                        result.getMaxTime(),
                        result.getComparisons(),
                        result.getInterchanges()
                );
            }
        }
    }
}