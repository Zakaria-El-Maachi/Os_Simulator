/*
package com.cs.os.cpuscheduler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class HRRNTest extends BaseSchedulerTest {
    @Override
    CPUScheduler createScheduler() {
        return new CPUScheduler(new SJF());
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
                                new Process(1, 1, 3),
                                new Process(2, 3, 6),
                                new Process(3, 5, 8),
                                new Process(4, 7, 4),
                                new Process(5, 8, 5)
                        ),
                        Arrays.asList(
                                Arrays.asList(1, 1, 3, 4, 3, 0),
                                Arrays.asList(2, 3, 6, 10, 7, 1),
                                Arrays.asList(3, 5, 8, 14, 24, 21),
                                Arrays.asList(4, 4, 2, 24, 20, 18),
                                Arrays.asList(5, 9, 1, 22, 13, 12)
                        ),
                        22.4,
                        15.2
                }
        );
    }
}*/
