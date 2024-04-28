package com.cs.os.cpuscheduler;

import javafx.util.Pair;

import java.util.*;

import static java.lang.Integer.max;

class PrioritySchedulingPreemptive implements SchedulingAlgorithm {

    private PriorityQueue<Process> pq = new PriorityQueue<>(PrioritySchedulingNonPreemptive::comparePriority);
    private int threshold;
    private int objectiveTime;
    private List<Process> processQueue;
    private Iterator<Process> pIterator;
    private int pidx;
    private Process savedProcess;
    private int numberOfProcesses;

    public PrioritySchedulingPreemptive() {
        this.threshold = -1;
        this.objectiveTime = 0;
        this.savedProcess = null;
        this.pidx = 0;
        this.numberOfProcesses = 0;
    }

    @Override
    public Pair<Process, Integer> schedule() {
        if (processQueue.isEmpty()) {
            return null;
        }

        if (this.threshold == -1) {
            threshold = processQueue.get(0).getArrivalTime();
        }

        for (int i = pidx; i < numberOfProcesses; i++) {
            if (processQueue.get(i).getArrivalTime() <= threshold) {
                pq.add(processQueue.get(i));
            } else {
                pidx = i;
                break;
            }
        }

        Process process = pq.peek();

        /* Iterate over the process queue to find the first Process (in terms of arrival time) who has a higher priority than the currently picked process */
        int maxTime = -1;
        for (int i = pidx; i < numberOfProcesses; i++) {
            if (processQueue.get(i).getPriority() < process.getPriority()) {
                maxTime = processQueue.get(i).getArrivalTime() - objectiveTime;
            }
        }

        if (maxTime == -1 || maxTime >= process.getBurstTime()) {
            int executionTime = process.getBurstTime() - process.getExecutionTime();
            pq.poll();
        }

        int executionTime = maxTime - process.getExecutionTime();

        objectiveTime = max(objectiveTime, process.getArrivalTime());
        threshold = objectiveTime + executionTime;
        objectiveTime += executionTime;

        return new Pair<>(process, executionTime);
    }

    @Override
    public void setUpAlgorithm(Queue<Process> processQueue) {
        List<Process> processList = new ArrayList<>(processQueue);
        processList.sort(Comparator.comparingInt(Process::getArrivalTime).thenComparingInt(Process::getPriority));

        System.out.println("Processes have been sorted by arrival time.");

        this.numberOfProcesses = processList.size();
        this.processQueue = processList;
    }

    public static int comparePriority(Process p1, Process p2) {
        return Integer.compare(p1.getPriority(), p2.getPriority());
    }

}