package com.os.cpuscheduler.algorithms;

import com.os.Process;
import javafx.util.Pair;
import java.util.PriorityQueue;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class SRTF extends AlgorithmTemplate {

    private PriorityQueue<Pair<Integer, Process>> pq = new PriorityQueue<>(SRTF::compareRemainingTime);

    public SRTF (int tq) { super(tq); }
    @Override
    public void checkIfReadyQueueIsEmpty() {
        if (pq.isEmpty() && pid < numberOfProcesses) {
            pq.add(new Pair<>(processQueue.get(pid).getBurstTime(), processQueue.get(pid)));
            objectiveTime = processQueue.get(pid).getArrivalTime();
            pid++;
        }
    }

    @Override
    public Pair<Process, Integer> updateTimes() {
        Process process = pq.poll().getValue();
        int executionTime = min(process.getBurstTime() - process.getExecutionTime(), timeQuantum);
        objectiveTime = max(objectiveTime, process.getLastIdle()) + executionTime;
        threshold = objectiveTime;
        return new Pair<>(process, executionTime);
    }

    @Override
    public void addToReadyQueue(int t) {
        pq.add(new Pair<>(processQueue.get(pid).getBurstTime(), processQueue.get(pid)));
    }

    @Override
    public Pair<Process, Integer> finalStep(Process process, Integer executionTime) {
        // If the process has not finished yet
        if (process.getExecutionTime() + executionTime < process.getBurstTime())
            pq.add(new Pair<>(process.getBurstTime() - process.getExecutionTime() - executionTime, process));
        return new Pair<>(process, executionTime);
    }

    @Override
    protected int getSecondaryCriteria(Process process) {
        return process.getBurstTime();
    }

    @Override
    protected int compareProcesses(Process p1, Process p2) { return 0; }

    @Override
    protected int compareProcessesWithTimestamp(Pair<Process, Integer> p1, Pair<Process, Integer> p2) {
        return 0;
    }


    public static int compareRemainingTime(Pair<Integer, Process> p1, Pair<Integer, Process>p2){
        return Integer.compare(p1.getKey(), p2.getKey());
    }

}
