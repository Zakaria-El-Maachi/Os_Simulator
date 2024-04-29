package com.cs.os.cpuscheduler;

import javafx.util.Pair;

import java.util.*;

import static java.lang.Integer.max;

class PrioritySchedulingPreemptive implements SchedulingAlgorithm {

    private PriorityQueue<Process> pq = new PriorityQueue<>(PrioritySchedulingPreemptive::compare);
    private int threshold;
    private int objectiveTime;
    private List<Process> processQueue;
    private int pid;
    private int higherPriorityPid;
    private int numberOfProcesses;

    public PrioritySchedulingPreemptive() {
        this.threshold = -1;
        this.objectiveTime = 0;
        this.pid = 0;
        this.higherPriorityPid = 0;
        this.numberOfProcesses = 0;
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

        Process process = pq.peek();

        /* Iterate over the process queue to find the first Process (in terms of arrival time) who has a higher priority than the currently picked process */
        higherPriorityPid = pid;
        int maxTime = -1;
        for (int i = higherPriorityPid; i < numberOfProcesses; i++) {
            if (processQueue.get(i).getPriority() < process.getPriority()) {
                maxTime = processQueue.get(i).getArrivalTime() - objectiveTime;
                break;
            }
        }

        int executionTime;

        if (maxTime == -1 || maxTime >= (process.getBurstTime() - process.getExecutionTime())) {
            executionTime = process.getBurstTime() - process.getExecutionTime();
            pq.poll();
        } else {
            executionTime =  maxTime;
        }

        objectiveTime = max(objectiveTime, process.getArrivalTime());
        threshold = objectiveTime + executionTime;
        objectiveTime += executionTime;

        return new Pair<>(process, executionTime);
    }

    @Override
    public void setUpAlgorithm(List<Process> processQueue) {
        processQueue.sort(Comparator.comparingInt(Process::getArrivalTime).thenComparingInt(Process::getPriority));

        System.out.println("Processes have been sorted by arrival time, then by priority.");

        this.numberOfProcesses = processQueue.size();
        this.processQueue = processQueue;
    }

    public static int compare(Process p1, Process p2) {
        if (p1.getPriority() != p2.getPriority())
            return p1.getPriority() - p2.getPriority();
        return p1.getArrivalTime() - p2.getArrivalTime();
    }

}