package com.os.cpuscheduler;

import com.os.cpuscheduler.algorithms.SJF;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class SJFTest extends BaseSchedulerTest {

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
                                new Process(1, 4, 2),
                                new Process(2, 3, 3),
                                new Process(3, 2, 10),
                                new Process(4, 1, 20),
                                new Process(5, 9, 1)
                        ),
                        Arrays.asList(
                                Arrays.asList(4, 1, 20, 21, 20, 0),
                                Arrays.asList(3, 2, 10, 37, 35, 25),
                                Arrays.asList(2, 3, 3, 27, 24, 21),
                                Arrays.asList(1, 4, 2, 24, 20, 18),
                                Arrays.asList(5, 9, 1, 22, 13, 12)
                        ),
                        22.4,
                        15.2
                },
                new Object[]{ // Test case 2
                        Arrays.asList(
                                new Process(1, 4, 2),
                                new Process(2, 3, 3),
                                new Process(3, 2, 10),
                                new Process(4, 1, 20),
                                new Process(5, 60, 1)
                        ),
                        Arrays.asList(
                                Arrays.asList(4, 1, 20, 21, 20, 0),
                                Arrays.asList(3, 2, 10, 36, 34, 24),
                                Arrays.asList(2, 3, 3, 26, 23, 20),
                                Arrays.asList(1, 4, 2, 23, 19, 17),
                                Arrays.asList(5, 60, 1, 61, 1, 0)
                        ),
                        19.4,
                        12.2
                },
                new Object[]{ // Test case 3
                        Arrays.asList(
                                new Process(1, 10, 1),
                                new Process(2, 20, 1),
                                new Process(3, 30, 1),
                                new Process(4, 40, 1)
                        ),
                        Arrays.asList(
                                Arrays.asList(1, 10, 1, 11, 1, 0),
                                Arrays.asList(2, 20, 1, 21, 1, 0),
                                Arrays.asList(3, 30, 1, 31, 1, 0),
                                Arrays.asList(4, 40, 1, 41, 1, 0)
                        ),
                        1.0,
                        0.0
                },
                new Object[]{ // Test case 4
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
                                Arrays.asList(2, 3, 5, 9, 6, 1),
                                Arrays.asList(3, 5, 12, 21, 16, 4),
                                Arrays.asList(4, 12, 10, 34, 22, 12),
                                Arrays.asList(5, 15, 1, 22, 7, 6),
                                Arrays.asList(6, 20, 2, 24, 4, 2),
                                Arrays.asList(7, 50, 20, 70, 20, 0),
                                Arrays.asList(8, 66, 4, 74, 8, 4)
                        ),
                        10.75,
                        3.625
                }
        );
    }
}