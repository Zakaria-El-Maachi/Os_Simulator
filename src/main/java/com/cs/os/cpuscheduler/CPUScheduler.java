package com.cs.os.cpuscheduler;

import javafx.util.Pair;

import java.util.*;

class CPUScheduler {

    private int osTime = 0;
    private List<Process> processQueue; // Queue for storing processes
    private List<Pair<Process, Integer>> executionTimeline;
    private CPU cpu; // CPU instance to execute processes
    private SchedulingAlgorithm schedulingAlgorithm; // Placeholder for the scheduling algorithm
    private int toBeExecuted, totalExecutionTime = 0;

    // New properties to store performance metrics
    private double averageTurnaround;
    private double throughput;
    private double averageWaitingTime;
    private int maxWaitingTime;
    private double waitingTimeVariance;
    private double cpuUtilization;
    private int contextSwitches;

    // Constructor
    public CPUScheduler(SchedulingAlgorithm schedulingAlgorithm) {
        this.processQueue = new LinkedList<>();
        this.executionTimeline = new LinkedList<>();
        this.cpu = new CPU();
        this.schedulingAlgorithm = schedulingAlgorithm;
        this.toBeExecuted = 0;
    }

    // Method to add a process to the scheduler
    public void addProcess(Process process) {
        processQueue.add(process);
        this.toBeExecuted++;
        System.out.println("Process added: " + process.getProcessID());
    }

    // Method to clear the process list of the CPU Scheduler
    public void clearProcesses() {
        this.processQueue.clear();
        this.executionTimeline.clear();
        // Reset metrics
        osTime = 0;
        totalExecutionTime = 0;
        averageTurnaround = 0;
        throughput = 0;
        averageWaitingTime = 0;
        maxWaitingTime = 0;
        waitingTimeVariance = 0;
        cpuUtilization = 0;
        contextSwitches = 0;
    }

    // Method to run the scheduler using the current scheduling algorithm
    public void runScheduler() {
        // Set up the scheduling algorithm
        this.schedulingAlgorithm.setUpAlgorithm(processQueue);

        // Variables to calculate performance metrics
        double totalTurnaroundTime = 0;
        double totalWaitingTime = 0;
        double totalServiceTime = 0;
        double totalWaitingTimeSquared = 0;
        int numProcesses = processQueue.size();

        int lastRunningProcessID = 0;

        while (toBeExecuted > 0) {
            Pair<Process, Integer> nextProcessTime = schedulingAlgorithm.schedule();
            this.executionTimeline.add(nextProcessTime);
            Process nextProcess = nextProcessTime.getKey();
            int execTime = nextProcessTime.getValue();

            if(nextProcess.getLastIdle() > osTime){
                executionTimeline.add(new Pair<>(null, nextProcess.getArrivalTime() - osTime));
                osTime = nextProcess.getArrivalTime();
            }

            int waitingTime = osTime - nextProcess.getLastIdle();
            nextProcess.addWaitingTime(waitingTime); // Add the waiting time to the process

//            cpu.executeProcess(nextProcess, execTime); // Execute the process for the specified execTime

            // Increment context switches if necessary
            if (lastRunningProcessID != nextProcess.getProcessID())
                contextSwitches++;


            osTime += execTime; // Update osTime based on execTime
            nextProcess.addExecutionTime(execTime);

            nextProcess.setLastIdle(osTime); // Update the last idle time for the process

            if (nextProcess.getExecutionTime() == nextProcess.getBurstTime()) {
                // Calculate metrics
                totalExecutionTime += nextProcess.getExecutionTime();
                totalTurnaroundTime += osTime - nextProcess.getArrivalTime();
                totalWaitingTime += nextProcess.getWaitingTime();
                totalServiceTime += nextProcess.getExecutionTime();
                totalWaitingTimeSquared += Math.pow(nextProcess.getWaitingTime(), 2);

                // Calculate max waiting time
                if (nextProcess.getWaitingTime() > maxWaitingTime) {
                    maxWaitingTime = nextProcess.getWaitingTime();
                }

                toBeExecuted--;
            }
        }

        // Calculate average turnaround time
        averageTurnaround = totalTurnaroundTime / numProcesses;

        // Calculate throughput
        throughput = numProcesses / (double) osTime;

        // Calculate average waiting time
        averageWaitingTime = totalWaitingTime / numProcesses;

        // Calculate waiting time variance
        waitingTimeVariance = (totalWaitingTimeSquared / numProcesses) - Math.pow(averageWaitingTime, 2);

        // Calculate CPU utilization
        cpuUtilization = (totalServiceTime / osTime) * 100;

    }

    // Method to set the scheduling algorithm
    public void setSchedulingAlgorithm(SchedulingAlgorithm algorithm) {
        this.schedulingAlgorithm = algorithm;
    }

    // Method to get the current scheduling algorithm
    public SchedulingAlgorithm getSchedulingAlgorithm() {
        return this.schedulingAlgorithm;
    }

    // Method to check if the process queue is empty
    public boolean processQueueIsEmpty() {
        return processQueue.isEmpty();
    }

    // Method to get the process queue
    public List<Process> getProcessQueue() {
        return processQueue;
    }

    // Getters for performance metrics

    public int getTotalExecutionTime() {
        return totalExecutionTime;
    }

    public double getAverageTurnaround() {
        return averageTurnaround;
    }

    public double getThroughput() {
        return throughput;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public int getMaxWaitingTime() {
        return maxWaitingTime;
    }

    public double getWaitingTimeVariance() {
        return waitingTimeVariance;
    }

    public double getCpuUtilization() {
        return cpuUtilization;
    }

    public int getContextSwitches() {
        return contextSwitches;
    }

    public List<Pair<Process, Integer>> getProcessExecutionTimeline() {
        return this.executionTimeline;
    }

}
