package com.cs.os.cpuscheduler;

import javafx.util.Pair;
import java.util.Queue;

class FCFS implements SchedulingAlgorithm {
    @Override
    public Pair<Process, Integer> schedule(Queue<Process> processQueue) {
        if (!processQueue.isEmpty()) {

            Process currentProcess = processQueue.peek();

            // Calculate waiting time
            int executionTime = currentProcess.getBurstTime();

            return new Pair<>(currentProcess, executionTime);
        }
        return null;
    }



}
