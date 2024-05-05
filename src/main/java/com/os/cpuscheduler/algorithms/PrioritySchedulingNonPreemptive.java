package com.os.cpuscheduler.algorithms;

import com.os.cpuscheduler.Process;
import javafx.util.Pair;

public class PrioritySchedulingNonPreemptive extends AlgorithmTemplate {

    public PrioritySchedulingNonPreemptive() { super(); }

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
        return process.getPriority();
    }

    @Override
    protected int compareProcesses(Process p1, Process p2) {
        return Integer.compare(p1.getPriority(), p2.getPriority());
    }

    @Override
    protected int compareProcessesWithTimestamp(Pair<Process, Integer> p1, Pair<Process, Integer> p2) {
        return 0;
    }

}