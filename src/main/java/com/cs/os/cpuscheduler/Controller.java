package com.cs.os.cpuscheduler;

import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML
    public HBox quantumInputSection;
    public VBox chartsView;
    public BarChart histogramChart;

    @FXML // ChoiceBox for approach selection and algorithm selection
    private ChoiceBox<String> approachChoice, algorithmChoice, preemptiveChoice, agingChoice, roundRobinChoice, outputChoiceBox;

    @FXML // VBox sections for different input methods
    private VBox normalInputSection, fileInputSection, randomProcessSection, priorityOptions;

    @FXML // Labels for view and algorithm titles
    public Label viewLabel, algoLabel;

    @FXML // Input fields for process data input
    private TextField arrivalTimeInput, burstTimeInput, quantumField, priorityInput, ticketsInput;

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
    private ScrollPane ganttScrollPane;


    private CPUScheduler cpuScheduler = new CPUScheduler(new FCFS());
    private ProcessFactory processFactory = new SimpleProcessFactory();
    private ObservableList<Process> processList = FXCollections.observableArrayList();

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
            default:
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
        // Set up the simulation
//        setUpSimulation();

        // Get the selected output choice
        String selectedChoice = outputChoiceBox.getValue();

        // Hide all output views initially
        processTableView.setVisible(false);
        ganttScrollPane.setVisible(false);
        chartsView.setVisible(false);
//        videoSimulationView.setVisible(false);

        // Show the selected output view and call the relevant function
        switch (selectedChoice) {
            case "Table View":
                processTableView.setVisible(true);
                setUpSimulation();
                onTableViewClick();
                break;
            case "Gantt Chart":
                ganttScrollPane.setVisible(true);
                // Call the function to draw the Gantt chart if necessary
                List<Process> processList = processTableView.getItems();
                drawGanttChart(processList);
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


    public void onTableViewClick() {
        // Setting Up the Simpulation
        viewLabel.setText("Table View");

        // Run the scheduler
        cpuScheduler.runScheduler();

        // Refresh the table view to display updated process information
        processTableView.refresh();
    }

    private Pair<List<String>, List<String>> createListsFromInputs() {

        // Split inputs and add them to the lists
        List<String> arrivalTimes = new ArrayList<>(List.of(arrivalTimeInput.getText().split("\\s+")));
        List<String> burstTimes = new ArrayList<>(List.of(burstTimeInput.getText().split("\\s+")));

        return new Pair<>(arrivalTimes, burstTimes);
    }

    private Pair<List<String>, List<String>> createListsFromFile(String filePath) throws IOException {
        List<String> arrivalTimes;
        List<String> burstTimes;

        // Create a BufferedReader to read the file
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // Read the first line for arrival times
        String line = reader.readLine();
        if (line != null) {
            // Split the line and add to the list
            arrivalTimes = new ArrayList<>(List.of(line.split("\\s+")));
        } else throw new IOException();

        // Read the second line for burst times
        line = reader.readLine();
        if (line != null) {
            // Split the line and add to the list
            burstTimes = new ArrayList<>(List.of(line.split("\\s+")));
        } else throw new IOException();

        return new Pair<>(arrivalTimes, burstTimes);
    }

    private Pair<List<Integer>, List<Integer>> createProcessInfo(String filePath) {
        List<String> arrivalTimesStrings;
        List<String> burstTimesStrings;

        try {
            // Determine source of data
            if (filePath != null && !filePath.isEmpty()) {
                // Use file input
                Pair<List<String>, List<String>> listsFromFile = createListsFromFile(filePath);
                arrivalTimesStrings = listsFromFile.getKey();
                burstTimesStrings = listsFromFile.getValue();
            } else {
                // Use user input
                Pair<List<String>, List<String>> listsFromInputs = createListsFromInputs();
                arrivalTimesStrings = listsFromInputs.getKey();
                burstTimesStrings = listsFromInputs.getValue();
            }

            // Convert string lists to integer lists
            List<Integer> arrivalTimes = new ArrayList<>();
            List<Integer> burstTimes = new ArrayList<>();

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

            return new Pair<>(arrivalTimes, burstTimes);

        } catch (IOException e) {
            showError("An error occurred while reading the file. Please check the file path.");
        } catch (NumberFormatException e) {
            showError("Invalid number format in input or file. Please ensure all values are integers.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }

        // Return null if there is an error
        return null;
    }

    private void setUpSimulation(){
        Pair<List<Integer>, List<Integer>> processInfos = createProcessInfo(null);
        List<Integer> arrivalTimes = processInfos.getKey();
        List<Integer> burstTimes = processInfos.getValue();

        processList.clear();
        cpuScheduler.clearProcesses();

        for(int i = 0; i < processInfos.getKey().size(); i++){
            Process p = processFactory.createProcess(arrivalTimes.get(i), burstTimes.get(i));
            processList.add(p);
            cpuScheduler.addProcess(p);
        }

        // Get the selected algorithm from the choice box
        String selectedAlgorithm = algorithmChoice.getValue();
        String selectedPreemption = preemptiveChoice.getValue();
        String selectedRR = roundRobinChoice.getValue();
        SchedulingAlgorithm algo = new FCFS();

        // Set the scheduling algorithm based on the selected value
        switch (selectedAlgorithm) {
            case "Shortest Job First":
                algo = new SJF();
                break;
            case "Round Robin":
                algo = new RoundRobin(1);
                break;
            case "Shortest Time Remaining":
//                cpuScheduler.setSchedulingAlgorithm(new STR());
                break;
            case "Priority Scheduling":
                if(selectedRR.equals("With Round Robin"))
//                    algo = new RRPriorityScheduling();
                    break;
                else if(selectedPreemption.equals("Preemptive"))
                    algo = new PrioritySchedulingPreemptive();
                else
                    algo = new PrioritySchedulingNonPreemptive();
                break;
            case "Lottery Scheduling":
//                cpuScheduler.setSchedulingAlgorithm(new Lottery());
                break;
        }
        cpuScheduler.setSchedulingAlgorithm(algo);
        algoLabel.setText(selectedAlgorithm);

    }



    // Method to draw the Gantt Chart
    public void drawGanttChart(List<Process> processList) {
        GraphicsContext gc = ganttCanvas.getGraphicsContext2D();
        double unitWidth = ganttCanvas.getWidth() / cpuScheduler.getTotalExecutionTime(); // Calculate the width of one unit of time
        double rectangleHeight = ganttCanvas.getHeight() / 2;  // Height of each rectangle
        double spacing = 2;  // Spacing between rectangles

        // Map to store process colors
        Map<String, Color> processColors = new HashMap<>();
        Random random = new Random();

        // Set a random but unique color for each process
        for (Process process : processList) {
            if (!processColors.containsKey(process.getProcessName())) {
                processColors.put(process.getProcessName(), getRandomColor(random));
            }
        }

        double currentX = 0;

        // Draw each process as a rectangle
        for (Process process : processList) {
            Color color = processColors.get(process.getProcessName());
            gc.setFill(color);

            double processWidth = process.getExecutionTime() * unitWidth;
            gc.fillRect(currentX, 0, processWidth, rectangleHeight);

            // Draw process ID and timestamps
            gc.setFill(Color.BLACK);
            gc.fillText(process.getProcessName(), currentX + processWidth / 2, rectangleHeight / 4);  // Center the ID
            gc.fillText(String.valueOf(currentX), currentX, rectangleHeight + 15);
            gc.fillText(String.valueOf(currentX + processWidth), currentX + processWidth, rectangleHeight + 15);

            // Update the x-coordinate for the next rectangle
            currentX += processWidth + spacing;
        }
    }

    // Helper method to get a random color
    private Color getRandomColor(Random random) {
        double red = random.nextDouble();
        double green = random.nextDouble();
        double blue = random.nextDouble();
        return new Color(red, green, blue, 1.0);  // Random color with full opacity
    }




    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
