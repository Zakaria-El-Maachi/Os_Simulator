package com.cs.os.cpuscheduler;

import javafx.util.Pair;
import java.util.Queue;

interface SchedulingAlgorithm {
    // Method to execute the scheduling algorithm
    Pair<Process, Integer> schedule();
    void setUpAlgorithm(Queue<Process> processQueue);
}
