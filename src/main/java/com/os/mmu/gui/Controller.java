package com.os.mmu.gui;

import com.os.mmu.*;
import com.os.mmu.strategies.*;
import com.os.Process;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {
    public TextField memorySizeInput;
    public TextField unitSizeInput;
    public ComboBox<Process> processComboBox;
    public TextField logicalAddressInput;
    public Label physicalAddressLabel;
    public HBox memoryVisual;
    @FXML
    private ComboBox<String> strategyComboBox;

    @FXML
    private TextField processInput;

    @FXML
    private Pane memoryPane;

    @FXML
    private ListView<String> processListView;

    @FXML
    private PieChart memoryPieChart;

    private MemoryManager memoryManager = new MemoryManagementUnit(10000, 1);;
    private final MemoryProcessFactory processFactory = new SimpleProcessFactory();
    private final StrategyFactory strategyFactory = StrategyFactory.getInstance();

    private List<Process> processes = new ArrayList<>();

    @FXML
    public void initialize() {

        memoryPane.prefWidthProperty().bind(memoryVisual.widthProperty());
        memoryPane.setPrefHeight(50);

        processComboBox.setCellFactory(param -> new ListCell<Process>() {
            @Override
            protected void updateItem(Process process, boolean empty) {
                super.updateItem(process, empty);

                if (empty || process == null) {
                    setText(null);
                } else {
                    setText("Process ID: " + process.getProcessID() + ", Size: " + process.getSize() + " KB");
                }
            }
        });

        // Set up the string converter to convert process objects to strings for display in the ComboBox
        processComboBox.setConverter(new StringConverter<Process>() {
            @Override
            public String toString(Process process) {
                if (process == null) {
                    return null;
                } else {
                    return "Process ID: " + process.getProcessID() + ", Size: " + process.getSize() + " KB";
                }
            }

            @Override
            public Process fromString(String string) {
                // Not needed for ComboBox, but required by StringConverter interface
                return null;
            }
        });
    }


    @FXML
    private void onStrategySelected() {
        String selectedStrategy = strategyComboBox.getValue();
        Integer strat;
        // Set the memory management strategy based on the selected item
        switch (selectedStrategy) {
            case "First Fit":
                strat = 1; // You may need to define a method to set the strategy
                break;
            case "Next Fit":
                strat = 2;
                break;
            case "Best Fit":
                strat = 3;
                break;
            case "Worst Fit":
                strat = 4;
                break;
            default:
                strat = 1;
                break;
        }
        memoryManager.setImplementation(strategyFactory.createStrategy(strat));
    }


    @FXML
    private void updateMemorySettings() {
        // Get memory size and unit size from input fields
        String memorySizeText = memorySizeInput.getText();
        String unitSizeText = unitSizeInput.getText();

        // Validate input values
        try {
            long memorySize = Long.parseLong(memorySizeText);
            long unitSize = Long.parseLong(unitSizeText);

            // Update memory manager settings
            memoryManager.setParams(memorySize, unitSize);
            memoryPane.getChildren().clear();
            processListView.getItems().clear();
            processComboBox.getItems().clear();
            processes.clear();
            updateMemoryVisualization();
        } catch (NumberFormatException e) {
            // Handle invalid input format
            showError("Invalid input format for memory size or unit size.");
        }
    }


    @FXML
    public void addProcess() {
        String input = processInput.getText();
        if (!input.isEmpty()) {
            try {
                long requestedMemory = Long.parseLong(input);
                Process process = processFactory.createProcess(requestedMemory);
                Segment allocated = memoryManager.allocate(process);
                if (allocated != null) {
                    processListView.getItems().add("Process ID: " + process.getProcessID() +
                            ", Base: " + allocated.getBase() + ", Limit: " + allocated.getLimit() * memoryManager.getUnit() + " KB");
                    processes.add(process); // Add the process ID to the list
                    updateProcessComboBox();
                    updateMemoryVisualization();
                }
            } catch (Exception e) {
                // Display error message for allocation exception
                showError(e.getMessage());
            }
        }
    }

    private void updateProcessComboBox() {
        ObservableList<Process> processList = FXCollections.observableArrayList(processes);
        processComboBox.setItems(processList);
    }

    @FXML
    public void generateRandomProcess() {
        Random random = new Random();
        long requestedMemory = random.nextInt(100) + 1; // Random memory size between 1 and 100 KB
        Process process = processFactory.createProcess(requestedMemory);
        try {
            Segment allocated = memoryManager.allocate(process);
            if (allocated != null) {
                processListView.getItems().add("Process ID: " + process.getProcessID() +
                        ", Base: " + allocated.getBase() + ", Limit: " + allocated.getLimit() * memoryManager.getUnit() + " KB");
                updateMemoryVisualization();
            }
        } catch (Exception e) {
            // Display error message for allocation exception
            showError(e.getMessage());
        }
    }

    private void updateMemoryVisualization() {
        memoryPane.getChildren().clear(); // Clear existing visualization

        // Get memory size and unit
        double memoryUnit = (double) memoryManager.getUnit() * memoryVisual.getWidth() / memoryManager.getSize();

        // Get the list of segments from memory mapping
        List<Segment> memoryMapping = memoryManager.getMapping();

        // Update memory visualization
        for (Segment segment : memoryMapping) {
            // Draw rectangle for the segment
            double width = segment.getLimit()* memoryUnit;
            double x = segment.getBase() * memoryUnit;
            Rectangle segmentRectangle = new Rectangle(width, memoryPane.getHeight());
            segmentRectangle.setX(x);
            segmentRectangle.setFill(Color.LIGHTGRAY); // Initially shaded in light gray
            memoryPane.getChildren().add(segmentRectangle);

            // Display process details inside the segment if it's allocated
            if (segment.isAllocated()) {
                // Generate random color for the segment
                Color segmentColor = generateRandomColor();
                segmentRectangle.setFill(segmentColor);

                // Display process details
                Text text = new Text(segment.getBase() * memoryUnit + "\n" +
                        "Process: " + segment.getProcess().getProcessName() + "\n" +
                        "Limit: " + segment.getLimit() * memoryManager.getUnit());
                text.setX(x + 5);
                text.setY(memoryPane.getHeight() / 2);
                memoryPane.getChildren().add(text);
            }
        }

        // Update pie chart
        updatePieChart();
    }

    @FXML
    private void convertToPhysicalAddress() {
        // Get the selected process from the combo box
        Process selectedProcess = processComboBox.getValue();

        if (selectedProcess != null) {
            try {
                // Get the logical address from the input field
                String logicalAddressText = logicalAddressInput.getText();
                long logicalAddress = Long.parseLong(logicalAddressText);

                // Convert the logical address to a physical address using the memory manager
                long physicalAddress = memoryManager.convert(selectedProcess.getProcessID(), logicalAddress);

                // Display the physical address
                physicalAddressLabel.setText("Physical Address: " + physicalAddress);
            } catch (NumberFormatException e) {
                // Handle invalid input format for the logical address
                showError("Invalid input format for logical address.");
            } catch (Exception e) {
                // Handle other exceptions
                showError(e.getMessage());
            }
        } else {
            // Handle case when no process is selected
            showError("No process selected.");
        }
    }

    @FXML
    public void deallocateProcess() {
        Process selectedProcess = processComboBox.getValue();
        if (selectedProcess != null) {
            try {
                int processID = selectedProcess.getProcessID();
                memoryManager.deallocate(processID);
                processListView.getItems().removeIf(item -> item.contains("Process ID: " + processID));
                processComboBox.getItems().remove(selectedProcess);
                processes.removeIf(process -> process.getProcessID() == processID);
                updateMemoryVisualization();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }



    private Color generateRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private void updatePieChart() {
        // Calculate total allocated memory and remaining memory
        long totalAllocatedMemory = memoryManager.getAllocatedMemory();
        long remainingMemory = memoryManager.getSize() - totalAllocatedMemory;

        // Update pie chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Allocated", totalAllocatedMemory),
                new PieChart.Data("Remaining", remainingMemory)
        );
        memoryPieChart.setData(pieChartData);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

}