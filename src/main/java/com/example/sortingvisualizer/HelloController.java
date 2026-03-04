package com.example.sortingvisualizer;

import com.example.sortingvisualizer.algorithms.SortingStrategy;
import com.example.sortingvisualizer.algorithms.SortingStrategyFactory;
import com.example.sortingvisualizer.models.ComparisonResult;
import com.example.sortingvisualizer.models.SortFrame;
import com.example.sortingvisualizer.services.SortingService;
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloController {

    // Comparison Tab CheckBoxes
    @FXML private CheckBox chkSelection, chkInsertion, chkBubble, chkMerge, chkHeap, chkQuick;

    @FXML private ComboBox<String> compareArrayTypeComboBox;
    @FXML private TextField compareSizeField;
    @FXML private TextField compareRunsField;
    @FXML private Button loadFileButton;
    @FXML private Label selectedFileLabel;
    @FXML private Button runComparisonButton;
    @FXML private TableView<ComparisonResult> comparisonTable;
    @FXML private TableColumn<ComparisonResult, String> colAlgorithm;
    @FXML private TableColumn<ComparisonResult, Integer> colSize;
    @FXML private TableColumn<ComparisonResult, String> colMode;
    @FXML private TableColumn<ComparisonResult, Integer> colRuns;
    @FXML private TableColumn<ComparisonResult, Double> colAvgTime;
    @FXML private TableColumn<ComparisonResult, Long> colMinTime;
    @FXML private TableColumn<ComparisonResult, Long> colMaxTime;
    @FXML private TableColumn<ComparisonResult, Integer> colComparisons;
    @FXML private TableColumn<ComparisonResult, Integer> colInterchanges;

    // Visualization Tab
    @FXML private ComboBox<String> vizAlgorithmComboBox;
    @FXML private ComboBox<String> vizArrayTypeComboBox;
    @FXML private Button generateArrayButton;
    @FXML private Button vizLoadFileButton;
    @FXML private Label vizSelectedFileLabel;
    @FXML private Button visualizeButton;
    @FXML private Slider speedSlider;
    @FXML private Label vizComparisonsLabel;
    @FXML private Label vizInterchangesLabel;
    @FXML private Canvas visualizationCanvas;

    private final SortingService sortingService = new SortingService();
    private final String[] algorithms = {"Selection Sort", "Insertion Sort", "Bubble Sort", "Merge Sort", "Heap Sort", "Quick Sort"};

    // ADDED "Files" option here
    private final String[] arrayTypes = {"Random", "Sorted", "Inversely Sorted", "Files"};

    private int[] currentVizArray;
    private Timeline currentAnimation;

    // To hold multiple files in Comparison Mode
    private List<File> selectedFiles = new ArrayList<>();
    private List<int[]> loadedFileArrays = new ArrayList<>();

    @FXML
    public void initialize() {
        AnchorPane parentPane = (AnchorPane) visualizationCanvas.getParent();
        parentPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double safeWidth = Math.max(1.0, newVal.doubleValue() - 20);
            visualizationCanvas.setWidth(safeWidth);
            if (currentVizArray != null && (currentAnimation == null || currentAnimation.getStatus() != javafx.animation.Animation.Status.RUNNING)) {
                drawBars(currentVizArray);
            }
        });

        parentPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            double safeHeight = Math.max(1.0, newVal.doubleValue() - 20);
            visualizationCanvas.setHeight(safeHeight);
            if (currentVizArray != null && (currentAnimation == null || currentAnimation.getStatus() != javafx.animation.Animation.Status.RUNNING)) {
                drawBars(currentVizArray);
            }
        });

        vizComparisonsLabel.setPrefWidth(150);
        vizInterchangesLabel.setPrefWidth(150);

        ObservableList<String> algoList = FXCollections.observableArrayList(algorithms);
        ObservableList<String> typeList = FXCollections.observableArrayList(arrayTypes);

        compareArrayTypeComboBox.setItems(typeList);
        compareArrayTypeComboBox.setValue("Random");

        vizAlgorithmComboBox.setItems(algoList);
        vizAlgorithmComboBox.setValue("Bubble Sort");
        vizArrayTypeComboBox.setItems(typeList);
        vizArrayTypeComboBox.setValue("Random");

        // --- DYNAMIC UI TOGGLE LOGIC ---

        // Comparison Tab listener
        compareArrayTypeComboBox.setOnAction(e -> {
            String selected = compareArrayTypeComboBox.getValue();
            if ("Files".equals(selected)) {
                // Hide normal inputs, show file buttons
                compareSizeField.setDisable(true);
                compareRunsField.setDisable(true);
                compareRunsField.setText(String.valueOf(selectedFiles.size()));

                loadFileButton.setVisible(true);
                loadFileButton.setManaged(true);
                selectedFileLabel.setVisible(true);
                selectedFileLabel.setManaged(true);
            } else {
                // Show normal inputs, hide file buttons
                compareSizeField.setDisable(false);
                compareRunsField.setDisable(false);

                loadFileButton.setVisible(false);
                loadFileButton.setManaged(false);
                selectedFileLabel.setVisible(false);
                selectedFileLabel.setManaged(false);

                // Clear out file data so it doesn't leak into random tests
                selectedFiles.clear();
                loadedFileArrays.clear();
                selectedFileLabel.setText("No files selected");
            }
        });

        // Visualization Tab listener
        vizArrayTypeComboBox.setOnAction(e -> {
            String selected = vizArrayTypeComboBox.getValue();
            if ("Files".equals(selected)) {
                generateArrayButton.setVisible(false);
                generateArrayButton.setManaged(false);
                vizLoadFileButton.setVisible(true);
                vizLoadFileButton.setManaged(true);
                vizSelectedFileLabel.setVisible(true);
                vizSelectedFileLabel.setManaged(true);
            } else {
                generateArrayButton.setVisible(true);
                generateArrayButton.setManaged(true);
                vizLoadFileButton.setVisible(false);
                vizLoadFileButton.setManaged(false);
                vizSelectedFileLabel.setVisible(false);
                vizSelectedFileLabel.setManaged(false);
            }
        });

        colAlgorithm.setCellValueFactory(new PropertyValueFactory<>("algorithm"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colMode.setCellValueFactory(new PropertyValueFactory<>("mode"));
        colRuns.setCellValueFactory(new PropertyValueFactory<>("runs"));
        colAvgTime.setCellValueFactory(new PropertyValueFactory<>("avgTime"));
        colMinTime.setCellValueFactory(new PropertyValueFactory<>("minTime"));
        colMaxTime.setCellValueFactory(new PropertyValueFactory<>("maxTime"));
        colComparisons.setCellValueFactory(new PropertyValueFactory<>("comparisons"));
        colInterchanges.setCellValueFactory(new PropertyValueFactory<>("interchanges"));

        runComparisonButton.setOnAction(e -> handleRunComparison());
        generateArrayButton.setOnAction(e -> handleGenerateArray());
        visualizeButton.setOnAction(e -> handleVisualize());

        loadFileButton.setOnAction(e -> handleLoadMultipleFiles());
        vizLoadFileButton.setOnAction(e -> handleVizLoadSingleFile());
    }

    private void handleRunComparison() {
        comparisonTable.getItems().clear();

        try {
            int size = Integer.parseInt(compareSizeField.getText());
            int runs = Integer.parseInt(compareRunsField.getText());
            String mode = compareArrayTypeComboBox.getValue();

            if (size > 10000) size = 10000;

            // 1. Check which algorithms the user selected
            List<String> selectedAlgos = new ArrayList<>();
            if (chkSelection.isSelected()) selectedAlgos.add("Selection Sort");
            if (chkInsertion.isSelected()) selectedAlgos.add("Insertion Sort");
            if (chkBubble.isSelected()) selectedAlgos.add("Bubble Sort");
            if (chkMerge.isSelected()) selectedAlgos.add("Merge Sort");
            if (chkHeap.isSelected()) selectedAlgos.add("Heap Sort");
            if (chkQuick.isSelected()) selectedAlgos.add("Quick Sort");

            if (selectedAlgos.isEmpty()) {
                showAlert("No Algorithms Selected", "Please select at least one algorithm checkbox to run.");
                return;
            }

            if ("Files".equals(mode) && loadedFileArrays.isEmpty()) {
                showAlert("No Files Loaded", "Please load at least one file to run the comparison.");
                return;
            }

            runComparisonButton.setDisable(true);
            runComparisonButton.setText("Running...");

            int finalSize = size;
            new Thread(() -> {
                try {
                    for (String algo : selectedAlgos) { // Only loop through checked algorithms

                        if ("Files".equals(mode)) {
                            // Pass the entire list to the service at once
                            String fName = "Files (" + loadedFileArrays.size() + ")";

                            ComparisonResult result = sortingService.runComparison(algo, fName, loadedFileArrays);
                            Platform.runLater(() -> comparisonTable.getItems().add(result));
                        } else {
                            // Standard Random/Sorted generation
                            ComparisonResult result = sortingService.runComparison(algo, mode, finalSize, runs);
                            Platform.runLater(() -> comparisonTable.getItems().add(result));
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    Platform.runLater(() -> showAlert("Algorithm Crashed!", "An error occurred during sorting: " + t.getClass().getSimpleName()));
                } finally {
                    Platform.runLater(() -> {
                        runComparisonButton.setDisable(false);
                        runComparisonButton.setText("Run Comparison");
                    });
                }
            }).start();

        } catch (NumberFormatException ex) {
            showAlert("Input Error", "Please enter valid integers for Size and Runs.");
        }
    }

    // Loads MULTIPLE files for the Comparison Mode
    private void handleLoadMultipleFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Integer CSV Files");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.csv"));

        // Allows user to shift+click or ctrl+click multiple files
        List<File> files = fileChooser.showOpenMultipleDialog(null);

        if (files != null && !files.isEmpty()) {
            selectedFiles = files;
            loadedFileArrays.clear();
            try {
                for (File file : files) {
                    String content = Files.readString(file.toPath());
                    String[] stringValues = content.split(",");
                    int[] arr = new int[stringValues.length];
                    for (int i = 0; i < stringValues.length; i++) {
                        arr[i] = Integer.parseInt(stringValues[i].trim());
                    }
                    loadedFileArrays.add(arr);
                }
                selectedFileLabel.setText(files.size() + " file(s) loaded");

                // Force runs to match the number of files as requested
                compareRunsField.setText(String.valueOf(files.size()));
            } catch (Exception ex) {
                showAlert("File Error", "Could not parse one or more files. Ensure they contain comma-separated integers.");
            }
        }
    }

    // Loads a SINGLE file for the Visualization Mode
    private void handleVizLoadSingleFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Integer CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                String[] stringValues = content.split(",");
                int size = stringValues.length;

                // Cap the visualizer at 100 elements even if the file is huge
                if (size > 100) {
                    showAlert("Size Notice", "Visualization only supports up to 100 elements. Taking the first 100.");
                    size = 100;
                }

                currentVizArray = new int[size];
                for (int i = 0; i < size; i++) {
                    currentVizArray[i] = Integer.parseInt(stringValues[i].trim());
                }

                vizSelectedFileLabel.setText(file.getName());
                vizComparisonsLabel.setText("Comparisons: 0");
                vizInterchangesLabel.setText("Interchanges: 0");
                if (currentAnimation != null) currentAnimation.stop();

                drawBars(currentVizArray);
            } catch (Exception ex) {
                showAlert("File Error", "Could not parse the file. Ensure it contains comma-separated integers.");
            }
        }
    }

    private void handleGenerateArray() {
        String mode = vizArrayTypeComboBox.getValue();
        currentVizArray = sortingService.generateArray(mode, 100);

        vizComparisonsLabel.setText("Comparisons: 0");
        vizInterchangesLabel.setText("Interchanges: 0");
        if (currentAnimation != null) currentAnimation.stop();

        drawBars(currentVizArray);
    }

    private void handleVisualize() {
        if (currentVizArray == null) {
            showAlert("No Array", "Please generate or load an array first!");
            return;
        }

        String algoName = vizAlgorithmComboBox.getValue();
        SortingStrategy strategy = SortingStrategyFactory.getStrategy(algoName);
        int[] arrayCopy = currentVizArray.clone();

        List<SortFrame> frames = strategy.sortRecord(arrayCopy);

        if (currentAnimation != null) {
            currentAnimation.stop();
        }

        AtomicInteger frameIndex = new AtomicInteger(0);

        double speedValue = speedSlider.getValue();
        int framesPerTick;
        double delayMillis;

        if (speedValue <= 5) {
            framesPerTick = 1;
            delayMillis = 150.0 - ((speedValue - 1) * 33.5);
        } else {
            delayMillis = 16.0;
            framesPerTick = (int) ((speedValue - 4) * 2);
        }

        currentAnimation = new Timeline(new KeyFrame(Duration.millis(delayMillis), e -> {
            int currentIndex = frameIndex.get();

            if (currentIndex >= frames.size()) {
                currentIndex = frames.size() - 1;
            }

            SortFrame currentFrame = frames.get(currentIndex);
            int[] activeIndices = findSwappedIndices(currentIndex, frames);

            drawBars(currentFrame.array(), activeIndices);
            vizComparisonsLabel.setText("Comparisons: " + currentFrame.comparisons());
            vizInterchangesLabel.setText("Interchanges: " + currentFrame.interchanges());

            frameIndex.addAndGet(framesPerTick);
        }));

        int totalTicks = (int) Math.ceil((double) frames.size() / framesPerTick);
        currentAnimation.setCycleCount(totalTicks);

        currentAnimation.setOnFinished(e -> {
            SortFrame finalFrame = frames.get(frames.size() - 1);
            drawBars(finalFrame.array());
            vizComparisonsLabel.setText("Comparisons: " + finalFrame.comparisons());
            vizInterchangesLabel.setText("Interchanges: " + finalFrame.interchanges());

            currentVizArray = arrayCopy;
        });

        currentAnimation.play();
    }

    private int[] findSwappedIndices(int currentIndex, List<SortFrame> frames) {
        if (currentIndex == 0) return new int[0];

        int[] currentArray = frames.get(currentIndex).array();
        int[] prevArray = frames.get(currentIndex - 1).array();

        List<Integer> changed = new ArrayList<>();
        for (int i = 0; i < currentArray.length; i++) {
            if (currentArray[i] != prevArray[i]) {
                changed.add(i);
            }
        }

        return changed.stream().mapToInt(i -> i).toArray();
    }

    private void drawBars(int[] array, int... activeIndices) {
        GraphicsContext gc = visualizationCanvas.getGraphicsContext2D();
        double width = visualizationCanvas.getWidth();
        double height = visualizationCanvas.getHeight();
        gc.setFill(Color.web("#2B2B2B"));
        gc.fillRect(0, 0, width, height);

        int max = Arrays.stream(array).max().orElse(1);
        double barWidth = width / array.length;

        for (int i = 0; i < array.length; i++) {
            double barHeight = ((double) array[i] / max) * (height - 20);
            double x = i * barWidth;
            double y = height - barHeight;

            boolean isHighlighted = false;
            for (int activeIndex : activeIndices) {
                if (i == activeIndex) {
                    isHighlighted = true;
                    break;
                }
            }

            if (isHighlighted) {
                gc.setFill(Color.web("#FF5252"));
            } else {
                gc.setFill(Color.DODGERBLUE);
            }

            double widthAdjustment = barWidth > 2 ? 1.0 : 0.0;
            gc.fillRect(x, y, barWidth - widthAdjustment, barHeight);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}