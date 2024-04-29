package com.cs.os.cpuscheduler;

import javafx.util.Pair;
import java.util.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class RRPriorityScheduling implements SchedulingAlgorithm {

    private PriorityQueue<Pair<Process, Integer>> pq = new PriorityQueue<>(RRPriorityScheduling::compare);
    private List<Process> processQueue;
    private int numberOfProcesses;
    private int timeQuantum;
    private int threshold;
    private int objectiveTime;
    private int pid;
    private int higherPriorityPid;
    private int lastTimestamp;

    public RRPriorityScheduling (int tq) {
        this.timeQuantum = tq;
        this.threshold = -1;
        this.objectiveTime = 0;
        this.pid = 0;
        this.higherPriorityPid = 0;
        this.numberOfProcesses = 0;
        this.lastTimestamp = 0;
    }

    @Override
    public Pair<Process, Integer> schedule() {
        if (processQueue.isEmpty()) {
            return null;
        }

        if (pq.isEmpty() && pid < numberOfProcesses) {
            pq.add(new Pair<>(processQueue.get(pid), lastTimestamp));
            lastTimestamp++;
            objectiveTime = processQueue.get(pid).getArrivalTime();
            pid++;
        }

        Process process = pq.poll().getKey();

        /* Iterate over the process queue to find the first Process (in terms of arrival time) who has a higher priority than the currently picked process */
        higherPriorityPid = pid;
        int maxTime = Integer.MAX_VALUE;
        for (int i = higherPriorityPid; i < numberOfProcesses; i++) {
            if (processQueue.get(i).getPriority() < process.getPriority()) {
                maxTime = processQueue.get(i).getArrivalTime() - objectiveTime;
                break;
            }
        }

        int executionTime = min(process.getBurstTime() - process.getExecutionTime(), min(timeQuantum, maxTime));

        objectiveTime = max(objectiveTime, process.getArrivalTime()) + executionTime;
        threshold = objectiveTime;

        for (int i = pid; i < numberOfProcesses; i++) {
            if (processQueue.get(i).getArrivalTime() <= threshold) {
                pq.add(new Pair<>(processQueue.get(i), lastTimestamp));
                lastTimestamp++;
                pid++;
            } else {
                pid = i;
                break;
            }
        }

        // If the process has not finished yet
        if (process.getExecutionTime() + executionTime != process.getBurstTime()) {
            pq.add(new Pair<>(process, lastTimestamp));
            lastTimestamp++;
        }

        return new Pair<>(process, executionTime);
    }

    @Override
    public void setUpAlgorithm(List<Process> processQueue) {
        processQueue.sort(Comparator.comparingInt(Process::getArrivalTime).thenComparingInt(Process::getPriority));

        System.out.println("Processes have been sorted by arrival time, then by priority.");

        this.processQueue = processQueue;
        this.numberOfProcesses = processQueue.size();
    }

    public static int compare(Pair<Process, Integer> p1, Pair<Process, Integer> p2) {
        Process p1p = p1.getKey();
        Integer p1ts = p1.getValue();
        Process p2p = p2.getKey();
        Integer p2ts = p2.getValue();
        if (p1p.getPriority() != p2p.getPriority())
            return p1p.getPriority() - p2p.getPriority();
        return p1ts - p2ts;
    }

}
