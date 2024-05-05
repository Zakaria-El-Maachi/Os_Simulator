package com.os.cpuscheduler.algorithms;

import com.os.cpuscheduler.Process;
import javafx.util.Pair;
import java.util.*;
import static java.lang.Math.min;

// This class serves as a template for implementing various CPU scheduling algorithms
abstract class AlgorithmTemplate implements SchedulingAlgorithm {
    protected PriorityQueue<Process> pq; // Priority queue for processes
    protected int threshold; // Time threshold for scheduling
    protected int objectiveTime; // Objective time for scheduling
    protected List<Process> processQueue; // Queue of processes
    protected int pid; // Process ID
    protected int numberOfProcesses;  // Total number of processes
    protected int higherPriorityPid; // PID of the process with higher priority
    protected Queue<Process> readyQueue; // Ready queue for processes
    protected int timeQuantum; // Time quantum for round-robin scheduling
    protected int lastTimestamp; // Last timestamp recorded
    protected PriorityQueue<Pair<Process, Integer>> pqWithTimestamp; // Priority queue for processes with timestamps
    protected List<Process> ticketQueue;
    protected int totalTickets;

    // Default constructor
    public AlgorithmTemplate() {
        this.threshold = -1;
        this.objectiveTime = 0;
        this.pid = 0;
        this.pq = new PriorityQueue<>(this::compareProcesses);
        this.higherPriorityPid = 0;
    }

    // Constructor with time quantum parameter
    public AlgorithmTemplate(int tq) {
        this.threshold = -1;
        this.objectiveTime = 0;
        this.pid = 0;
        this.pq = new PriorityQueue<>(this::compareProcesses);
        this.pqWithTimestamp = new PriorityQueue<>(this::compareProcessesWithTimestamp);;
        this.higherPriorityPid = 0;
        this.readyQueue = new LinkedList<>();
        this.timeQuantum = tq;
        this.lastTimestamp = 0;
        this.ticketQueue = new ArrayList<>();
    }

    // Method to schedule processes based on specific algorithm logic
    @Override
    public Pair<Process, Integer> schedule() {
        // Ensure the process queue is not empty
        if (processQueue.isEmpty()) {
            return null;
        }

        // Check if the ready queue is empty and update process times
        checkIfReadyQueueIsEmpty();
        Pair<Process, Integer> pair = updateTimes();
        Process process = pair.getKey();
        Integer executionTime = pair.getValue();

        // Iterate over the process queue to add processes to the ready queue if their arrival time is less than or equal to the threshold
        for (int i = pid; i < numberOfProcesses; i++) {
            if (processQueue.get(i).getArrivalTime() <= threshold) {
                addToReadyQueue(i);
                pid++;
            } else {
                pid = i;
                break;
            }
        }

        return finalStep(process, executionTime);
    }

    // Method to set up the algorithm with a given process queue
    @Override
    public void setUpAlgorithm(List<Process> processQueue) {
        processQueue.sort(Comparator.comparingInt(Process::getArrivalTime).thenComparingInt(this::getSecondaryCriteria));
        System.out.println("Processes have been sorted by arrival time, then by secondary criteria.");

        this.processQueue = processQueue;
        this.numberOfProcesses = processQueue.size();
    }

    // Abstract methods to be implemented by subclasses for specific algorithm behavior
    protected abstract void checkIfReadyQueueIsEmpty(); // Check if ready queue is empty
    protected abstract Pair<Process, Integer> updateTimes(); // Update process times
    protected abstract void addToReadyQueue(int t); // Add process to ready queue
    protected abstract Pair<Process, Integer> finalStep(Process process, Integer executionTime); // Final step of scheduling
    protected abstract int getSecondaryCriteria(Process process); // Get secondary criteria for process comparison
    protected abstract int compareProcesses(Process p1, Process p2); // Compare two processes
    protected abstract int compareProcessesWithTimestamp(Pair<Process, Integer> p1, Pair<Process, Integer> p2); // Compare processes with timestamps

    // Option 1 for checking if ready queue is empty
    protected void checkIfReadyQueueIsEmptyOption1() {
        // Check if the priority queue is empty and there are pending processes in the process queue
        if (pq.isEmpty() && pid < numberOfProcesses) {
            // Check if the arrival time of the next process in the process queue is greater than the threshold
            if (processQueue.get(pid).getArrivalTime() > threshold) {
                // Update the threshold to the arrival time of the next process
                threshold = processQueue.get(pid).getArrivalTime();
                // Update the objective time to the arrival time of the first process in the process queue
                objectiveTime = processQueue.get(0).getArrivalTime();
            }
        }
    }

    // Option 2 for checking if ready queue is empty
    protected void checkIfReadyQueueIsEmptyOption2() {
        // Check if the ready queue is empty and there are pending processes in the process queue
        if (readyQueue.isEmpty() && pid < numberOfProcesses) {
            // Add the next process from the process queue to the ready queue
            readyQueue.add(processQueue.get(pid));
            // Update the objective time to the arrival time of the added process
            objectiveTime = processQueue.get(pid).getArrivalTime();
            // Move to the next process in the process queue
            pid++;
        }
    }

    // Option 2 for updating process times
    protected Pair<Process, Integer> updateTimesOption2() {
        // Retrieve the next process from the ready queue
        Process process = readyQueue.poll();
        // Calculate the execution time for the process, considering the time quantum and remaining burst time
        int executionTime = min(process.getBurstTime() - process.getExecutionTime(), timeQuantum);
        // Update the objective time based on the maximum of the current objective time and the arrival time of the process,
        // then add the execution time to get the new objective time
        objectiveTime = Math.max(objectiveTime, process.getArrivalTime()) + executionTime;
        // Update the threshold with the new objective time
        threshold = objectiveTime;

        return new Pair<>(process, executionTime);
    }

    // Option 1 for adding process to ready queue
    protected void addToReadyQueueOption1(int t) {
        pq.add(processQueue.get(t));
    }

    // Option 2 for adding process to ready queue
    protected void addToReadyQueueOption2(int t) {
        readyQueue.add(processQueue.get(t));
    }

    // Option 1 for final step of scheduling
    protected Pair<Process, Integer> finalStepOption1(Process p, Integer et) {
        // Retrieve the next process from the priority queue
        Process process = pq.poll();
        // Determine the execution time for the process
        int executionTime = process.getBurstTime();
        // Update the threshold and objective time based on the execution time of the process
        threshold = objectiveTime + executionTime;
        objectiveTime += executionTime;

        return new Pair<>(process, executionTime);
    }

    // Option 2 for final step of scheduling
    protected Pair<Process, Integer> finalStepOption2(Process process, Integer executionTime) {
        // If the process has not finished yet
        if (process.getExecutionTime() + executionTime != process.getBurstTime()) {
            readyQueue.add(process);
        }
        return new Pair<>(process, executionTime);
    }
}