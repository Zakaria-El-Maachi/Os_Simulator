package com.os.cpuscheduler.algorithms;

import com.os.cpuscheduler.Process;
import javafx.util.Pair;
import static java.lang.Integer.max;

public class PrioritySchedulingPreemptive extends AlgorithmTemplate {

    public PrioritySchedulingPreemptive() { super(); }

    @Override
    public void checkIfReadyQueueIsEmpty() { checkIfReadyQueueIsEmptyOption1(); }

    @Override
    public Pair<Process, Integer> updateTimes() { return new Pair<>(null, null); }

    @Override
    public void addToReadyQueue(int t) { addToReadyQueueOption1(t); }

    @Override
    public Pair<Process, Integer> finalStep(Process p, Integer et) {
        Process process = pq.peek();

        // Determine the higher priority PID and the maximum time difference for higher priority processes
        higherPriorityPid = pid;
        int maxTime = -1;
        for (int i = higherPriorityPid; i < numberOfProcesses; i++) {
            if (processQueue.get(i).getPriority() < process.getPriority()) {
                maxTime = processQueue.get(i).getArrivalTime() - objectiveTime;
                break;
            }
        }

        // Calculate the execution time based on whether there are higher priority processes or not
        int executionTime;

        if (maxTime == -1 || maxTime >= (process.getBurstTime() - process.getExecutionTime())) {
            executionTime = process.getBurstTime() - process.getExecutionTime();
            pq.poll();
        } else {
            executionTime =  maxTime;
        }

        // Update the objective time and threshold based on the execution time
        objectiveTime = max(objectiveTime, process.getArrivalTime());
        threshold = objectiveTime + executionTime;
        objectiveTime += executionTime;

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
        return 0;
    }

}