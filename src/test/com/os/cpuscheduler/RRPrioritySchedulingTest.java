package com.os.cpuscheduler;

import com.os.cpuscheduler.algorithms.RRPriorityScheduling;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class RRPrioritySchedulingTest extends BaseSchedulerTest {

    @Override
    CPUScheduler createScheduler() {
        return new CPUScheduler(new RRPriorityScheduling(0)); // Dummy tq value, overridden in test cases
    }

    @ParameterizedTest
    @MethodSource("processData")
    void testRunScheduler(List<Process> processes, List<List<Integer>> expected, Double expectedAverageTurnaround, Double expectedAverageWaiting, int tq) {
        scheduler = new CPUScheduler(new RRPriorityScheduling(tq));
        runAndAssert(processes, expected, expectedAverageTurnaround, expectedAverageWaiting);
    }

    // Method to provide test data
    private static Object processData() {
        return Stream.of(
                new Object[]{ // Test case 1
                        Arrays.asList(
                                new Process(1, 0, 4, 2, 0),
                                new Process(2, 1, 5, 1, 0),
                                new Process(3, 2, 6, 2, 0),
                                new Process(4, 3, 1, 1, 0)
                        ),
                        Arrays.asList(
                                Arrays.asList(1, 0, 4, 13, 13, 9),
                                Arrays.asList(2, 1, 5, 9, 8, 3),
                                Arrays.asList(3, 2, 6, 16, 14, 8),
                                Arrays.asList(4, 3, 1, 7, 4, 3)
                        ),
                        9.75,
                        5.75,
                        3
                },
                new Object[]{ // Test case 2
                        Arrays.asList(
                                new Process(1, 0, 4, 2, 0),
                                new Process(2, 1, 5, 1, 0),
                                new Process(3, 2, 6, 2, 0),
                                new Process(4, 3, 1, 1, 0),
                                new Process(5, 20, 10, 2, 0)
                        ),
                        Arrays.asList(
                                Arrays.asList(1, 0, 4, 13, 13, 9),
                                Arrays.asList(2, 1, 5, 9, 8, 3),
                                Arrays.asList(3, 2, 6, 16, 14, 8),
                                Arrays.asList(4, 3, 1, 7, 4, 3),
                                Arrays.asList(5, 20, 10, 30, 10, 0)
                        ),
                        9.8,
                        4.6,
                        3
                }
        );
    }
}