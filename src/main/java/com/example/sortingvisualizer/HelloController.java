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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloController {

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
    @FXML private ComboBox<String> vizAlgorithmComboBox;
    @FXML private ComboBox<String> vizArrayTypeComboBox;
    @FXML private Button generateArrayButton;
    @FXML private Button visualizeButton;
    @FXML private Slider speedSlider;
    @FXML private Label vizComparisonsLabel;
    @FXML private Label vizInterchangesLabel;
    @FXML private Canvas visualizationCanvas;

    private final SortingService sortingService = new SortingService();
    private final String[] algorithms = {"Selection Sort", "Insertion Sort", "Bubble Sort", "Merge Sort", "Heap Sort", "Quick Sort"};
    private final String[] arrayTypes = {"Random", "Sorted", "Inversely Sorted"};

    private int[] currentVizArray;
    private int[] loadedFileArray = null;
    private Timeline currentAnimation;

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
        loadFileButton.setOnAction(e -> handleLoadFile());

        compareArrayTypeComboBox.setOnAction(e -> {
            loadedFileArray = null;
            selectedFileLabel.setText("No file selected");
            compareSizeField.setDisable(false);
        });
    }

    private void handleRunComparison() {
        comparisonTable.getItems().clear();

        try {
            // 1. SAFELY EXTRACT ALL UI DATA BEFORE THE THREAD STARTS
            int size = Integer.parseInt(compareSizeField.getText());
            int runs = Integer.parseInt(compareRunsField.getText());
            String mode = compareArrayTypeComboBox.getValue();
            String fileName = selectedFileLabel.getText(); // MUST be read here!

            if (size > 10000) size = 10000;

            runComparisonButton.setDisable(true);
            runComparisonButton.setText("Running...");

            int finalSize = size;

            new Thread(() -> {
                try {
                    for (String algo : algorithms) {
                        ComparisonResult result;

                        // 2. Use the pre-extracted 'fileName' variable here
                        if (loadedFileArray != null) {
                            result = sortingService.runComparison(algo, "File: " + fileName, loadedFileArray, runs);
                        } else {
                            result = sortingService.runComparison(algo, mode, finalSize, runs);
                        }

                        // Safely add to UI
                        Platform.runLater(() -> {
                            comparisonTable.getItems().add(result);
                        });
                    }
                } catch (Throwable t) {
                    // 3. CATCH SILENT CRASHES (Like StackOverflows in QuickSort)
                    t.printStackTrace();
                    Platform.runLater(() -> {
                        showAlert("Algorithm Crashed!", "An error occurred during sorting: " + t.getClass().getSimpleName() + "\nCheck your IDE console for details.");
                    });
                } finally {
                    // 4. GUARANTEE the button resets, even if an error happens
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

    private void handleLoadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Integer CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                String[] stringValues = content.split(",");
                loadedFileArray = new int[stringValues.length];
                for (int i = 0; i < stringValues.length; i++) {
                    loadedFileArray[i] = Integer.parseInt(stringValues[i].trim());
                }
                selectedFileLabel.setText(file.getName());
                compareSizeField.setText(String.valueOf(loadedFileArray.length));
                compareSizeField.setDisable(true);
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
            showAlert("No Array", "Please generate an array first!");
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

        // Find which indices have different values from the previous frame
        java.util.List<Integer> changed = new java.util.ArrayList<>();
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