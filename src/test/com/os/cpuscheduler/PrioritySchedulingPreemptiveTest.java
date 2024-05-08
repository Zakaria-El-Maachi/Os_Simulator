package com.os.cpuscheduler;

import com.os.Process;
import com.os.cpuscheduler.algorithms.PrioritySchedulingPreemptive;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class PrioritySchedulingPreemptiveTest  extends BaseSchedulerTest {

    @Override
    CPUScheduler createScheduler() {
        return new CPUScheduler(new PrioritySchedulingPreemptive());
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
                                Arrays.asList(2, 0, 3, 14, 14, 11),
                                Arrays.asList(3, 6, 7, 13, 7, 0),
                                Arrays.asList(4, 11, 4, 20, 9, 5),
                                Arrays.asList(5, 12, 2, 16, 4, 2)
                        ),
                        7.6,
                        3.6
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
                                Arrays.asList(1, 1, 10, 48, 47, 37),
                                Arrays.asList(4, 2, 20, 22, 20, 0),
                                Arrays.asList(2, 4, 15, 39, 35, 20),
                                Arrays.asList(3, 6, 2, 24, 18, 16),
                                Arrays.asList(5, 100, 2, 102, 2, 0)
                        ),
                        24.4,
                        14.6
                },
                new Object[]{ // Test case 3
                        Arrays.asList(
                                new Process(1, 0, 4, 2, 0),
                                new Process(2, 1, 5, 1, 0),
                                new Process(3, 2, 6, 2, 0),
                                new Process(4, 3, 1, 1, 0)
                        ),
                        Arrays.asList(
                                Arrays.asList(1, 0, 4, 10, 10, 6),
                                Arrays.asList(2, 1, 5, 6, 5, 0),
                                Arrays.asList(3, 2, 6, 16, 14, 8),
                                Arrays.asList(4, 3, 1, 7, 4, 3)
                        ),
                        8.25,
                        4.25
                }
        );
    }
}
