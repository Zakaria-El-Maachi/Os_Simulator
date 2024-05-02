package com.cs.os.cpuscheduler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


class HRRNTest extends BaseSchedulerTest {
    @Override
    CPUScheduler createScheduler() {
        return new CPUScheduler(new HRRN());
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
                                new Process(1, 0, 3),
                                new Process(2, 2, 6),
                                new Process(3, 4, 4),
                                new Process(4, 6, 5),
                                new Process(5, 8, 2)
                        ),
                        Arrays.asList(
                                Arrays.asList(1, 0, 3, 3, 3, 0),
                                Arrays.asList(2, 2, 6, 9, 7, 1),
                                Arrays.asList(3, 4, 4, 13, 9, 5),
                                Arrays.asList(4, 6, 5, 20, 14, 9),
                                Arrays.asList(5, 8, 2, 15, 7, 5)
                        ),
                        8.0,
                        4.0
                },
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
                                Arrays.asList(3, 5, 8, 27, 22, 14),
                                Arrays.asList(4, 7, 4, 14, 7, 3),
                                Arrays.asList(5, 8, 5, 19, 11, 6)
                        ),
                        10.0,
                        4.8
                }
        );
    }
}
