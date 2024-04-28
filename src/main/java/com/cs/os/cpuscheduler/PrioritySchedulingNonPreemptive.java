package com.cs.os.cpuscheduler;

import javafx.util.Pair;

import java.util.*;

import static java.lang.Integer.max;

class PrioritySchedulingNonPreemptive implements SchedulingAlgorithm {

    private PriorityQueue<Process> pq = new PriorityQueue<>(PrioritySchedulingNonPreemptive::comparePriority);
    private int threshold;
    private int objectiveTime;
    private List<Process> processQueue;
    private int pid;
    private int numberOfProcesses;

    public PrioritySchedulingNonPreemptive() {
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
        processQueue.sort(Comparator.comparingInt(Process::getArrivalTime).thenComparingInt(Process::getPriority));
        System.out.println("Processes have been sorted by arrival time, then by priority.");

        this.processQueue = processQueue;
        this.numberOfProcesses = processQueue.size();
    }

    public static int comparePriority(Process p1, Process p2) {
        return Integer.compare(p1.getPriority(), p2.getPriority());
    }

}