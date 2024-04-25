package com.cs.os.cpuscheduler;

import javafx.util.Pair;

import java.util.*;


class CPUScheduler {

    private int osTime = 0;
    private Queue<Process> processQueue; // Queue for storing processes
    private CPU cpu; // CPU instance to execute processes
    private SchedulingAlgorithm schedulingAlgorithm; // Placeholder for the scheduling algorithm

    // Constructor
    public CPUScheduler(SchedulingAlgorithm schedulingAlgorithm) {
        this.processQueue = new LinkedList<>();
        this.cpu = new CPU();
        this.schedulingAlgorithm = schedulingAlgorithm;
    }

    // Method to add a process to the scheduler
    public void addProcess(Process process) {
        processQueue.offer(process);
        System.out.println("Process added: " + process.getProcessID());
    }

    // Method to clear the process list of the CPU Scheduler
    public void clearProcesses(){
        this.processQueue.clear();
    }

    // Method to get the first Arrival Time and set the osTime
    public void setUpScheduler() {
        List<Process> processList = new ArrayList<>(processQueue); // Convert the queue to a list for sorting
        // Sort the list of processes based on arrival time using List.sort
        processList.sort(Comparator.comparingInt(Process::getArrivalTime));
        // Clear the queue and add sorted processes back to the queue
        System.out.println(processList.size());
        processQueue.clear();
        System.out.println(processList.size());
        processQueue.addAll(processList);
        System.out.println(processList.size());

        System.out.println("Processes have been sorted by arrival time.");
    }

    // Method to run the scheduler using the current scheduling algorithm
    public void runScheduler() {
        setUpScheduler();

        while(! processQueue.isEmpty()){

            osTime = Integer.max(osTime, processQueue.peek().getArrivalTime()); // set OsTime to the firstArrival bigger than osTime

            Pair<Process, Integer> nextProcessTime= schedulingAlgorithm.schedule(processQueue);
            Process nextProcess = nextProcessTime.getKey();
            int execTime = nextProcessTime.getValue();

            nextProcess.addWaitingTime(osTime - nextProcess.getLastIdle()); // Add the waiting time to the process

//            cpu.executeProcess(nextProcess, execTime); // Execute the Process for that execTime

            osTime += execTime; // Add execTime to both the osTime and Process
            nextProcess.addExecutionTime(execTime);

            nextProcess.setLastIdle(osTime); // Set the last Idle Time for the Process

            if(nextProcess.getCompletionTime() != -1){
                processQueue.remove(nextProcess);
                System.out.println("Process" + nextProcess.getProcessID() + "Finished: " + nextProcess.getCompletionTime());
            }
        }
    }

    // Method to set the scheduling algorithm
    public void setSchedulingAlgorithm(SchedulingAlgorithm algorithm) {
        this.schedulingAlgorithm = algorithm;
    }

    // Method to get the current scheduling algorithm
    public SchedulingAlgorithm getSchedulingAlgorithm() {
        return this.schedulingAlgorithm;
    }
}


