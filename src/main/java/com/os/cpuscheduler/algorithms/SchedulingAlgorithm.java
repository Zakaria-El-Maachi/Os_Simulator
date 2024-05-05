package com.os.cpuscheduler.algorithms;

import com.os.cpuscheduler.Process;
import javafx.util.Pair;
import java.util.List;

public interface SchedulingAlgorithm {
    // Method to execute the scheduling algorithm
    Pair<Process, Integer> schedule();
    void setUpAlgorithm(List<Process> processQueue);
}
