package com.os.cpuscheduler.algorithms;

import com.os.cpuscheduler.Process;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static java.lang.Math.max;

public class HRRN extends AlgorithmTemplate {

    private List<Process> pq = new ArrayList<>();

    public HRRN() { super(); }

    @Override
    public void checkIfReadyQueueIsEmpty() {
        if (pq.isEmpty() && pid < numberOfProcesses) {
            pq.add(processQueue.get(pid));
            objectiveTime = processQueue.get(pid).getArrivalTime();
            pid++;
        }
    }

    @Override
    public Pair<Process, Integer> updateTimes() {
        pq.sort(Comparator.comparingDouble(this::getResponseRatio));
        Process process = pq.getLast();
        pq.removeLast();
        int executionTime = process.getBurstTime();
        objectiveTime = max(objectiveTime, process.getArrivalTime()) + executionTime;
        threshold = objectiveTime;

        return new Pair<>(process, executionTime);
    }

    @Override
    public void addToReadyQueue(int t) { pq.add(processQueue.get(t)); }

    @Override
    public Pair<Process, Integer> finalStep(Process process, Integer executionTime) { return new Pair<>(process, executionTime); }

    @Override
    protected int getSecondaryCriteria(Process process) {
        return 0;
    }

    @Override
    protected int compareProcesses(Process p1, Process p2) { return 0; }

    @Override
    protected int compareProcessesWithTimestamp(Pair<Process, Integer> p1, Pair<Process, Integer> p2) {
        return 0;
    }

    private double getResponseRatio(Process p) {
        return (double) (objectiveTime - p.getArrivalTime() + p.getBurstTime()) / p.getBurstTime();
    }

}
