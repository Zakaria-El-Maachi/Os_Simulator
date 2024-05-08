package com.os.cpuscheduler;

import com.os.Process;
import com.os.cpuscheduler.CPUScheduler;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

public abstract class BaseSchedulerTest {

    protected CPUScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = createScheduler();
    }

    abstract CPUScheduler createScheduler();

    protected void runAndAssert(List<Process> processes, List<List<Integer>> expected, Double expectedAverageTurnaround, Double expectedAverageWaiting) {
        for (Process p : processes) {
            scheduler.addProcess(p);
        }
        scheduler.runScheduler();
        List<Process> completedProcesses = scheduler.getProcessQueue();
        Double averageTurnaround = scheduler.getAverageTurnaround();
        Double averageWaitingTime = scheduler.getAverageWaitingTime();
        for (int i = 0; i < expected.size(); i++) {
            assertProcessEquals(expected.get(i), completedProcesses.get(i));
        }
        assertEquals(expectedAverageTurnaround, averageTurnaround);
        assertEquals(expectedAverageWaiting, averageWaitingTime);
    }

    private void assertProcessEquals(List<Integer> expected, Process actual) {
        assertEquals(
                expected.get(0),
                actual.getProcessID(),
                "Process ID mismatch: expected " + expected.get(0) + ", but got " + actual.getProcessID()
        );

        assertEquals(
                expected.get(1),
                actual.getArrivalTime(),
                "Arrival time mismatch: expected " + expected.get(1) + ", but got " + actual.getArrivalTime()
        );

        assertEquals(
                expected.get(2),
                actual.getBurstTime(),
                "Burst time mismatch: expected " + expected.get(2) + ", but got " + actual.getBurstTime()
        );

        assertEquals(
                expected.get(3),
                actual.getLastIdle(),
                "Last idle time mismatch: expected " + expected.get(3) + ", but got " + actual.getLastIdle()
        );

        assertEquals(
                expected.get(4),
                actual.getTurnaround(),
                "Turnaround time mismatch: expected " + expected.get(4) + ", but got " + actual.getTurnaround()
        );

        assertEquals(
                expected.get(5),
                actual.getWaitingTime(),
                "Waiting time mismatch: expected " + expected.get(5) + ", but got " + actual.getWaitingTime()
        );
    }

}
