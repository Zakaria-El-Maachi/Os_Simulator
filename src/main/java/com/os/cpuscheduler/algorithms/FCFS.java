package com.os.cpuscheduler.algorithms;

import com.os.Process;
import javafx.util.Pair;
import java.util.*;

public class FCFS implements SchedulingAlgorithm {

    private int pid = 0;
    private int numberOfProcesses;
    private List<Process> processQueue;

    @Override
    public Pair<Process, Integer> schedule() {
        if (!processQueue.isEmpty() && pid < numberOfProcesses) {
            Process currentProcess = processQueue.get(pid++);
            int executionTime = currentProcess.getBurstTime();
            return new Pair<>(currentProcess, executionTime);
        }
        return null;
    }

    @Override
    public void setUpAlgorithm(List<Process> processQueue) {
        processQueue.sort(Comparator.comparingInt(Process::getArrivalTime));
        System.out.println("Processes have been sorted by arrival time.");

        this.processQueue = processQueue;
        this.numberOfProcesses = processQueue.size();
    }

}
