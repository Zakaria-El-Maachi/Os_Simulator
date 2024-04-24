package com.cs.os.cpuscheduler;

import javafx.util.Pair;
import java.util.Queue;

class FCFSAlgorithm implements SchedulingAlgorithm {
    @Override
    public Pair<Process, Integer> schedule(Queue<Process> processQueue, CPU cpu) {
        if (!processQueue.isEmpty()) {

            Process currentProcess = processQueue.poll();

            // Calculate waiting time
            int executionTime = Math.max(0, currentProcess.getBurstTime() - currentProcess.getExecutionTime());

            // Execute the process on the CPU
            cpu.executeProcess(currentProcess, currentProcess.getBurstTime());

            return new Pair<>(currentProcess, executionTime);
        }
        return null;
    }



}
