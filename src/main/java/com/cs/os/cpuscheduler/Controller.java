package com.cs.os.cpuscheduler;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Controller {

    @FXML
    private TextField arrivalTimeInput;

    @FXML
    private TextField burstTimeInput;

    @FXML
    private Button chooseFileButton;

    @FXML
    private ChoiceBox<String> algorithmChoice;

    @FXML
    private TableView<Process> processTableView;

    @FXML
    private TableColumn<Process, Integer> jobColumn;

    @FXML
    private TableColumn<Process, Float> arrivalColumn;

    @FXML
    private TableColumn<Process, Float> burstColumn;

    @FXML
    private TableColumn<Process, Float> finishColumn;

    @FXML
    private TableColumn<Process, Float> turnaroundColumn;

    @FXML
    private TableColumn<Process, Float> waitingColumn;

    private CPUScheduler cpuScheduler;
    private ObservableList<Process> processList = FXCollections.observableArrayList();

    public void initialize() {
        // Initialize CPUScheduler with a default algorithm
        cpuScheduler = new CPUScheduler(new DefaultSchedulingAlgorithm());

        // Set up the choice box for scheduling algorithms
        algorithmChoice.setItems(FXCollections.observableArrayList(
                "First-Come, First-Served",
                "Shortest Job First",
                "Round Robin"
                // Add more algorithms as needed
        ));
        algorithmChoice.getSelectionModel().selectFirst();

        // Set up columns in the table view
        jobColumn.setCellValueFactory(data -> data.getValue().processIDProperty().asObject());
        arrivalColumn.setCellValueFactory(data -> data.getValue().arrivalTimeProperty().asObject());
        burstColumn.setCellValueFactory(data -> data.getValue().burstTimeProperty().asObject());
        finishColumn.setCellValueFactory(data -> data.getValue().getCompletionTimeProperty().asObject());
        turnaroundColumn.setCellValueFactory(data -> data.getValue().turnaroundProperty().asObject());
        waitingColumn.setCellValueFactory(data -> data.getValue().waitingTimeProperty().asObject());

        // Bind the observable list to the table view
        processTableView.setItems(processList);
    }

    @FXML
    private void onAddProcess() {
        // Get input values
        String arrivalInput = arrivalTimeInput.getText();
        String burstInput = burstTimeInput.getText();

        // Split the inputs by spaces and parse them
        String[] arrivalTimes = arrivalInput.split("\\s+");
        String[] burstTimes = burstInput.split("\\s+");

        if (arrivalTimes.length != burstTimes.length) {
            showError("Mismatch in arrival and burst times length.");
            return;
        }

        ProcessFactory processFactory = new ProcessFactory();

        // Create processes from input data
        for (int i = 0; i < arrivalTimes.length; i++) {
            try {
                int processID = i + 1; // Process IDs are sequential starting from 1
                float arrivalTime = Float.parseFloat(arrivalTimes[i]);
                float burstTime = Float.parseFloat(burstTimes[i]);

                // Create a process using the process factory
                Process process = processFactory.createProcess(processID, burstTime, arrivalTime);

                // Add the process to the scheduler and observable list
                cpuScheduler.addProcess(process);
                processList.add(process);
            } catch (NumberFormatException e) {
                showError("Invalid input: Please enter valid numbers for arrival and burst times.");
                return;
            } catch (Exception e) {
                showError(e.getMessage());
                return;
            }
        }

        // Set up the scheduler after adding processes
        cpuScheduler.setUpScheduler();
    }

    @FXML
    private void onRunScheduler() {
        // Get the selected algorithm from the choice box
        String selectedAlgorithm = algorithmChoice.getValue();

        // Set the scheduling algorithm based on the selected value
        if (selectedAlgorithm.equals("First-Come, First-Served")) {
            cpuScheduler.setSchedulingAlgorithm(new FirstComeFirstServedAlgorithm());
        } else if (selectedAlgorithm.equals("Shortest Job First")) {
            cpuScheduler.setSchedulingAlgorithm(new ShortestJobFirstAlgorithm());
        } else if (selectedAlgorithm.equals("Round Robin")) {
            cpuScheduler.setSchedulingAlgorithm(new RoundRobinAlgorithm());
        }

        // Run the scheduler
        cpuScheduler.runScheduler();

        // Refresh the table view to display updated process information
        processTableView.refresh();
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
