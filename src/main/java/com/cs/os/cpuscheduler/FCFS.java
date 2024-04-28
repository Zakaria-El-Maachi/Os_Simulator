package com.cs.os.cpuscheduler;

import javafx.util.Pair;

import java.util.*;

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
        List<Process> processList = new ArrayList<>(processQueue); // Convert the queue to a list for sorting
        processList.sort(Comparator.comparingInt(Process::getArrivalTime));
        // Clear the queue and add sorted processes back to the queue
        processQueue.clear();
        processQueue.addAll(processList);

        System.out.println("Processes have been sorted by arrival time.");
        if (!processQueue.isEmpty()) {
            this.pIterator = processQueue.iterator();
        }
    }

}
