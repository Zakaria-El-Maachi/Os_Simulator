package com.cs.os.cpuscheduler;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static java.lang.Math.max;

public class HRRN implements SchedulingAlgorithm {
    private List<Process> pq = new ArrayList<>();
    private List<Process> processQueue;
    private int numberOfProcesses;
    private int threshold;
    private int objectiveTime;
    private int pid;

    public HRRN () {
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
            pq.add(processQueue.get(pid));
            objectiveTime = processQueue.get(pid).getArrivalTime();
            pid++;
        }

        pq.sort(Comparator.comparingDouble(Process::getResponseRatio));
        Process process = pq.getLast();

        int executionTime = process.getBurstTime();

        objectiveTime = max(objectiveTime, process.getArrivalTime()) + executionTime;
        threshold = objectiveTime;

        for (int i = pid; i < numberOfProcesses; i++) {
            if (processQueue.get(i).getArrivalTime() <= threshold) {
                pq.add(processQueue.get(i));
                pid++;
            } else {
                pid = i;
                break;
            }
        }

        return new Pair<>(process, executionTime);
    }

    @Override
    public void setUpAlgorithm(List<Process> processQueue) {
        processQueue.sort(Comparator.comparingInt(Process::getArrivalTime));

        System.out.println("Processes have been sorted by arrival time, then by priority.");

        this.processQueue = processQueue;
        this.numberOfProcesses = processQueue.size();
    }
}
