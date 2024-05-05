package com.os.cpuscheduler;

import java.util.Random;

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
    public Process createProcess(int arrivalTime, int burstTime, int priority, int tickets) {
        // Ensure the process ID is unique
        return new Process(++biggestID, arrivalTime, burstTime, priority, tickets);
    }

    private int totalTickets = 0; // Variable to track the total number of tickets

    @Override
    public Process createRandomProcess(int lastArrival, int burstInterval, int priorityInterval, int maxTickets) {
        int processID = ++biggestID;

        // Generate random burst time and arrival time
        int arrivalTime = random.nextInt(lastArrival + 1);
        int burstTime = random.nextInt(burstInterval + 1) + 1;
        int priority = random.nextInt(priorityInterval) + 1;

        // Calculate the remaining tickets to distribute
        int remainingTickets = maxTickets - totalTickets;
        if (remainingTickets <= 0) {
            // If there are no remaining tickets, set the tickets to a minimum of 1
            remainingTickets = 1;
        }

        // Generate a random number of tickets based on the remaining tickets
        int tickets = random.nextInt(remainingTickets) + 1;

        // Update the total tickets generated
        totalTickets += tickets;

        return new Process(processID, arrivalTime, burstTime, priority, tickets);
    }


    @Override
    public void reset() {
        this.biggestID = 0;
    }

}

