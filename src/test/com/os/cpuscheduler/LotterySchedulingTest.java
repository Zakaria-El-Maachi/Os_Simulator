package com.os.cpuscheduler;

import com.os.cpuscheduler.algorithms.LotteryScheduling;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LotterySchedulingTest extends BaseSchedulerTest {

    @Override
    CPUScheduler createScheduler() {
        return new CPUScheduler(new LotteryScheduling(1));
    }

    @Test
    void testLotteryScheduling() {
        List<Process> processes = createProcesses();

        int runs = 10000; // Number of times to run the scheduler
        Map<Integer, Integer> executionCounts = new HashMap<>(), waitingTotals = new HashMap<>();


        for (int i = 0; i < runs; i++) {
            processes = createProcesses();
            CPUScheduler scheduler = createScheduler();
            for(Process pr: processes)
                scheduler.addProcess(pr);
            scheduler.runScheduler();

            // Track which process was executed first
            int firstProcessExecuted = scheduler.getProcessExecutionTimeline().getFirst().getKey().getProcessID();
            executionCounts.merge(firstProcessExecuted, 1, Integer::sum);

            for(Process pr: processes)
                waitingTotals.merge(pr.getProcessID(), pr.getWaitingTime(), Integer::sum);
        }

        // Calculate the probabilities based on ticket allocations
        int totalTickets = 100; // Sum of tickets (10 + 20 + 30 + 40)
        for (int pid = 1; pid <= 4; pid++) {
            int expectedExecutions = (int) ((double) processes.get(pid - 1).getTickets() / totalTickets * runs);
            int actualExecutions = executionCounts.getOrDefault(pid, 0);

            // Assert that the actual executions are within a reasonable range of expected executions
            double tolerance = 0.1 * runs; // 10% tolerance
            assertTrue(Math.abs(actualExecutions - expectedExecutions) <= tolerance,
                    "Process " + pid + " executions not within expected range");
        }

        int prevWaiting = 0;
        for(int pid = 4; pid > 0; pid--){
            assertTrue(waitingTotals.get(pid) >= prevWaiting, "Process " + pid + " Total Waiting Time not within expected range");
            prevWaiting = waitingTotals.get(pid);
        }
    }

    List<Process> createProcesses(){
        return Arrays.asList(
                new Process(1, 0, 20, 0, 10), // Process with 10 tickets
                new Process(2, 0, 20, 0, 20), // Process with 20 tickets
                new Process(3, 0, 20, 0, 30), // Process with 30 tickets
                new Process(4, 0, 20, 0, 40)  // Process with 40 tickets
        );
    }

}
