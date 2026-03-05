package com.example.sortingvisualizer;

import com.example.sortingvisualizer.algorithms.SortingStrategy;
import com.example.sortingvisualizer.algorithms.SortingStrategyFactory;
import com.example.sortingvisualizer.models.ComparisonResult;
import com.example.sortingvisualizer.models.SortFrame;
import com.example.sortingvisualizer.services.CsvExportService;
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

    @FXML private CheckBox chkSelection, chkInsertion, chkBubble, chkMerge, chkHeap, chkQuick;
    @FXML private ComboBox<String> compareArrayTypeComboBox;
    @FXML private TextField compareSizeField;
    @FXML private TextField compareRunsField;
    @FXML private Button loadFileButton;
    @FXML private Label selectedFileLabel;
    @FXML private Button runComparisonButton;
    @FXML private Button clearTableButton;
    @FXML private Button exportCsvButton;
    @FXML private TableView<ComparisonResult> comparisonTable;
    @FXML private TableColumn<ComparisonResult, String> colAlgorithm;
    @FXML private TableColumn<ComparisonResult, Integer> colSize;
    @FXML private TableColumn<ComparisonResult, String> colMode;
    @FXML private TableColumn<ComparisonResult, Integer> colRuns;
    @FXML private TableColumn<ComparisonResult, Double> colAvgTime;
    @FXML private TableColumn<ComparisonResult, Double> colMinTime;
    @FXML private TableColumn<ComparisonResult, Double> colMaxTime;
    @FXML private TableColumn<ComparisonResult, Integer> colComparisons;
    @FXML private TableColumn<ComparisonResult, Integer> colInterchanges;

    @FXML private ComboBox<String> vizAlgorithmComboBox;
    @FXML private ComboBox<String> vizArrayTypeComboBox;
    @FXML private Button generateArrayButton;
    @FXML private Button vizLoadFileButton;
    @FXML private Label vizSelectedFileLabel;
    @FXML private Button visualizeButton;
    @FXML private Button pauseResumeButton; // NEW
    @FXML private Slider speedSlider;
    @FXML private Label vizComparisonsLabel;
    @FXML private Label vizInterchangesLabel;
    @FXML private Canvas visualizationCanvas;

    private final SortingService sortingService = new SortingService();
    private final CsvExportService csvExportService = new CsvExportService();
    private final String[] algorithms = {"Selection Sort", "Insertion Sort", "Bubble Sort", "Merge Sort", "Heap Sort", "Quick Sort"};
    private final String[] arrayTypes = {"Random", "Sorted", "Inversely Sorted", "Files"};

    private int[] currentVizArray;
    private Timeline currentAnimation;

    private List<File> selectedFiles = new ArrayList<>();
    private List<int[]> loadedFileArrays = new ArrayList<>();

    @FXML
    public void initialize() {
        AnchorPane parentPane = (AnchorPane) visualizationCanvas.getParent();
        parentPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double safeWidth = Math.max(1.0, newVal.doubleValue() - 20);
            visualizationCanvas.setWidth(safeWidth);
            if (currentVizArray != null && (currentAnimation == null || currentAnimation.getStatus() != javafx.animation.Animation.Status.RUNNING)) {
                drawBars(currentVizArray, new int[0], new int[0]);
            }
        });

        parentPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            double safeHeight = Math.max(1.0, newVal.doubleValue() - 20);
            visualizationCanvas.setHeight(safeHeight);
            if (currentVizArray != null && (currentAnimation == null || currentAnimation.getStatus() != javafx.animation.Animation.Status.RUNNING)) {
                drawBars(currentVizArray, new int[0], new int[0]);
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

        compareArrayTypeComboBox.setOnAction(e -> {
            String selected = compareArrayTypeComboBox.getValue();
            if ("Files".equals(selected)) {
                compareSizeField.setDisable(true);
                compareRunsField.setDisable(true);
                compareRunsField.setText(String.valueOf(selectedFiles.size()));
                loadFileButton.setVisible(true);
                loadFileButton.setManaged(true);
                selectedFileLabel.setVisible(true);
                selectedFileLabel.setManaged(true);
            } else {
                compareSizeField.setDisable(false);
                compareRunsField.setDisable(false);
                loadFileButton.setVisible(false);
                loadFileButton.setManaged(false);
                selectedFileLabel.setVisible(false);
                selectedFileLabel.setManaged(false);
                selectedFiles.clear();
                loadedFileArrays.clear();
                selectedFileLabel.setText("No files selected");
            }
        });

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

        runComparisonButton.setOnAction(e -> runComparison());
        clearTableButton.setOnAction(e -> comparisonTable.getItems().clear());
        exportCsvButton.setOnAction(e -> exportToCsv());
        generateArrayButton.setOnAction(e -> generateArray());
        visualizeButton.setOnAction(e -> visualize());
        pauseResumeButton.setOnAction(e -> togglePauseResume());
        loadFileButton.setOnAction(e -> loadMultipleFiles());
        vizLoadFileButton.setOnAction(e -> vizLoadSingleFile());
    }

    private void runComparison() {
        try {
            int size = Integer.parseInt(compareSizeField.getText());
            int runs = Integer.parseInt(compareRunsField.getText());
            String mode = compareArrayTypeComboBox.getValue();
            if (size > 10000) size = 10000;
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

            List<int[]> arrays = new ArrayList<>();
            String displayMode = mode;

            if ("Files".equals(mode)) {
                arrays.addAll(loadedFileArrays);
                displayMode = "Files (" + loadedFileArrays.size() + ")";
            } else {
                for (int i = 0; i < runs; i++) {
                    arrays.add(sortingService.generateArray(mode, size));
                }
            }

            runComparisonButton.setDisable(true);
            runComparisonButton.setText("Running...");

            List<int[]> finalArrays = arrays;
            String finalMode = displayMode;

            new Thread(() -> {
                try {
                    for (String algo : selectedAlgos) {
                        ComparisonResult result = sortingService.runComparison(algo, finalMode, finalArrays);
                        Platform.runLater(() -> comparisonTable.getItems().add(result));
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

    private void loadMultipleFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Integer CSV Files");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.csv"));
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
                compareRunsField.setText(String.valueOf(files.size()));
            } catch (Exception ex) {
                showAlert("File Error", "Could not parse one or more files. Ensure they contain comma-separated integers.");
            }
        }
    }

    private void vizLoadSingleFile() {
        stopAnimationIfRunning();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Integer CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                String[] stringValues = content.split(",");
                int size = stringValues.length;
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

                drawBars(currentVizArray, new int[0], new int[0]);
            } catch (Exception ex) {
                showAlert("File Error", "Could not parse the file. Ensure it contains comma-separated integers.");
            }
        }
    }

    private void generateArray() {
        stopAnimationIfRunning();
        String mode = vizArrayTypeComboBox.getValue();
        currentVizArray = sortingService.generateArray(mode, 100);
        vizComparisonsLabel.setText("Comparisons: 0");
        vizInterchangesLabel.setText("Interchanges: 0");
        drawBars(currentVizArray, new int[0], new int[0]);
    }

    private void stopAnimationIfRunning() {
        if (currentAnimation != null) {
            currentAnimation.stop();
            pauseResumeButton.setDisable(true);
            pauseResumeButton.setText("Pause");
            pauseResumeButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white;");
        }
    }

    private void visualize() {
        if (currentVizArray == null) {
            showAlert("No Array", "Please generate or load an array first!");
            return;
        }

        String algoName = vizAlgorithmComboBox.getValue();
        SortingStrategy strategy = SortingStrategyFactory.getStrategy(algoName);
        int[] arrayCopy = currentVizArray.clone();

        List<SortFrame> frames = strategy.sortRecord(arrayCopy);

        stopAnimationIfRunning();

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

        pauseResumeButton.setDisable(false);
        pauseResumeButton.setText("Pause");
        pauseResumeButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");

        currentAnimation = new Timeline(new KeyFrame(Duration.millis(delayMillis), e -> {
            int currentIndex = frameIndex.get();

            if (currentIndex >= frames.size()) {
                currentIndex = frames.size() - 1;
            }

            SortFrame currentFrame = frames.get(currentIndex);

            int[] comparedIndices = currentFrame.comparedIndices();
            int[] swappedIndices = currentFrame.swappedIndices();

            drawBars(currentFrame.array(), swappedIndices, comparedIndices);
            vizComparisonsLabel.setText("Comparisons: " + currentFrame.comparisons());
            vizInterchangesLabel.setText("Interchanges: " + currentFrame.interchanges());

            frameIndex.addAndGet(framesPerTick);
        }));

        int totalTicks = (int) Math.ceil((double) frames.size() / framesPerTick);
        currentAnimation.setCycleCount(totalTicks);

        currentAnimation.setOnFinished(e -> {
            SortFrame finalFrame = frames.get(frames.size() - 1);
            drawBars(finalFrame.array(), new int[0], new int[0]);
            vizComparisonsLabel.setText("Comparisons: " + finalFrame.comparisons());
            vizInterchangesLabel.setText("Interchanges: " + finalFrame.interchanges());

            currentVizArray = arrayCopy;

            pauseResumeButton.setDisable(true);
            pauseResumeButton.setText("Pause");
            pauseResumeButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white;");
        });

        currentAnimation.play();
    }

    private void togglePauseResume() {
        if (currentAnimation != null) {
            if (currentAnimation.getStatus() == javafx.animation.Animation.Status.RUNNING) {
                currentAnimation.pause();
                pauseResumeButton.setText("Resume");
                pauseResumeButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
            } else if (currentAnimation.getStatus() == javafx.animation.Animation.Status.PAUSED) {
                currentAnimation.play();
                pauseResumeButton.setText("Pause");
                pauseResumeButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
            }
        }
    }

    private void drawBars(int[] array, int[] swappedIndices, int[] comparedIndices) {
        GraphicsContext gc = visualizationCanvas.getGraphicsContext2D();
        double width = visualizationCanvas.getWidth();
        double height = visualizationCanvas.getHeight();
        if (width <= 0 || height <= 0) return;

        gc.setFill(Color.web("#121212"));
        gc.fillRect(0, 0, width, height);

        int max = Arrays.stream(array).max().orElse(1);
        double barWidth = width / array.length;

        for (int i = 0; i < array.length; i++) {
            double barHeight = ((double) array[i] / max) * (height - 20);
            double x = i * barWidth;
            double y = height - barHeight;

            boolean isSwapped = containsIndex(swappedIndices, i);
            boolean isCompared = containsIndex(comparedIndices, i);

            if (isSwapped) {
                gc.setFill(Color.WHITE);
            } else if (isCompared) {
                gc.setFill(Color.web("#0D47A1"));
            } else {
                gc.setFill(Color.web("#2196F3"));
            }

            double widthAdjustment = barWidth > 2 ? 1.0 : 0.0;
            gc.fillRect(x, y, barWidth - widthAdjustment, barHeight);
        }
    }

    private boolean containsIndex(int[] array, int target) {
        if (array == null) return false;
        for (int j : array) {
            if (j == target) return true;
        }
        return false;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void exportToCsv() {
        if (comparisonTable.getItems().isEmpty()) {
            showAlert("Export Error", "There is no data to export. Please run a comparison first.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Results as CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("comparison_results.csv");
        File file = fileChooser.showSaveDialog(comparisonTable.getScene().getWindow());
        if (file != null) {
            try {
                List<ComparisonResult> results = new ArrayList<>(comparisonTable.getItems());
                csvExportService.exportToCsv(results, file);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Successful");
                alert.setHeaderText(null);
                alert.setContentText("Results successfully saved to:\n" + file.getAbsolutePath());
                alert.showAndWait();

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Export Error", "An error occurred while saving the file: " + ex.getMessage());
            }
        }
    }
}