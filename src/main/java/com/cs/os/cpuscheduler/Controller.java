package com.cs.os.cpuscheduler;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    public Label viewLabel;

    @FXML
    public Label algoLabel;

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
    private TableColumn<Process, String> jobColumn;

    @FXML
    private TableColumn<Process, Integer> arrivalColumn;

    @FXML
    private TableColumn<Process, Integer> burstColumn;

    @FXML
    private TableColumn<Process, Integer> finishColumn;

    @FXML
    private TableColumn<Process, Integer> turnaroundColumn;

    @FXML
    private TableColumn<Process, Integer> waitingColumn;

    private CPUScheduler cpuScheduler;
    private ProcessFactory processFactory = new SimpleProcessFactory();
    private ObservableList<Process> processList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize CPUScheduler with a default algorithm
        cpuScheduler = new CPUScheduler(new FCFS());

        // Set up the choice box for scheduling algorithms
        algorithmChoice.setItems(FXCollections.observableArrayList(
                "First-Come, First-Served",
                "Shortest Job First",
                "Round Robin"
                // Add more algorithms as needed
        ));
        algorithmChoice.getSelectionModel().selectFirst();

        // Set up columns in the table view
        jobColumn.setCellValueFactory(data -> new SimpleStringProperty(Process.intToAlphabet(data.getValue().getProcessID())));
        arrivalColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getArrivalTime()).asObject());
        burstColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getBurstTime()).asObject());
        finishColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCompletionTime()).asObject());
        turnaroundColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTurnaround()).asObject());
        waitingColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getWaitingTime()).asObject());

        // Bind the observable list to the table view
        processTableView.setItems(processList);
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
        Pair<List<Integer>, List<Integer>> processInfos = createProcessInfo("");
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

        // Set the scheduling algorithm based on the selected value
        switch (selectedAlgorithm) {
            case "Shortest Job First":
                cpuScheduler.setSchedulingAlgorithm(new SJF());
                break;
            case "Round Robin":
                cpuScheduler.setSchedulingAlgorithm(new RoundRobin());
                break;
            default:
                cpuScheduler.setSchedulingAlgorithm(new FCFS());
                break;
        }
        algoLabel.setText(selectedAlgorithm);

    }
    @FXML
    public void onTableViewClick() {
        // Setting Up the Simpulation
        setUpSimulation();
        viewLabel.setText("Table View");

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
