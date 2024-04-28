package com.cs.os.cpuscheduler;

import javafx.util.Pair;

import java.util.*;


class CPUScheduler {

    private int osTime = 0;
    private Queue<Process> processQueue; // Queue for storing processes
    private CPU cpu; // CPU instance to execute processes
    private SchedulingAlgorithm schedulingAlgorithm; // Placeholder for the scheduling algorithm
    private int toBeExecuted;

    // Constructor
    public CPUScheduler(SchedulingAlgorithm schedulingAlgorithm) {
        this.processQueue = new LinkedList<>();
        this.cpu = new CPU();
        this.schedulingAlgorithm = schedulingAlgorithm;
        this.toBeExecuted = 0;
    }

    // Method to add a process to the scheduler
    public void addProcess(Process process) {
        processQueue.offer(process);
        this.toBeExecuted++;
        System.out.println("Process added: " + process.getProcessID());
    }

    // Method to clear the process list of the CPU Scheduler
    public void clearProcesses(){
        this.processQueue.clear();
    }

    // Method to get the first Arrival Time and set the osTime
/*    public void setUpScheduler() {
        List<Process> processList = new ArrayList<>(processQueue); // Convert the queue to a list for sorting
        // Sort the list of processes based on arrival time using List.sort
        processList.sort(Comparator.comparingInt(Process::getArrivalTime).thenComparingInt(Process::getBurstTime));
        // Clear the queue and add sorted processes back to the queue
        // System.out.println(processList.size());
        processQueue.clear();
        // System.out.println(processList.size());
        processQueue.addAll(processList);
        // System.out.println(processList.size());

        System.out.println("Processes have been sorted by arrival time.");
    }*/

    // Method to run the scheduler using the current scheduling algorithm
    public void runScheduler() {
        /*setUpScheduler();*/
        this.schedulingAlgorithm.setUpAlgorithm(processQueue);

        while(toBeExecuted > 0){

            Pair<Process, Integer> nextProcessTime = schedulingAlgorithm.schedule();
            Process nextProcess = nextProcessTime.getKey();
            int execTime = nextProcessTime.getValue();

            osTime = Integer.max(osTime, nextProcess.getArrivalTime());

            nextProcess.addWaitingTime(osTime - nextProcess.getLastIdle()); // Add the waiting time to the process

            cpu.executeProcess(nextProcess, execTime); // Execute the Process for that execTime

            osTime += execTime; // Add execTime to both the osTime and Process
            nextProcess.addExecutionTime(execTime);

            nextProcess.setLastIdle(osTime); // Set the last Idle Time for the Process

            toBeExecuted--;
        }
        System.out.println(this.processQueue.isEmpty());
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

    // Method to check if the process queue is empty
    public Queue<Process> getProcessQueue() {
        return processQueue;
    }
}


