package com.cs.os.cpuscheduler;

import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Controller {
    @FXML
    public HBox quantumInputSection;
    @FXML
    public BarChart histogramChart;
    public AnchorPane visualizationDiv;

    @FXML // ChoiceBox for approach selection and algorithm selection
    private ChoiceBox<String> approachChoice, algorithmChoice, preemptiveChoice, agingChoice, roundRobinChoice, outputChoiceBox;

    @FXML // VBox sections for different input methods
    private VBox normalInputSection, fileInputSection, randomProcessSection, priorityOptions, chartsView;

    @FXML // Labels for view and algorithm titles
    public Label viewLabel, algoLabel, filePathLabel;

    @FXML
    private Label averageTurnaroundTime, averageWaitingTime, throughput, cpuUtilization, contextSwitches, waitingTimeVariance, maxWaitingTime;

    @FXML // Input fields for process data input
    private TextField arrivalTimeInput, burstTimeInput, quantumField, priorityInput, ticketsInput, maxBurstTimeInput, numProcesses, lastArrivalInput;

    @FXML // Button for choosing file input
    private Button chooseFileButton;

    // TableView and columns for displaying process data
    @FXML
    private TableView<Process> processTableView;
    @FXML
    private TableColumn<Process, String> jobColumn;
    @FXML
    private TableColumn<Process, Integer> arrivalColumn, burstColumn, finishColumn, turnaroundColumn, waitingColumn;

    @FXML
    private Canvas ganttCanvas;  // The Canvas where the Gantt Chart is drawn


    private CPUScheduler cpuScheduler = new CPUScheduler(new FCFS());
    private ProcessFactory processFactory = new SimpleProcessFactory();
    private ObservableList<Process> processList = FXCollections.observableArrayList();
    private File selectedFile;


    public record Triple<L, M, R>(L left, M middle, R right) {}


    @FXML
    public void initialize() {

        normalInputSection.setVisible(false);
        normalInputSection.setManaged(false);
        fileInputSection.setVisible(false);
        fileInputSection.setManaged(false);
        randomProcessSection.setVisible(false);
        randomProcessSection.setManaged(false);

        quantumField.setText("1");
        quantumInputSection.setVisible(false);
        quantumInputSection.setManaged(false);


        // Set up columns in the table view
        jobColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProcessName()));
        arrivalColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getArrivalTime()).asObject());
        burstColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getBurstTime()).asObject());
        finishColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCompletionTime()).asObject());
        turnaroundColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTurnaround()).asObject());
        waitingColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getWaitingTime()).asObject());

        // Bind the observable list to the table view
        processTableView.setItems(processList);


        processTableView.prefWidthProperty().bind(visualizationDiv.widthProperty());
        processTableView.prefHeightProperty().bind(visualizationDiv.heightProperty());
        ganttCanvas.widthProperty().bind(visualizationDiv.widthProperty());
        ganttCanvas.heightProperty().bind(visualizationDiv.heightProperty());
        chartsView.prefWidthProperty().bind(visualizationDiv.widthProperty());
        chartsView.prefHeightProperty().bind(visualizationDiv.heightProperty());

        processTableView.setVisible(false);
        ganttCanvas.setVisible(false);
        chartsView.setVisible(false);

    }

    @FXML
    private void onApproachChoiceChange() {
        // Get the selected approach
        String selectedApproach = approachChoice.getValue();
        String selectedAlgorithm = algorithmChoice.getValue();

        // Hide all input sections initially
        normalInputSection.setVisible(false);
        normalInputSection.setManaged(false);
        fileInputSection.setVisible(false);
        fileInputSection.setManaged(false);
        randomProcessSection.setVisible(false);
        randomProcessSection.setManaged(false);

        priorityInput.setVisible(false);
        priorityInput.setManaged(false);
        ticketsInput.setVisible(false);
        ticketsInput.setManaged(false);

        if(selectedApproach == null)
            selectedApproach = "Normal Input";
        // Show the selected approach's input section and set it to be managed
        switch (selectedApproach) {
            case "Normal Input":
                normalInputSection.setVisible(true);
                normalInputSection.setManaged(true);
                switch (selectedAlgorithm) {
                    case "Priority Scheduling":
                        priorityInput.setVisible(true);
                        priorityInput.setManaged(true);
                        break;
                    case "Lottery Scheduling":
                        ticketsInput.setVisible(true);
                        ticketsInput.setManaged(true);
                        break;
                }
                break;
            case "File Input":
                fileInputSection.setVisible(true);
                fileInputSection.setManaged(true);
                break;
            case "Random Process Generation":
                randomProcessSection.setVisible(true);
                randomProcessSection.setManaged(true);
                break;
        }
    }

    @FXML
    private void onAlgorithmChoiceChange() {
        String selectedAlgorithm = algorithmChoice.getValue();

        priorityOptions.setVisible(false);
        priorityOptions.setManaged(false);
        quantumInputSection.setVisible(false);
        quantumInputSection.setManaged(false);


        // Adjust visibility based on the selected algorithm
        switch (selectedAlgorithm){
            case "Priority Scheduling":
                priorityOptions.setVisible(true);
                priorityOptions.setManaged(true);
                onPriorityRRChoiceChange();
                break;
            case "Round Robin":
                quantumInputSection.setVisible(true);
                quantumInputSection.setManaged(true);
                break;
        }

        onApproachChoiceChange();
    }

    @FXML
    private void onPriorityRRChoiceChange() {
        if(roundRobinChoice.getValue() == null)
            roundRobinChoice.setValue("With First Come, First Served");
        if (roundRobinChoice.getValue().equals("With Round Robin")) {
            quantumInputSection.setVisible(true);
            quantumInputSection.setManaged(true);
        } else {
            quantumInputSection.setVisible(false);
            quantumInputSection.setManaged(false);
        }
    }


    @FXML
    public void displayHistogram(HashMap<String, Integer> data) {
        // Clear the existing data from the histogram chart
        histogramChart.getData().clear();

        // Create a new data series
        XYChart.Series<String, Integer> series = new XYChart.Series<>();

        // Add data from the HashMap to the series
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Add the data series to the histogram chart
        histogramChart.getData().add(series);
    }

    @FXML
    public void onOutputChoiceChange() {
        setUpSimulation(); // Set up the simulation
        cpuScheduler.runScheduler(); // Run the scheduler

        updateMetrics();

        String selectedChoice = outputChoiceBox.getValue(); // Get the selected output choice

        // Hide all output views initially
        processTableView.setVisible(false);
        ganttCanvas.setVisible(false);
        chartsView.setVisible(false);
//        videoSimulationView.setVisible(false);
        viewLabel.setText(selectedChoice);
        // Show the selected output view and call the relevant function
        switch (selectedChoice) {
            case "Table View":
                processTableView.setVisible(true);
                processTableView.refresh(); // Refresh the table view to display updated process information
                break;
            case "Gantt Chart":
                ganttCanvas.setVisible(true);
                drawGanttChart(cpuScheduler.getProcessExecutionTimeline()); // Call the function to draw the Gantt chart if necessary
                break;
            case "Charts View":
                chartsView.setVisible(true);
                // Call the function to display the histogram if necessary
//                HashMap<String, Integer> histogramData = getHistogramData(); // Assuming you have a method to get histogram data
//                displayHistogram(histogramData);
                break;
//            case "Video Simulation":
//                videoSimulationView.setVisible(true);
//                // Implement function to display video simulation if necessary
//                break;
        }
    }

    @FXML
    // Event handler for the "Choose File" button
    private void onChooseFileClick(ActionEvent event) {
        // Create a new FileChooser instance
        FileChooser fileChooser = new FileChooser();

        // Optional: Set file filters, initial directory, or initial file name
        fileChooser.setTitle("Open Process Definition File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        // Get the stage (parent window) from the event source
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

        // Show the file chooser and get the selected file
        selectedFile = fileChooser.showOpenDialog(stage);
        // Update the file path label if a file is selected
        if (selectedFile != null) {
            filePathLabel.setText("Selected file: " + selectedFile.getPath());
        } else {
            filePathLabel.setText("No file selected");
        }
    }

    private void createListsFromInputs(List<String> arrivalTimes, List<String> burstTimes, List<String> priority) {
        // Clear the existing lists
        arrivalTimes.clear();
        burstTimes.clear();
        priority.clear();

        // Split inputs and add them to the existing lists
        Collections.addAll(arrivalTimes, arrivalTimeInput.getText().split("\\s+"));
        Collections.addAll(burstTimes, burstTimeInput.getText().split("\\s+"));
        Collections.addAll(priority, priorityInput.getText().split("\\s+"));
    }

    private void createListsFromFile(String filePath, List<String> arrivalTimes, List<String> burstTimes, List<String> priority) throws IOException {
        // Clear the existing lists
        arrivalTimes.clear();
        burstTimes.clear();
        priority.clear();

        // Create a BufferedReader to read the file
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Read the first line for arrival times
            String line = reader.readLine();
            if (line != null) {
                // Split the line and add to the existing list
                Collections.addAll(arrivalTimes, line.split("\\s+"));
            } else {
                throw new IOException("Missing arrival times in file.");
            }

            // Read the second line for burst times
            line = reader.readLine();
            if (line != null) {
                // Split the line and add to the existing list
                Collections.addAll(burstTimes, line.split("\\s+"));
            } else {
                throw new IOException("Missing burst times in file.");
            }

            // Read the third line for priorities (if present)
            line = reader.readLine();
            if (line != null) {
                // Split the line and add to the existing list
                Collections.addAll(priority, line.split("\\s+"));
            } else {
                // If the priority line is missing, set the priority list to null
                priority.clear();
            }
        }
    }

    private Triple<List<Integer>, List<Integer>, List<Integer>> createProcessInfo(File file, boolean usePriority) {
        // Variables to store the inputs as strings
        List<String> arrivalTimesStrings = new ArrayList<>();
        List<String> burstTimesStrings = new ArrayList<>();
        List<String> priorityStrings = new ArrayList<>();

        // Get the selected approach
        String selectedApproach = approachChoice.getValue();

        try {
            // Determine the source of data based on the selected approach
            switch (selectedApproach) {
                case "Normal Input":
                    // Use user input
                    createListsFromInputs(arrivalTimesStrings, burstTimesStrings, priorityStrings);
                    break;
                case "File Input":
                    // Use file input
                    if (file != null && !file.getPath().isEmpty()) {
                        createListsFromFile(file.getPath(), arrivalTimesStrings, burstTimesStrings, priorityStrings);
                    } else {
                        showError("No file selected for File Input approach.");
                        return null;
                    }
                    break;
                case "Random Process Generation":
                    break;
                default:
                    showError("Invalid approach selected.");
                    return null;
            }

            // Convert the string lists to integer lists
            List<Integer> arrivalTimes = new ArrayList<>();
            List<Integer> burstTimes = new ArrayList<>();
            List<Integer> priorities = new ArrayList<>();

            // Check for mismatched lengths
            if (arrivalTimesStrings.size() != burstTimesStrings.size()) {
                showError("Mismatch in arrival and burst times length.");
                return null;
            }

            // Convert arrival and burst times to integers
            for (int i = 0; i < arrivalTimesStrings.size(); i++) {
                int arrivalTime = Integer.parseInt(arrivalTimesStrings.get(i));
                int burstTime = Integer.parseInt(burstTimesStrings.get(i));

                arrivalTimes.add(arrivalTime);
                burstTimes.add(burstTime);
            }

            // Handle priority list based on whether priority scheduling is being used
            if (usePriority) {
                // Check for mismatched lengths if priority scheduling is used
                if (arrivalTimesStrings.size() != priorityStrings.size()) {
                    showError("Mismatch in arrival and priority times length.");
                    return null;
                }

                // Convert priority strings to integers
                for (String priorityString : priorityStrings) {
                    priorities.add(Integer.parseInt(priorityString));
                }
            } else {
                // Set all priorities to 1 if priority scheduling is not used
                priorities = Collections.nCopies(arrivalTimes.size(), 1);
            }

            // Return the Triple with the processed lists
            return new Triple<>(arrivalTimes, burstTimes, priorities);

        } catch (IOException e) {
            showError("An error occurred while reading the file. Please check the file path.");
        } catch (NumberFormatException e) {
            showError("Invalid number format in input or file. Please ensure all values are integers.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
        return null;
    }

    private void setUpSimulation(){
        boolean priorityEnabled = false;

        processList.clear();
        cpuScheduler.clearProcesses();
        processFactory.reset();

        // Get the selected algorithm from the choice box
        String selectedAlgorithm = algorithmChoice.getValue();
        String selectedApproach = approachChoice.getValue();
        String selectedPreemption = preemptiveChoice.getValue();
        String selectedRR = roundRobinChoice.getValue();
        int quantum = Integer.parseInt(quantumField.getText());
        SchedulingAlgorithm algo = new FCFS();

        // Set the scheduling algorithm based on the selected value
        switch (selectedAlgorithm) {
            case "Shortest Job First":
                algo = new SJF();
                break;
            case "Round Robin":
                algo = new RoundRobin(quantum);
                break;
            case "Shortest Time Remaining":
//                algo = new STR();
                break;
            case "Priority Scheduling":
                priorityEnabled = true;
                if(selectedRR.equals("With Round Robin")){
                    algo = new RRPriorityScheduling(quantum);
                    break;
                }
                else if(selectedPreemption.equals("Preemptive"))
                    algo = new PrioritySchedulingPreemptive();
                else
                    algo = new PrioritySchedulingNonPreemptive();
                break;
            case "Lottery Scheduling":
//                algo = new Lottery();
                break;
        }

        cpuScheduler.setSchedulingAlgorithm(algo);
        algoLabel.setText(selectedAlgorithm);

        if(selectedApproach.equals("Random Process Generation")){
            int numberProcesses = Integer.parseInt(numProcesses.getText());
            int lastArrival = Integer.parseInt(lastArrivalInput.getText());
            int bursttime = Integer.parseInt(maxBurstTimeInput.getText());

            for(int i = 0; i < numberProcesses; i++){
                Process p = processFactory.createRandomProcess(lastArrival, bursttime, numberProcesses);
                processList.add(p);
                cpuScheduler.addProcess(p);
            }
            return;
        }

        // Get the process information (arrival times, burst times, and priorities) from createProcessInfo method
        Triple<List<Integer>, List<Integer>, List<Integer>> processInfos = createProcessInfo(selectedFile, priorityEnabled);

        // Extract arrival times, burst times, and priorities from the returned triple
        List<Integer> arrivalTimes = processInfos.left();
        List<Integer> burstTimes = processInfos.middle();
        List<Integer> priorities = processInfos.right();


        for(int i = 0; i < arrivalTimes.size(); i++){
            Process p = processFactory.createProcess(arrivalTimes.get(i), burstTimes.get(i), priorities.get(i));
            processList.add(p);
            cpuScheduler.addProcess(p);
        }


    }

    // Method to update metrics
    private void updateMetrics() {
        averageTurnaroundTime.setText(String.format("%.2f", cpuScheduler.getAverageTurnaround()));
        averageWaitingTime.setText(String.format("%.2f", cpuScheduler.getAverageWaitingTime()));
        throughput.setText(String.format("%.2f", cpuScheduler.getThroughput()));
        cpuUtilization.setText(String.format("%.2f%%", cpuScheduler.getCpuUtilization()));
        contextSwitches.setText(String.valueOf(cpuScheduler.getContextSwitches()));
        waitingTimeVariance.setText(String.format("%.2f", cpuScheduler.getWaitingTimeVariance()));
        maxWaitingTime.setText(String.valueOf(cpuScheduler.getMaxWaitingTime()));
    }

    // Method to draw the Gantt Chart
    private void drawGanttChart(List<Pair<Process, Integer>> processList) {
        GraphicsContext gc = ganttCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, ganttCanvas.getWidth(), ganttCanvas.getHeight()); // Clear the entire canvas

        double totalExecutionTime = cpuScheduler.getTotalExecutionTime();
        double unitWidth = ganttCanvas.getWidth() / totalExecutionTime; // Calculate the width of one unit of time
        double rectangleHeight = ganttCanvas.getHeight() / 12; // Adjust height of each rectangle to be smaller
        double rowSpacing = 16; // Spacing between rows
        double columnSpacing = 27;
        double cornerRadius = 10; // Corner radius for rounded rectangles
        int osTime = 0;
        double currentX = 0;
        double currentY = 0; // Start at the top of the canvas
        int currentRow = 0; // Keep track of current row

        // Map to store process colors
        Map<String, Color> processColors = new HashMap<>();
        Random random = new Random();

        // Set a random but unique color for each process
        for (Pair<Process, Integer> execution : processList) {
            if (!processColors.containsKey(execution.getKey().getProcessName())) {
                processColors.put(execution.getKey().getProcessName(), getRandomColor(random));
            }
        }

        // Draw each process as a rectangle
        for (int i = 0; i < processList.size(); i++) {
            Pair<Process, Integer> execution = processList.get(i);
            Process process = execution.getKey();
            int executionTime = execution.getValue();
            Color color = processColors.get(process.getProcessName());
            double processWidth = executionTime * unitWidth;

            // Check if current rectangle exceeds canvas width
            if (currentX + processWidth > ganttCanvas.getWidth()) {
                currentX = 0; // Reset currentX to wrap
                currentRow++; // Move to the next row
                currentY = currentRow * (rectangleHeight + columnSpacing); // Calculate new y-coordinate
            }

            // Draw the process rectangle with rounded corners and gradient fill
            gc.setFill(createGradientFill(color, currentX, currentY, processWidth, rectangleHeight));
            gc.fillRoundRect(currentX, currentY, processWidth, rectangleHeight, cornerRadius, cornerRadius);

            // Draw process ID inside the rectangle
            gc.setFill(Color.BLACK);
            double processTextY = currentY + rectangleHeight / 2 + 4; // Positioning in the center of the rectangle
            gc.fillText(process.getProcessName(), currentX + processWidth / 2, processTextY);

            // Draw osTime timestamp
            if (i == 0) {
                // For the first timestamp, draw it at the start of the chart
                gc.fillText(String.valueOf(osTime), currentX, currentY + rectangleHeight + 20);
            } else {
                // Calculate the center of the gap between rectangles
                gc.fillText(String.valueOf(osTime), currentX - rowSpacing/2 - 2, currentY + rectangleHeight + 20);
            }

            // Update osTime and currentX for the next rectangle
            osTime += executionTime;
            currentX += processWidth + rowSpacing;
        }

        // Draw the final osTime timestamp at the end of the chart
        gc.fillText(String.valueOf(osTime), currentX, currentY + rectangleHeight + 20); // Final osTime timestamp

    }


    // Method to create a gradient fill for the rectangles
    private Paint createGradientFill(Color baseColor, double startX, double startY, double width, double height) {
        Stop[] stops = new Stop[]{
                new Stop(0, baseColor.darker()), // Darker color at the start
                new Stop(1, baseColor) // Lighter color at the end
        };
        return new LinearGradient(startX, startY, startX + width, startY, false, CycleMethod.NO_CYCLE, stops);
    }

    private Color getRandomColor(Random random) {
        // Generate a random color with a specified range for each RGB value
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
