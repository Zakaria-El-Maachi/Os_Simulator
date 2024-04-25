package com.cs.os.cpuscheduler;

interface ProcessFactory {
    // Method to create a process with defined parameters
    Process createProcess(int arrivalTime, int burstTime);

    // Method to create a process with random parameters
    Process createRandomProcess(int firstArrival, int lastArrival, int burstInterval);
}
