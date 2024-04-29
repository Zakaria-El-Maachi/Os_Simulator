package com.cs.os.cpuscheduler;

interface ProcessFactory {
    // Method to create a process with defined parameters
    Process createProcess(int arrivalTime, int burstTime, int priority);

    // Method to create a process with random parameters
    Process createRandomProcess(int lastArrival, int burstInterval, int priorityInterval);

    void reset();
}
