package com.cs.os.cpuscheduler;

import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import javafx.embed.swing.SwingNode;


import java.io.File;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.util.Duration;

public class Controller {
    @FXML
    public HBox quantumInputSection;
    @FXML
    public AnchorPane visualizationDiv;

    @FXML // ChoiceBox for approach selection and algorithm selection
    private ChoiceBox<String> approachChoice, algorithmChoice, preemptiveChoice, roundRobinChoice, outputChoiceBox;

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

    @FXML
    private Canvas videoSimulationView;
    private Timeline timeline;

    private CPUScheduler cpuScheduler = new CPUScheduler(new FCFS());
    private ProcessFactory processFactory = new SimpleProcessFactory();
    private ObservableList<Process> processList = FXCollections.observableArrayList();
    private File selectedFile;


    public record Quadruple<A, B, C, D>(A first, B second, C third, D fourth) {}


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
        videoSimulationView.widthProperty().bind(visualizationDiv.widthProperty());


        processTableView.setVisible(false);
        ganttCanvas.setVisible(false);
        chartsView.setVisible(false);
        videoSimulationView.setVisible(false);

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




    // Utility method to get the color for each process
    private String getProcessColor(int processID) {
        // Generate a color based on the process ID using HSB color model
        Color color = Color.hsb((processID * 50) % 360, 0.7, 0.7);
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }


    public void drawBarChart(List<Pair<Process, Integer>> executionTimeline) {
        // Create a TaskSeriesCollection to hold the data for the Gantt chart
        TaskSeriesCollection collection = new TaskSeriesCollection();

        // Create a TaskSeries for each process
        TaskSeries taskSeries = new TaskSeries("Processes");

        // Initialize the current time to start drawing from zero
        int currentTime = 0;

        // Iterate through the execution timeline
        for (Pair<Process, Integer> entry : executionTimeline) {
            Process process = entry.getKey();
            Integer executionTime = entry.getValue();

            if (process == null) {
                currentTime += executionTime;
                continue;
            }

            // Determine the start and end time for the process execution
            int startTime = currentTime;
            int endTime = currentTime + executionTime;

            // Create a Task for the process execution
            Task task = new Task(process.getProcessName(), new Date(startTime * 1000L), new Date(endTime * 1000L));

            // Add the task to the task series
            taskSeries.add(task);

            // Update the current time to the end of the process execution
            currentTime = endTime;
        }

        // Add the task series to the collection
        collection.add(taskSeries);

        // Create the Gantt chart using the collection
        JFreeChart ganttChart = ChartFactory.createGanttChart(
                "Execution Timeline",
                "Process",
                "Time",
                collection,
                true, // Legend
                true, // Tooltips
                false // URLs
        );

        // Customize the Gantt chart if needed
        CategoryPlot plot = (CategoryPlot) ganttChart.getPlot();
        GanttRenderer renderer = new GanttRenderer();
        plot.setRenderer(renderer);

        // Create a SwingNode to embed the Gantt chart in the JavaFX HBox
        SwingNode swingNode = new SwingNode();

        // Set the Swing content in a Runnable to be executed on the Swing thread
        Platform.runLater(() -> {
            // Create a ChartPanel for the Gantt chart and set it as the SwingNode content
            ChartPanel chartPanel = new ChartPanel(ganttChart);
            swingNode.setContent(chartPanel);
        });

        // Clear any existing children in the HBox
        chartsView.getChildren().clear();

        // Add the SwingNode to the HBox
        chartsView.getChildren().add(swingNode);
    }

    // Function to draw the execution timeline with animation
    private void drawExecutionTimeline(List<Process> processQueue, List<Pair<Process, Integer>> executionTimeline) {
        if (timeline != null) {
            timeline.stop(); // Stop the previous timeline if it exists
        }

        Map<Integer, Double> processProgressMap = new HashMap<>();
        Map<Integer, Color> processColorMap = new HashMap<>();
        int numberOfProcesses = processQueue.size();

        // Calculate the height of each bar with some spacing
        double barHeight = 50;
        double spacing = 20;

        videoSimulationView.setHeight((barHeight+spacing) * numberOfProcesses);

        GraphicsContext gc = videoSimulationView.getGraphicsContext2D();
        double canvasWidth = videoSimulationView.getWidth() * 0.65;

        gc.clearRect(0, 0, videoSimulationView.getWidth(), videoSimulationView.getHeight()); // Clear the entire canvas

        // Clear the canvas
        /*gc.clearRect(0, 0, canvasWidth, canvasHeight);*/


        // Initialize progress for each process
        for (Process p : processQueue) {
            processProgressMap.put(p.getProcessID(), 0.0);
            processColorMap.put(p.getProcessID(), getRandomColor());
        }

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14)); // Set font style

        for (int i = 0; i < numberOfProcesses; i++) {
            // Draw process name
            Process process = processQueue.get(i);
            Text text = new Text("Process " + process.getProcessName());
            text.setFont(gc.getFont());
            double textWidth = text.getBoundsInLocal().getWidth(); // Get the width of the text
            double textX = (100 - textWidth) / 2; // Calculate x position to center the text
            double textY = (i * (barHeight + spacing)) + (barHeight / 2) + 7; // Calculate y position to center the text vertically
            gc.fillText("Process " + process.getProcessName(), textX, textY);

            // Draw the outline of the bar
            gc.setStroke(Color.WHITE);
            gc.strokeRect(100, i * (barHeight + spacing), canvasWidth, barHeight);

        }

        // Create a Timeline to update the execution every 1 second
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            // Your handle code here
            if (!executionTimeline.isEmpty()) {
                Pair<Process, Integer> pair = executionTimeline.get(0);
                Process process = pair.getKey();
                int executionTime = pair.getValue();

                if (process != null) {

                    Double currentProgress = processProgressMap.get(process.getProcessID());

                    // Calculate the percentage of completion
                    double progressPercentage = (double) 1 / process.getBurstTime();

                    // Calculate the width of the filled part of the bar
                    double filledWidth = progressPercentage * canvasWidth;

                    // Draw the filled part of the bar
                    gc.setFill(processColorMap.get(process.getProcessID()));
                    gc.fillRect(100 + currentProgress * canvasWidth, (process.getProcessID() - 1) * (barHeight + spacing), filledWidth, barHeight);

                    // Draw the percentage background
                    double progressX = 100 + canvasWidth + 10; // Offset from the right side of the bar
                    double progressY = ((process.getProcessID() - 1) * (barHeight + spacing)) + (barHeight / 2) + 7;
                    double textBgWidth = 75; // Width of the background rectangle
                    double textBgHeight = barHeight + 0.2 * barHeight; // Height of the background rectangle
                    gc.setFill(Color.web("#2c2f33")); // Background color of the text
                    gc.fillRect(progressX, progressY - textBgHeight, textBgWidth, textBgHeight);

                    // Draw the percentage text
                    gc.setFill(Color.WHITE);
                    gc.fillText(String.format("%.2f%%", (currentProgress + progressPercentage) * 100), progressX, progressY);

                    // Update the progress
                    processProgressMap.put(process.getProcessID(), progressPercentage + currentProgress);
                }

                executionTimeline.remove(0);
                if (executionTime != 1) {
                    executionTimeline.add(0, new Pair<>(process, executionTime - 1));
                }

            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE); // Run indefinitely
        timeline.play();
    }


    // Function to get a random color
    private Color getRandomColor() {
        double red = Math.random();
        double green = Math.random();
        double blue = Math.random();
        return new Color(red, green, blue, 1);
    }


    @FXML
    private void onOutputChoiceChange() {
        setUpSimulation(); // Set up the simulation
        cpuScheduler.runScheduler(); // Run the scheduler

        updateMetrics();

        String selectedChoice = outputChoiceBox.getValue(); // Get the selected output choice

        // Hide all output views initially
        processTableView.setVisible(false);
        ganttCanvas.setVisible(false);
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
                ganttCanvas.setVisible(true);
                drawGanttChart(cpuScheduler.getProcessExecutionTimeline()); // Call the function to draw the Gantt chart if necessary
                break;
            case "Charts View":
                chartsView.setVisible(true);
                drawBarChart(cpuScheduler.getProcessExecutionTimeline());
                break;
            case "Video Simulation":
                videoSimulationView.setVisible(true);
                drawExecutionTimeline(cpuScheduler.getProcessQueue(), cpuScheduler.getProcessExecutionTimeline());
                break;
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
        Collections.addAll(priority, ticketsInput.getText().split("\\s+"));
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
                if (arrivalTimesStrings.size() != priorityStrings.size()) {
                    showError("Mismatch in arrival and priority times length.");
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
        }

        cpuScheduler.setSchedulingAlgorithm(algo);
        algoLabel.setText(selectedAlgorithm);

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

        double leftPadding = 20;
        double rightPadding = 20;

        double currentX = leftPadding;
        double currentY = 0; // Start at the top of the canvas
        int currentRow = 0; // Keep track of current row

        // Map to store process colors
        Map<String, Color> processColors = new HashMap<>();
        Random random = new Random();

        // Set a random but unique color for each process
        for (Pair<Process, Integer> execution : processList) {
            if (execution.getKey() != null && !processColors.containsKey(execution.getKey().getProcessName())) {
                processColors.put(execution.getKey().getProcessName(), getRandomColor(random));
            }
        }

        // Draw each process as a rectangle
        for (int i = 0; i < processList.size(); i++) {
            Pair<Process, Integer> execution = processList.get(i);
            Process process = execution.getKey();
            int executionTime = execution.getValue();


            if (process == null) {
                // When process is null, draw a white horizontal line to represent the gap
                double gapWidth = executionTime * unitWidth;
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2); // Set the line width as desired
                gc.strokeLine(currentX, currentY + rectangleHeight / 2, currentX + gapWidth, currentY + rectangleHeight / 2);
                osTime += executionTime;
                currentX += gapWidth + rowSpacing;
                continue;
            }


            Color color = processColors.get(process.getProcessName());
            double processWidth = executionTime * unitWidth;

            // Check if current rectangle exceeds canvas width
            if (currentX + processWidth > ganttCanvas.getWidth() - rightPadding) {
                currentX = leftPadding; // Reset currentX to wrap
                currentRow++; // Move to the next row
                currentY = currentRow * (rectangleHeight + columnSpacing); // Calculate new y-coordinate
            }

            // Draw the process rectangle with rounded corners and gradient fill
            gc.setFill(createGradientFill(color, currentX, currentY, processWidth, rectangleHeight));
            gc.fillRoundRect(currentX, currentY, processWidth, rectangleHeight, cornerRadius, cornerRadius);

            // Draw process ID inside the rectangle
            gc.setFill(Color.WHITE);
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
