package com.cs.os.cpuscheduler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class PrioritySchedulingNonPreemptiveTest extends BaseSchedulerTest {

    @Override
    CPUScheduler createScheduler() {
        return new CPUScheduler(new PrioritySchedulingNonPreemptive());
    }

    @ParameterizedTest
    @MethodSource("processData")
    void testRunScheduler(List<Process> processes, List<List<Integer>> expected, Double expectedAverageTurnaround, Double expectedAverageWaiting) {
        runAndAssert(processes, expected, expectedAverageTurnaround, expectedAverageWaiting);
    }

    // Method to provide test data
    private static Stream<Object[]> processData() {
        return Stream.of(
                new Object[]{ // Test case 1
                        Arrays.asList(
                                new Process(1, 0, 4, 1, 0),
                                new Process(2, 0, 3, 2, 0),
                                new Process(3, 6, 7, 1, 0),
                                new Process(4, 11, 4, 3, 0),
                                new Process(5, 12, 2, 2, 0)
                        ),
                        Arrays.asList(
                                Arrays.asList(1, 0, 4, 4, 4, 0),
                                Arrays.asList(2, 0, 3, 7, 7, 4),
                                Arrays.asList(3, 6, 7, 14, 8, 1),
                                Arrays.asList(4, 11, 4, 20, 9, 5),
                                Arrays.asList(5, 12, 2, 16, 4, 2)
                        ),
                        6.4,
                        2.4
                },
                new Object[]{ // Test case 2
                        Arrays.asList(
                                new Process(1, 1, 10, 3, 0),
                                new Process(2, 4, 15, 2, 0),
                                new Process(3, 6, 2, 1, 0),
                                new Process(4, 2, 20, 1, 0),
                                new Process(5, 100, 2, 3, 0)
                        ),
                        Arrays.asList(
                                Arrays.asList(1, 1, 10, 11, 10, 0),
                                Arrays.asList(4, 2, 20, 31, 29, 9),
                                Arrays.asList(2, 4, 15, 48, 44, 29),
                                Arrays.asList(3, 6, 2, 33, 27, 25),
                                Arrays.asList(5, 100, 2, 102, 2, 0)
                        ),
                        22.4,
                        12.6
                },
                new Object[]{ // Test case 3
                        Arrays.asList(
                                new Process(1, 0, 4, 2, 0),
                                new Process(2, 1, 5, 1, 0),
                                new Process(3, 2, 6, 2, 0),
                                new Process(4, 3, 1, 1, 0)
                        ),
                        Arrays.asList(
                                Arrays.asList(1, 0, 4, 4, 4, 0),
                                Arrays.asList(2, 1, 5, 9, 8, 3),
                                Arrays.asList(3, 2, 6, 16, 14, 8),
                                Arrays.asList(4, 3, 1, 10, 7, 6)
                        ),
                        8.25,
                        4.25
                }
        );
    }
}