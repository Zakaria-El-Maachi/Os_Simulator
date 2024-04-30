package com.cs.os.cpuscheduler;

public class Process {
    private final int processID;
    private final int burstTime;
    private final int arrivalTime;
    private int lastIdle;
    private int waitingTime = 0;
    private int executionTime = 0;
    private int priority = 0;

    private int tickets = 0;


    // Constructor
    public Process(int processID, int arrivalTime, int burstTime, int priority, int tickets) {
        this.processID = processID;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.lastIdle = arrivalTime;
        this.priority = priority;
        this.tickets = tickets;
    }
    public Process(int processID, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.lastIdle = arrivalTime;
    }

    // Getters for the attributes
    public int getProcessID() {
        return processID;
    }

    public String getProcessName() {
        return intToAlphabet(getProcessID());
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getCompletionTime() {
        if(executionTime == burstTime)
            return arrivalTime + burstTime + waitingTime;
        return -1;
    }

    public int getLastIdle(){
        return lastIdle;
    }

    public int getTurnaround(){
        return burstTime + waitingTime;
    }
    public int getPriority() {
        return this.priority;
    }

    public int getWaitingTime() {
        return waitingTime;
    }
    public void addWaitingTime(int time) {
        waitingTime += time;
    }
    public int getExecutionTime() {
        return executionTime;
    }
    public void addExecutionTime(int time) {
        executionTime += time;
    }

    public void setLastIdle(int lastIdle) {
        this.lastIdle = lastIdle;
    }
    public double getResponseRatio() {
        return (double) waitingTime / (waitingTime + burstTime);
    }
    public int getTickets() {
        return tickets;
    }


    // Converts an integer to its corresponding representation in the alphabet.
    public static String intToAlphabet(int num) {
        if (num < 1) {
            throw new IllegalArgumentException("The input number must be greater than or equal to 1.");
        }

        StringBuilder result = new StringBuilder();

        // Convert the number to its alphabetic representation
        while (num > 0) {
            num--; // Adjust for 0-indexing
            int remainder = num % 26;
            char currentLetter = (char) ('A' + remainder);
            result.insert(0, currentLetter);

            // Move to the next digit place (divide the number by 26)
            num = num / 26;
        }

        return result.toString();
    }


    // toString() method to provide a textual representation of the process
    @Override
    public String toString() {
        return "Process ID: " + intToAlphabet(processID) + "\n" +
                "Arrival Time: " + arrivalTime + "\n" +
                "Burst Time: " + burstTime + "\n" +
                "Finish Time: " + lastIdle + "\n" +
                "Turnaround Time: " + getTurnaround() + "\n" +
                "Waiting Time: " + waitingTime + "\n" +
                "Execution Time: " + executionTime + "\n";
    }


}
