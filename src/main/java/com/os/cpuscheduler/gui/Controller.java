package com.os.cpuscheduler.gui;

import com.os.cpuscheduler.CPUScheduler;
import com.os.cpuscheduler.Process;
import com.os.cpuscheduler.ProcessFactory;
import com.os.cpuscheduler.SimpleProcessFactory;
import com.os.cpuscheduler.algorithms.*;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.File;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Flow;

import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;

public class Controller {
    public AnchorPane middlePane;
    @FXML
    private HBox quantumInputSection;
    @FXML
    private GridPane progressGrid;
    @FXML
    private ScrollPane videoSimulationView, ganttView;

    @FXML // ChoiceBox for approach selection and algorithm selection
    private ChoiceBox<String> approachChoice, algorithmChoice, preemptiveChoice, roundRobinChoice, outputChoiceBox;

    @FXML // VBox sections for different input methods
    private VBox ganttChart, normalInputSection, fileInputSection, randomProcessSection, priorityOptions, chartsView;

    @FXML
    private Label viewLabel, algoLabel, filePathLabel, averageTurnaroundTime, averageWaitingTime, throughput, cpuUtilization, contextSwitches, waitingTimeVariance, maxWaitingTime;

    @FXML // Input fields for process data input
    private TextField arrivalTimeInput, burstTimeInput, quantumField, priorityInput, ticketsInput, maxBurstTimeInput, numProcesses, lastArrivalInput;

    // TableView and columns for displaying process data
    @FXML
    private TableView<Process> processTableView;
    @FXML
    private TableColumn<Process, String> jobColumn;
    @FXML
    private TableColumn<Process, Integer> arrivalColumn, burstColumn, finishColumn, turnaroundColumn, waitingColumn;

    private Timeline timeline;

    private final CPUScheduler cpuScheduler = new CPUScheduler(new FCFS());
    private final ProcessFactory processFactory = new SimpleProcessFactory();
    private final ObservableList<Process> processList = FXCollections.observableArrayList();
    private File selectedFile;





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


//        processTableView.prefWidthProperty().bind(middlePane.widthProperty());
//        processTableView.prefHeightProperty().bind(middlePane.heightProperty());
//        ganttChart.prefWidthProperty().bind(middlePane.widthProperty());
//        ganttChart.prefHeightProperty().bind(middlePane.heightProperty());
//        chartsView.prefWidthProperty().bind(middlePane.widthProperty());
//        chartsView.prefHeightProperty().bind(middlePane.heightProperty());
        progressGrid.prefWidthProperty().bind(videoSimulationView.widthProperty());
        ganttChart.prefWidthProperty().bind(videoSimulationView.widthProperty());


        processTableView.setVisible(false);
        ganttView.setVisible(false);
        chartsView.setVisible(false);
        videoSimulationView.setVisible(false);

    }





    @FXML
    private void onOutputChoiceChange() {
        setUpSimulation(); // Set up the simulation
        cpuScheduler.runScheduler(); // Run the scheduler
        updateMetrics(); // Update the metrics

        String selectedChoice = outputChoiceBox.getValue(); // Get the selected output choice

        // Hide all output views initially
        processTableView.setVisible(false);
        ganttView.setVisible(false);
        chartsView.setVisible(false);
        videoSimulationView.setVisible(false);


        viewLabel.setText(selectedChoice);
        // Show the selected output view and call the relevant function
        switch (selectedChoice) {
            case "Table View":
                processTableView.setVisible(true);
                processTableView.refresh(); // Refresh the table view to display updated process information
                break;
            case "Gantt Chart":
                ganttView.setVisible(true);
                drawGanttChart(cpuScheduler.getProcessExecutionTimeline()); // Call the function to draw the Gantt chart if necessary
                break;
            case "Charts View":
                chartsView.setVisible(true);
                drawTimeSeriesChart(cpuScheduler.getProcessExecutionTimeline());
                break;
            case "Video Simulation":
                videoSimulationView.setVisible(true);
                drawExecutionTimeline(cpuScheduler.getProcessQueue(), cpuScheduler.getProcessExecutionTimeline());
                break;
        }
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
            case "Round Robin", "Lottery Scheduling", "Shortest Time Remaining":
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






    private void setUpSimulation(){
        int priorityEnabled = 0;

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
                algo = new SRTF(quantum);
                break;
            case "Priority Scheduling":
                priorityEnabled = 1;
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
                priorityEnabled = 2;
                algo = new LotteryScheduling(quantum);
                break;
            case "Highest Response Ratio Next":
                algo = new HRRN();
                break;
        }

        cpuScheduler.setSchedulingAlgorithm(algo);

        String addition = "";
        if(selectedAlgorithm.equals("Priority Scheduling")){
            switch (preemptiveChoice.getValue()){
                case "Non-Preemptive":
                    addition = roundRobinChoice.getValue();
                    break;
                default:
                    addition = "Preemptive";
                    break;
            }
        }

        algoLabel.setText(selectedAlgorithm + " " + addition);

        if(selectedApproach.equals("Random Process Generation")){
            int numberProcesses = Integer.parseInt(numProcesses.getText());
            int lastArrival = Integer.parseInt(lastArrivalInput.getText());
            int bursttime = Integer.parseInt(maxBurstTimeInput.getText());
            int ticketsNumber = numberProcesses * 100;

            for(int i = 0; i < numberProcesses; i++){
                Process p = processFactory.createRandomProcess(lastArrival, bursttime, numberProcesses, ticketsNumber);
                processList.add(p);
                cpuScheduler.addProcess(p);
            }
            return;
        }

        // Get the process information (arrival times, burst times, and priorities) from createProcessInfo method
        Quadruple<List<Integer>, List<Integer>, List<Integer>, List<Integer>> processInfos = createProcessInfo(selectedFile, priorityEnabled);

        // Extract arrival times, burst times, and priorities from the returned triple
        List<Integer> arrivalTimes = processInfos.first();
        List<Integer> burstTimes = processInfos.second();
        List<Integer> priorities = processInfos.third();
        List<Integer> tickets = processInfos.fourth();


        for(int i = 0; i < arrivalTimes.size(); i++){
            Process p = processFactory.createProcess(arrivalTimes.get(i), burstTimes.get(i), priorities.get(i), tickets.get(i));
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








    private void drawGanttChart(List<Pair<Process, Integer>> processList) {
        // Clear existing content in the FlowPane
        ganttChart.getChildren().clear();

        // Calculate necessary dimensions and padding
        double unitWidth = (ganttChart.getWidth() - 80) / (cpuScheduler.getMaxExecutionTime() * 6);
        double rectangleHeight = 30; // Adjustable rectangle height
        double cornerRadius = 10;

        // Map to store process colors
        Map<String, Color> processColors = new HashMap<>();
        Random random = new Random();

        // Assign a unique color for each process
        for (Pair<Process, Integer> execution : processList) {
            Process process = execution.getKey();
            if (process != null && !processColors.containsKey(process.getProcessName())) {
                processColors.put(process.getProcessName(), getRandomColor(random));
            }
        }

        // Create a FlowPane for the Gantt chart with centered alignment and automatic wrapping
        FlowPane ganttPane = new FlowPane();
        ganttPane.setAlignment(Pos.TOP_LEFT);
        ganttPane.setHgap(10); // Horizontal gap
        ganttPane.setVgap(10); // Vertical gap

        int osTime = 0; // Tracks the time of execution

        // Iterate through the process list to draw each process segment
        for (Pair<Process, Integer> execution : processList) {
            Process process = execution.getKey();
            int executionTime = execution.getValue();

            double segmentWidth = executionTime * unitWidth;
            Node processNode;

            // Add the final timestamp label
            Label timeStamp = new Label(String.valueOf(osTime));
            timeStamp.setTextFill(Color.WHITE);
            ganttPane.getChildren().add(timeStamp);

            if (process == null) {
                // Handle idle time (gap)
                Rectangle gapLine = new Rectangle(segmentWidth, 2);
                gapLine.setFill(Color.WHITE);
                processNode = gapLine;
            } else {
                // Handle process execution
                Paint gradientFill = createGradientFill(processColors.get(process.getProcessName()), 0, 0, segmentWidth);

                Rectangle processRect = new Rectangle(segmentWidth, rectangleHeight);
                processRect.setArcWidth(cornerRadius);
                processRect.setArcHeight(cornerRadius);
                processRect.setFill(gradientFill);

                Label processLabel = new Label(process.getProcessName());
                processLabel.setTextFill(Color.WHITE);

                // Combine the rectangle and label in a StackPane
                StackPane processStack = new StackPane();
                processStack.getChildren().addAll(processRect, processLabel);
                processNode = processStack;
            }

            // Add the process node to the FlowPane
            ganttPane.getChildren().add(processNode);

            // Increment the current time
            osTime += executionTime;
        }

        // Add the final timestamp label
        Label finalTimestampLabel = new Label(String.valueOf(osTime));
        finalTimestampLabel.setTextFill(Color.WHITE);
        ganttPane.getChildren().add(finalTimestampLabel);

        // Add the FlowPane (ganttPane) to the ganttChart VBox
        ganttChart.getChildren().add(ganttPane);
    }

    private void drawTimeSeriesChart(List<Pair<Process, Integer>> executionTimeline) {
        // Map to track the color for each process
        Map<Integer, Color> processColorMap = new HashMap<>();
        Random random = new Random(); // For generating random colors

        // Create a list to hold each series separately
        List<XYChart.Series<Number, Number>> allSeries = new ArrayList<>();

        // Initialize current time
        int currentTime = 0;

        // Iterate through the execution timeline
        for (Pair<Process, Integer> entry : executionTimeline) {
            Process process = entry.getKey();
            Integer executionTime = entry.getValue();

            if (process == null) {
                // Draw a gap by increasing currentTime and continuing without adding a line
                currentTime += executionTime;
                continue;
            }

            // Use computeIfAbsent to assign a new color only if the process is not already in the map
            processColorMap.computeIfAbsent(process.getProcessID(), k -> getRandomColor(random));

            // Create a new series for the current execution
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(process.getProcessName());

            // Add data points to represent the start and end of execution time
            series.getData().add(new XYChart.Data<>(currentTime, process.getProcessID()));
            series.getData().add(new XYChart.Data<>(currentTime + executionTime, process.getProcessID()));

            // Add the new series to the list of all series
            allSeries.add(series);

            // Update the current time
            currentTime += executionTime;
        }

        // Create a LineChart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time");
        yAxis.setLabel("Process Execution");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Execution Timeline");

        // Hide the default legend
        lineChart.setLegendVisible(false);

        // Add each series to the chart
        for (XYChart.Series<Number, Number> series : allSeries) {
            lineChart.getData().add(series);
            // Apply color to the data points of the new series
            Color color = processColorMap.get(series.getData().getFirst().getYValue());

            series.getNode().setStyle(String.format("-fx-stroke: rgb(%d, %d, %d);",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255)));

            // Iterate through the data points in the series
            for (XYChart.Data<Number, Number> data : series.getData()) {
                // Get the symbol node for the data point
                Node symbolNode = data.getNode();

                symbolNode.setStyle(String.format("-fx-background-color: rgb(%d, %d, %d);",
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255)));
            }
        }

        // Create custom legend
        FlowPane customLegend = createCustomLegend(processColorMap);

        // Add the custom legend to the chartsView along with the line chart
        chartsView.getChildren().clear();
        chartsView.getChildren().addAll(lineChart, customLegend);
    }

    private FlowPane createCustomLegend(Map<Integer, Color> processColorMap) {
        // Create a FlowPane for the custom legend and center its children
        FlowPane customLegend = new FlowPane();
        customLegend.setAlignment(Pos.CENTER);

        // Set spacing between children for better visual separation
        customLegend.setHgap(15); // Horizontal gap between children
        customLegend.setVgap(10); // Vertical gap between lines

        // Iterate through the process color map to create legend entries
        for (Map.Entry<Integer, Color> entry : processColorMap.entrySet()) {
            String processName = Process.intToAlphabet(entry.getKey());
            Color color = entry.getValue();

            // Create a small circle with the process color
            Circle colorCircle = new Circle(5);
            colorCircle.setFill(color);

            // Create a label for the process name with white text color
            Label legendLabel = new Label(processName);
            legendLabel.setStyle("-fx-text-fill: white;");

            // Create an HBox for the legend entry (circle and label)
            HBox legendEntry = new HBox(colorCircle, legendLabel);
            legendEntry.setSpacing(3);
            legendEntry.setAlignment(Pos.CENTER);

            // Add the legend entry to the custom legend FlowPane
            customLegend.getChildren().add(legendEntry);
        }

        return customLegend;
    }

    private void drawExecutionTimeline(List<Process> processQueue, List<Pair<Process, Integer>> executionTimeline) {
        // Stop any existing timeline
        if (timeline != null) {
            timeline.stop();
        }

        // Create and initialize maps for process progress and colors
        Map<Integer, Double> processProgressMap = new HashMap<>();
        Map<Integer, Color> processColorMap = new HashMap<>();
        int numberOfProcesses = processQueue.size();

        // Clear the GridPane before adding new components
        Platform.runLater(() -> progressGrid.getChildren().clear());

        // Initialize a random number generator for process colors
        Random random = new Random();

        processQueue.sort(Comparator.comparingInt(Process::getProcessID));

        // Initialize progress and colors for each process
        for (Process process : processQueue) {
            processProgressMap.put(process.getProcessID(), 0.0);
            processColorMap.put(process.getProcessID(), getRandomColor(random));
        }

        // Set up the GridPane layout with process progress bars and labels
        for (int i = 0; i < numberOfProcesses; i++) {
            Process process = processQueue.get(i);

            // Create a ProgressBar for the process
            ProgressBar progressBar = new ProgressBar(0);
            Color processColor = processColorMap.get(process.getProcessID());
            progressBar.setStyle(String.format("-fx-accent: rgb(%d, %d, %d);",
                    (int) (processColor.getRed() * 255),
                    (int) (processColor.getGreen() * 255),
                    (int) (processColor.getBlue() * 255)));
            progressBar.setPrefWidth(progressGrid.getPrefWidth() / 3); // Set preferred width
            progressBar.setMinHeight(20);

            // Create a Label for the process name
            Label processLabel = new Label("Process " + process.getProcessName());
            processLabel.setTextFill(Color.WHITE);

            // Create a Label to display the percentage of completeness
            Label progressPercentageLabel = new Label("0.00%");
            progressPercentageLabel.setTextFill(Color.WHITE);

            // Combine these elements into a VBox
            VBox processBox = new VBox();
            processBox.getChildren().addAll(processLabel, progressBar, progressPercentageLabel);
            processBox.setSpacing(5);

            // Calculate the grid position (two columns, alternating rows)
            int row = i / 2;
            int col = i % 2;

            // Add the custom component to the GridPane at the calculated position
            Platform.runLater(() -> progressGrid.add(processBox, col, row));
        }

        // Create a Timeline to update the execution every 1 second
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            if (!executionTimeline.isEmpty()) {
                Pair<Process, Integer> pair = executionTimeline.getFirst();
                Process process = pair.getKey();
                int executionTime = pair.getValue();

                // Process execution
                if (process != null) {
                    // Calculate the current progress
                    Double currentProgress = processProgressMap.get(process.getProcessID());
                    double progressPercentage = (double) 1 / process.getBurstTime();
                    double newProgress = currentProgress + progressPercentage;

                    // Update the progress bar and label for the process
                    Platform.runLater(() -> {
                        VBox processBox = (VBox) progressGrid.getChildren()
                                .stream()
                                .filter(node -> GridPane.getRowIndex(node) == (process.getProcessID()-1) / 2
                                        && GridPane.getColumnIndex(node) == (process.getProcessID()-1) % 2)
                                .findFirst()
                                .orElse(null);

                        if (processBox != null) {
                            ProgressBar progressBar = (ProgressBar) processBox.getChildren().get(1);
                            Label progressLabel = (Label) processBox.getChildren().get(2);

                            progressBar.setProgress(newProgress);
                            progressLabel.setText(String.format("%.2f%%", newProgress * 100));
                        }
                    });

                    // Update the progress map
                    processProgressMap.put(process.getProcessID(), newProgress);
                }

                // Manage the execution timeline
                executionTimeline.removeFirst();
                if (executionTime > 1) {
                    executionTimeline.add(0, new Pair<>(process, executionTime - 1));
                }
            }
        }));

        // Set the timeline to repeat indefinitely
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }







    public record Quadruple<A, B, C, D>(A first, B second, C third, D fourth) {}

    private void createListsFromInputs(List<String> arrivalTimes, List<String> burstTimes, List<String> priority, List<String> tickets) {
        // Clear the existing lists
        arrivalTimes.clear();
        burstTimes.clear();
        priority.clear();
        tickets.clear();

        // Split inputs and add them to the existing lists
        Collections.addAll(arrivalTimes, arrivalTimeInput.getText().split("\\s+"));
        Collections.addAll(burstTimes, burstTimeInput.getText().split("\\s+"));
        Collections.addAll(priority, priorityInput.getText().split("\\s+"));
        Collections.addAll(tickets, ticketsInput.getText().split("\\s+"));
    }

    private void createListsFromFile(String filePath, List<String> arrivalTimes, List<String> burstTimes, List<String> priority, List<String> tickets) throws IOException {
        // Clear the existing lists
        arrivalTimes.clear();
        burstTimes.clear();
        priority.clear();
        tickets.clear();

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
            }

            // Read the third line for priorities (if present)
            line = reader.readLine();
            if (line != null) {
                // Split the line and add to the existing list
                Collections.addAll(tickets, line.split("\\s+"));
            }

        }
    }

    private Quadruple<List<Integer>, List<Integer>, List<Integer>, List<Integer>> createProcessInfo(File file, int usePriority) {
        // Variables to store the inputs as strings
        List<String> arrivalTimesStrings = new ArrayList<>();
        List<String> burstTimesStrings = new ArrayList<>();
        List<String> priorityStrings = new ArrayList<>();
        List<String> ticketsStrings = new ArrayList<>();

        // Get the selected approach
        String selectedApproach = approachChoice.getValue();

        try {
            // Determine the source of data based on the selected approach
            switch (selectedApproach) {
                case "Normal Input":
                    // Use user input
                    createListsFromInputs(arrivalTimesStrings, burstTimesStrings, priorityStrings, ticketsStrings);
                    break;
                case "File Input":
                    // Use file input
                    if (file != null && !file.getPath().isEmpty()) {
                        createListsFromFile(file.getPath(), arrivalTimesStrings, burstTimesStrings, priorityStrings, ticketsStrings);
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

            List<Integer> priorities = Collections.nCopies(arrivalTimes.size(), 1);
            List<Integer> tickets = Collections.nCopies(arrivalTimes.size(), 1);;

            // Handle priority list based on whether priority scheduling is being used
            if (usePriority == 1) {
                priorities = new ArrayList<>();
                // Check for mismatched lengths if priority scheduling is used
                if (arrivalTimesStrings.size() != priorityStrings.size()) {
                    showError("Mismatch in arrival and priority times length.");
                    return null;
                }

                // Convert priority strings to integers
                for (String priorityString : priorityStrings) {
                    priorities.add(Integer.parseInt(priorityString));
                }
            } else if(usePriority == 2){
                tickets = new ArrayList<>();
                // Check for mismatched lengths if priority scheduling is used
                if (arrivalTimesStrings.size() != ticketsStrings.size()) {
                    showError("Mismatch in arrival and tickets times length.");
                    return null;
                }

                // Convert priority strings to integers
                for (String ticketString : ticketsStrings) {
                    tickets.add(Integer.parseInt(ticketString));
                }
            }

            // Return the Triple with the processed lists
            return new Quadruple<>(arrivalTimes, burstTimes, priorities, tickets);

        } catch (IOException e) {
            showError("An error occurred while reading the file. Please check the file path.");
        } catch (NumberFormatException e) {
            showError("Invalid number format in input or file. Please ensure all values are integers.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
        return null;
    }









    // Method to create a gradient fill for the rectangles
    private Paint createGradientFill(Color baseColor, double startX, double startY, double width) {
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
