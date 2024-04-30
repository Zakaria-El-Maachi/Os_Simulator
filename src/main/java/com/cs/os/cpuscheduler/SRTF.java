package com.cs.os.cpuscheduler;

import javafx.util.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class SRTF implements SchedulingAlgorithm{

    private PriorityQueue<Pair<Integer, Process>> pq = new PriorityQueue<>(SRTF::compareRemainingTime);
    private List<Process> processQueue;
    private int numberOfProcesses;
    private int timeQuantum;
    private int threshold;
    private int objectiveTime;
    private int pid;

    public SRTF (int tq) {
        this.timeQuantum = tq;
        this.threshold = -1;
        this.objectiveTime = 0;
        this.pid = 0;
        this.numberOfProcesses = 0;
    }

    @Override
    public Pair<Process, Integer> schedule() {
        if (processQueue.isEmpty()) {
            return null;
        }

        if (pq.isEmpty() && pid < numberOfProcesses) {
            pq.add(new Pair<>(processQueue.get(pid).getBurstTime(), processQueue.get(pid)));
            objectiveTime = processQueue.get(pid).getArrivalTime();
            pid++;
        }

        Process process = pq.poll().getValue();

        int executionTime = min(process.getBurstTime() - process.getExecutionTime(), timeQuantum);

        objectiveTime = max(objectiveTime, process.getLastIdle()) + executionTime;
        threshold = objectiveTime;

        for (int i = pid; i < numberOfProcesses; i++) {
            if (processQueue.get(i).getArrivalTime() <= threshold) {
                pq.add(new Pair<>(processQueue.get(pid).getBurstTime(), processQueue.get(pid)));
                pid++;
            } else {
                pid = i;
                break;
            }
        }

        // If the process has not finished yet
        if (process.getExecutionTime() + executionTime < process.getBurstTime())
            pq.add(new Pair<>(process.getBurstTime() - process.getExecutionTime() - executionTime, process));

        return new Pair<>(process, executionTime);
    }

    @Override
    public void setUpAlgorithm(List<Process> processQueue) {
        processQueue.sort(Comparator.comparingInt(Process::getArrivalTime).thenComparingInt(Process::getBurstTime));

        System.out.println("Processes have been sorted by arrival time, then by priority.");

        this.processQueue = processQueue;
        this.numberOfProcesses = processQueue.size();
    }

    public static int compareRemainingTime(Pair<Integer, Process> p1, Pair<Integer, Process>p2){
        return Integer.compare(p1.getKey(), p2.getKey());
    }

}
