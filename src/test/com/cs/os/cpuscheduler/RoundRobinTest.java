package com.cs.os.cpuscheduler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class RoundRobinTest extends BaseSchedulerTest {

    @Override
    CPUScheduler createScheduler() {
        return new CPUScheduler(new RoundRobin(0)); // Dummy tq value, overridden in test cases
    }

    @ParameterizedTest
    @MethodSource("processData")
    void testRunScheduler(List<Process> processes, List<List<Integer>> expected, Double expectedAverageTurnaround, Double expectedAverageWaiting, int tq) {
        scheduler = new CPUScheduler(new RoundRobin(tq));
        runAndAssert(processes, expected, expectedAverageTurnaround, expectedAverageWaiting);
    }

    // Method to provide test data
    private static Stream<Object[]> processData() {
        return Stream.of(
                new Object[]{ // Test case 1
                        Arrays.asList(
                                new Process(1, 0, 4),
                                new Process(2, 0, 3),
                                new Process(3, 6, 7),
                                new Process(4, 11, 4),
                                new Process(5, 12, 2)
                        ),
                        Arrays.asList(
                                Arrays.asList(1, 0, 4, 6, 6, 2),
                                Arrays.asList(2, 0, 3, 7, 7, 4),
                                Arrays.asList(3, 6, 7, 20, 14, 7),
                                Arrays.asList(4, 11, 4, 19, 8, 4),
                                Arrays.asList(5, 12, 2, 17, 5, 3)
                        ),
                        8.0,
                        4.0,
                        2
                },
                new Object[]{ // Test case 2
                        Arrays.asList(
                                new Process(1, 1, 3),
                                new Process(2, 3, 5),
                                new Process(3, 5, 12),
                                new Process(4, 12, 10),
                                new Process(5, 15, 1),
                                new Process(6, 20, 2),
                                new Process(7, 50, 20),
                                new Process(8, 66, 4)
                        ),
                        Arrays.asList(
                                Arrays.asList(1, 1, 3, 4, 3, 0),
                                Arrays.asList(2, 3, 5, 12, 9, 4),
                                Arrays.asList(3, 5, 12, 30, 25, 13),
                                Arrays.asList(4, 12, 10, 34, 22, 12),
                                Arrays.asList(5, 15, 1, 19, 4, 3),
                                Arrays.asList(6, 20, 2, 27, 7, 5),
                                Arrays.asList(7, 50, 20, 73, 23, 3),
                                Arrays.asList(8, 66, 4, 74, 8, 4)
                        ),
                        12.625,
                        5.5,
                        3
                }
        );
    }
}