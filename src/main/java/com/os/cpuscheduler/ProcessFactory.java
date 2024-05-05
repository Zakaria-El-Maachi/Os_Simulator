package com.os.cpuscheduler;

public interface ProcessFactory {
    // Method to create a process with defined parameters
    Process createProcess(int arrivalTime, int burstTime, int priority, int tickets);

    // Method to create a process with random parameters
    Process createRandomProcess(int lastArrival, int burstInterval, int priorityInterval, int maxTickets);

    void reset();
}
