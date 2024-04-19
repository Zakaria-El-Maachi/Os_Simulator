package com.cs.os.cpuscheduler;

public class Process {
    private final int processID;
    private final int burstTime;
    private final int arrivalTime;
    private int completionTime;

    // Constructor
    public Process(int processID, int burstTime, int arrivalTime) {
        this.processID = processID;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.completionTime = arrivalTime;
    }

    // Getters for the attributes
    public int getProcessID() {
        return processID;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public int getTurnaround(){
        return completionTime - arrivalTime;
    }

    public int getWaitingTime() {
        return getTurnaround() - burstTime;
    }

    // Setter
    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

}
