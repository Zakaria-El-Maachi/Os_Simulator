package com.cs.os.cpuscheduler;

import javafx.util.Pair;
import java.util.*;

import static java.lang.Integer.max;

class SJF implements SchedulingAlgorithm {

    private int threshold;
    private PriorityQueue<Process> pq = new PriorityQueue<>(SJF::compareBurst);
    private int objectiveTime;
    private List<Process> processQueue;
    private int pid;
    private int numberOfProcesses;

    public SJF() {
        this.threshold = -1;
        this.objectiveTime = 0;
        this.pid = 0;
    }

    @Override
    public Pair<Process, Integer> schedule() {
        if (processQueue.isEmpty()) {
            return null;
        }

        if (pq.isEmpty() && pid < numberOfProcesses) {
            if (processQueue.get(pid).getArrivalTime() > threshold) {
                threshold = processQueue.get(pid).getArrivalTime();
                objectiveTime = processQueue.get(0).getArrivalTime();
            }
        }

        for (int i = pid; i < numberOfProcesses; i++) {
            if (processQueue.get(i).getArrivalTime() <= threshold) {
                pq.add(processQueue.get(i));
                pid++;
            } else {
                pid = i;
                break;
            }
        }

        Process process = pq.poll();
        int executionTime = process.getBurstTime();

        threshold = objectiveTime + executionTime;
        objectiveTime += executionTime;

        return new Pair<>(process, executionTime);
    }

    @Override
    public void setUpAlgorithm(List<Process> processQueue) {
        // Sort the list of processes based on arrival time, then by burst time
        processQueue.sort(Comparator.comparingInt(Process::getArrivalTime).thenComparingInt(Process::getBurstTime));

        System.out.println("Processes have been sorted by arrival time, then by burst Time.");

        this.processQueue = processQueue;
        this.numberOfProcesses = processQueue.size();
    }

    public static int compareBurst(Process p1, Process p2) {
        return Integer.compare(p1.getBurstTime(), p2.getBurstTime());
    }

}