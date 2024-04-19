package com.cs.os.cpuscheduler;

interface ProcessFactory {
    // Method to create a process with defined parameters
    Process createProcess(int burstTime, int arrivalTime);

    // Method to create a process with random parameters
    Process createRandomProcess(int firstArrival, int lastArrival, int burstInterval);
}
