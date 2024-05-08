package com.os.cpuscheduler.algorithms;

import com.os.Process;
import javafx.util.Pair;

public class SJF extends AlgorithmTemplate {

    public SJF() { super(); }

    @Override
    public void checkIfReadyQueueIsEmpty() { checkIfReadyQueueIsEmptyOption1(); }

    @Override
    public Pair<Process, Integer> updateTimes() { return new Pair<>(null, null); }

    @Override
    public void addToReadyQueue(int t) { addToReadyQueueOption1(t); }

    @Override
    public Pair<Process, Integer> finalStep(Process process, Integer executionTime) { return finalStepOption1(process, executionTime); }

    @Override
    protected int getSecondaryCriteria(Process process) {
        return process.getBurstTime();
    }

    @Override
    protected int compareProcesses(Process p1, Process p2) {
        return Integer.compare(p1.getBurstTime(), p2.getBurstTime());
    }

    @Override
    protected int compareProcessesWithTimestamp(Pair<Process, Integer> p1, Pair<Process, Integer> p2) {
        return 0;
    }
}