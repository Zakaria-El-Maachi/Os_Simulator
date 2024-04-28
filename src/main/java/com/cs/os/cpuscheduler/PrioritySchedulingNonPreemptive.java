package com.cs.os.cpuscheduler;

import javafx.util.Pair;

import java.util.*;

import static java.lang.Integer.max;

class PrioritySchedulingNonPreemptive implements SchedulingAlgorithm {

    private PriorityQueue<Process> pq = new PriorityQueue<>(PrioritySchedulingNonPreemptive::comparePriority);
    private int threshold;
    private int objectiveTime;
    private Queue<Process> processQueue;
    private Iterator<Process> pIterator;
    private Process savedProcess;

    public PrioritySchedulingNonPreemptive() {
        this.threshold = -1;
        this.objectiveTime = 0;
        this.savedProcess = null;
    }

    @Override
    public Pair<Process, Integer> schedule() {
        if (processQueue.isEmpty()) {
            return null;
        }

        boolean skipFlag = false;

        if (this.threshold == -1) {
            threshold = processQueue.peek().getArrivalTime();
            pIterator = processQueue.iterator();
        }

        if (savedProcess != null) {
            if (savedProcess.getArrivalTime() <= threshold) {
                pq.add(savedProcess);
                savedProcess = null;
            } else {
                if (pq.isEmpty()) {
                    threshold = savedProcess.getArrivalTime();
                    pq.add(savedProcess);
                    savedProcess = null;
                } else {
                    skipFlag = true;
                }
            }
        }

        if (!skipFlag) {
            while (pIterator.hasNext()) {
                Process currentProcess = pIterator.next();
                if (currentProcess.getArrivalTime() <= threshold) {
                    pq.add(currentProcess);
                } else {
                    savedProcess = currentProcess;
                    break;
                }
            }
        }

        Process process = pq.poll();
        int executionTime = process.getBurstTime();

        objectiveTime = max(objectiveTime, process.getArrivalTime());
        threshold = objectiveTime + executionTime;

        objectiveTime += executionTime;

        /*        System.out.println("NEW THRESHOLD: " + threshold);*/

        return new Pair<>(process, executionTime);
    }

    @Override
    public void setUpAlgorithm(Queue<Process> processQueue) {
        List<Process> processList = new ArrayList<>(processQueue);
        processList.sort(Comparator.comparingInt(Process::getArrivalTime).thenComparingInt(Process::getPriority));
        processQueue.clear();
        processQueue.addAll(processList);

        System.out.println("Processes have been sorted by arrival time.");

        this.processQueue = processQueue;
    }

    public static int comparePriority(Process p1, Process p2) {
        return Integer.compare(p1.getPriority(), p2.getPriority());
    }

}