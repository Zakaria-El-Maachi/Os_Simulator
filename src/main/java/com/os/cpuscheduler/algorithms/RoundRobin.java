package com.os.cpuscheduler.algorithms;

import com.os.Process;
import javafx.util.Pair;

public class RoundRobin extends AlgorithmTemplate {

    public RoundRobin(int tq) { super(tq); }

    @Override
    public void checkIfReadyQueueIsEmpty() { checkIfReadyQueueIsEmptyOption2(); }

    @Override
    public Pair<Process, Integer> updateTimes() { return updateTimesOption2(); }

    @Override
    public void addToReadyQueue(int t) { addToReadyQueueOption2(t); }

    @Override
    public Pair<Process, Integer> finalStep(Process process, Integer executionTime) { return finalStepOption2(process, executionTime); }

    @Override
    protected int getSecondaryCriteria(Process process) { return 0; }

    @Override
    protected int compareProcesses(Process p1, Process p2) { return 0; }

    @Override
    protected int compareProcessesWithTimestamp(Pair<Process, Integer> p1, Pair<Process, Integer> p2) {
        return 0;
    }
}
