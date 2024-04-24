package com.cs.os.cpuscheduler;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SimpleProcessFactory implements ProcessFactory{
    private int biggestID; // To keep track of existing process IDs
    private final Random random;

    // Constructor
    public SimpleProcessFactory() {
        this.biggestID = 0;
        this.random = new Random();
    }

    // Method to create a process with defined parameters
    @Override
    public Process createProcess(int burstTime, int arrivalTime) {
        // Ensure the process ID is unique
        return new Process(++biggestID, burstTime, arrivalTime);
    }

    // Method to create a process with random parameters
    @Override
    public Process createRandomProcess(int firstArrival, int lastArrival, int burstInterval) {
        int processID = ++biggestID;

        // Generate random burst time and arrival time
        int burstTime = random.nextInt(burstInterval) + 1;
        int arrivalTime = random.nextInt(lastArrival-firstArrival) + firstArrival;

        return new Process(processID, burstTime, arrivalTime);
    }
}
