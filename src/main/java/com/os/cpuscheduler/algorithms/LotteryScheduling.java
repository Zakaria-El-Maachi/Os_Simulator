package com.os.cpuscheduler.algorithms;

import com.os.Process;
import javafx.util.Pair;
import java.util.*;

public class LotteryScheduling implements SchedulingAlgorithm {
    private List<Process> ticketQueue;
    private List<Process> processQueue;
    private int totalTickets;
    private int objectiveTime;
    private int pid;
    private int timeQuantum;

    public LotteryScheduling(int tq) {
        this.timeQuantum = tq;
        this.objectiveTime = 0;
        this.pid = 0;
        this.ticketQueue = new ArrayList<>();
    }

    @Override
    public Pair<Process, Integer> schedule() {
        if (processQueue.isEmpty()) {
            return null;
        }

        if (ticketQueue.isEmpty() && pid < processQueue.size()) {
            ticketQueue.add(processQueue.get(pid));
            objectiveTime = processQueue.get(pid).getArrivalTime();
            pid++;
        }

        for (int i = pid; i < processQueue.size(); i++) {
            if (processQueue.get(i).getArrivalTime() <= objectiveTime) {
                ticketQueue.add(processQueue.get(i));
                pid++;
            } else {
                pid = i;
                break;
            }
        }

        // Calculate the total number of tickets
        calculateTotalTickets();

        // Select a random ticket
        int selectedTicket = new Random().nextInt(totalTickets);

        // Find the process that holds the selected ticket
        int ticketSum = 0;
        Process selectedProcess = null;
        for (Process process : ticketQueue) {
            ticketSum += process.getTickets();
            if (selectedTicket < ticketSum) {
                selectedProcess = process;
                break;
            }
        }

        // Calculate the execution time for the selected process
        int executionTime = Math.min(selectedProcess.getBurstTime() - selectedProcess.getExecutionTime(), timeQuantum);

        // Update the current time
        objectiveTime += executionTime;


        for (int i = pid; i < processQueue.size(); i++) {
            if (processQueue.get(i).getArrivalTime() <= objectiveTime) {
                ticketQueue.add(processQueue.get(i));
                pid++;
            } else {
                pid = i;
                break;
            }
        }

        // Remove the process from the ticket queue if it has completed execution
        if (selectedProcess.getExecutionTime() + executionTime >= selectedProcess.getBurstTime()) {
            ticketQueue.remove(selectedProcess);
        }

        return new Pair<>(selectedProcess, executionTime);
    }

    @Override
    public void setUpAlgorithm(List<Process> processQueue) {
        // Sort the process queue based on arrival time
        processQueue.sort(Comparator.comparingInt(Process::getArrivalTime));
        this.processQueue = processQueue;
    }

    private void calculateTotalTickets() {
        totalTickets = 0;
        for (Process process : ticketQueue) {
            totalTickets += process.getTickets();
        }
    }
}