package com.cs.os.cpuscheduler;

public class Process {
    private final int processID;
    private final float burstTime;
    private final float arrivalTime;
    private float lastIdle;
    private float waitingTime = 0;
    private float executionTime = 0;

    // Constructor
    public Process(int processID, float burstTime, float arrivalTime) {
        this.processID = processID;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.lastIdle = arrivalTime;
    }

    // Getters for the attributes
    public int getProcessID() {
        return processID;
    }

    public float getBurstTime() {
        return burstTime;
    }

    public float getArrivalTime() {
        return arrivalTime;
    }

    public float getCompletionTime() {
        if(executionTime == burstTime)
            return arrivalTime + burstTime + waitingTime;
        return -1;
    }

    public float getLastIdle(){
        return lastIdle;
    }

    public float getTurnaround(){
        return burstTime + waitingTime;
    }

    public float getWaitingTime() {
        return waitingTime;
    }
    public void addWaitingTime(float time) {
        waitingTime += time;
    }
    public float getExecutionTime() {
        return executionTime;
    }
    public void addExecutionTime(float time) {
        executionTime += time;
    }

    public void setLastIdle(float lastIdle) {
        this.lastIdle = lastIdle;
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
                "Burst Time: " + burstTime + "\n" +
                "Arrival Time: " + arrivalTime + "\n" +
                "Waiting Time: " + waitingTime + "\n" +
                "Execution Time: " + executionTime + "\n" +
                "Turnaround Time: " + getTurnaround();
    }


}
