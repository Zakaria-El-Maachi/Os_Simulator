package com.os.cpuscheduler.algorithms;

import com.os.Process;
import javafx.util.Pair;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class RRPriorityScheduling extends AlgorithmTemplate {

    public RRPriorityScheduling(int tq) { super(tq); }

    @Override
    public void checkIfReadyQueueIsEmpty() {
        // Check if the priority queue with timestamp is empty and there are pending processes in the process queue
        if (pqWithTimestamp.isEmpty() && pid < numberOfProcesses) {
            // Add the next process from the process queue to the priority queue with its timestamp
            pqWithTimestamp.add(new Pair<>(processQueue.get(pid), lastTimestamp));
            // Increment the last timestamp for the next process
            lastTimestamp++;
            // Update the objective time to the arrival time of the added process
            objectiveTime = processQueue.get(pid).getArrivalTime();
            // Move to the next process in the process queue
            pid++;
        }
    }

    @Override
    public Pair<Process, Integer> updateTimes() {
        // Retrieve the next process with timestamp from the priority queue
        Process process = pqWithTimestamp.poll().getKey();
        // Determine the execution time for the process, considering the time quantum and remaining burst time
        int executionTime = min(process.getBurstTime() - process.getExecutionTime(), timeQuantum);
        // Update the objective time based on the maximum of the current objective time and the arrival time of the process,
        // then add the execution time to get the new objective time
        objectiveTime = max(objectiveTime, process.getArrivalTime()) + executionTime;
        // Update the threshold with the new objective time
        threshold = objectiveTime;

        return new Pair<>(process, executionTime);
    }

    @Override
    public void addToReadyQueue(int t) {
        pqWithTimestamp.add(new Pair<>(processQueue.get(t), lastTimestamp));
        lastTimestamp++;
    }

    @Override
    public Pair<Process, Integer> finalStep(Process process, Integer executionTime) {
        // If the process has not finished yet
        if (process.getExecutionTime() + executionTime != process.getBurstTime()) {
            pqWithTimestamp.add(new Pair<>(process, lastTimestamp));
            lastTimestamp++;
        }
        return new Pair<>(process, executionTime);
    }

    @Override
    protected int getSecondaryCriteria(Process process) {
        return process.getPriority();
    }

    @Override
    protected int compareProcesses(Process p1, Process p2) {
        if (p1.getPriority() != p2.getPriority())
            return p1.getPriority() - p2.getPriority();
        return p1.getArrivalTime() - p2.getArrivalTime();
    }

    @Override
    protected int compareProcessesWithTimestamp(Pair<Process, Integer> p1, Pair<Process, Integer> p2) {
        Process p1p = p1.getKey();
        Integer p1ts = p1.getValue();
        Process p2p = p2.getKey();
        Integer p2ts = p2.getValue();
        if (p1p.getPriority() != p2p.getPriority())
            return p1p.getPriority() - p2p.getPriority();
        return p1ts - p2ts;
    }

}
