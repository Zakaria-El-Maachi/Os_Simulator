package com.cs.os.cpuscheduler;

import javafx.util.Pair;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class RoundRobin implements SchedulingAlgorithm {
    private List<Process> processQueue;
    private int numberOfProcesses;
    private Queue<Process> readyQueue;
    private int timeQuantum;
    private int threshold;
    private int objectiveTime;
    private int pid;

    public RoundRobin(int tq) {
        this.threshold = -1;
        this.objectiveTime = 0;
        this.timeQuantum = tq;
        this.pid = 0;
        this.readyQueue = new LinkedList<>();
    }

    @Override
    public Pair<Process, Integer> schedule() {
        if (processQueue.isEmpty()) {
            return null;
        }

        if (readyQueue.isEmpty() && pid < numberOfProcesses) {
            readyQueue.add(processQueue.get(pid));
            objectiveTime = processQueue.get(pid).getArrivalTime();
            pid++;
        }

        Process process = readyQueue.poll();

        int executionTime = min(process.getBurstTime() - process.getExecutionTime(), timeQuantum);

        objectiveTime = max(objectiveTime, process.getArrivalTime()) + executionTime;
        threshold = objectiveTime;

        for (int i = pid; i < numberOfProcesses; i++) {
            if (processQueue.get(i).getArrivalTime() <= threshold) {
                readyQueue.add(processQueue.get(i));
                pid++;
            } else {
                pid = i;
                break;
            }
        }

        // If the process has not finished yet
        if (process.getExecutionTime() + executionTime != process.getBurstTime()) {
            readyQueue.add(process);
        }

        return new Pair<>(process, executionTime);
    }

    @Override
    public void setUpAlgorithm(List<Process> processQueue) {
        // Sort the list of processes based on arrival time
        processQueue.sort(Comparator.comparingInt(Process::getArrivalTime));

        System.out.println("Processes have been sorted by arrival time.");

        this.processQueue = processQueue;
        this.numberOfProcesses = processQueue.size();
    }
}
