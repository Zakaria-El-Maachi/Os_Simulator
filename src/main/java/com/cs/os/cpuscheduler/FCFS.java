package com.cs.os.cpuscheduler;

import javafx.util.Pair;

import java.util.Iterator;
import java.util.Queue;

class FCFS implements SchedulingAlgorithm {

    private Iterator<Process> pIterator;

    @Override
    public Pair<Process, Integer> schedule() {
        if (pIterator.hasNext()) {
            Process currentProcess = pIterator.next();
            int executionTime = currentProcess.getBurstTime();
            return new Pair<>(currentProcess, executionTime);
        }
        return null;
    }

    @Override
    public void setUpAlgorithm(Queue<Process> processQueue) {
        if (!processQueue.isEmpty()) {
            this.pIterator = processQueue.iterator();
        }
    }

}
