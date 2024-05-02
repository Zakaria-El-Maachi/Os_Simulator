package com.cs.os.cpuscheduler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class SRTFTest extends BaseSchedulerTest {

    @Override
    CPUScheduler createScheduler() {
        return new CPUScheduler(new SRTF(1)); // Dummy tq value, overridden in test cases
    }

    @ParameterizedTest
    @MethodSource("processData")
    void testRunScheduler(List<Process> processes, List<List<Integer>> expected, Double expectedAverageTurnaround, Double expectedAverageWaiting, int tq) {
        scheduler = new CPUScheduler(new SRTF(tq));
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
                                Arrays.asList(2, 2, 6, 15, 13, 7),
                                Arrays.asList(3, 4, 4, 8, 4, 0),
                                Arrays.asList(4, 6, 5, 20, 14, 9),
                                Arrays.asList(5, 8, 2, 10, 2, 0)
                        ),
                        7.2,
                        3.2,
                        1
                },
                new Object[]{ // Test case 2
                        Arrays.asList(
                                new Process(1, 17, 3),
                                new Process(2, 12, 12),
                                new Process(3, 3, 20),
                                new Process(4, 8, 10),
                                new Process(5, 12, 17)
                        ),
                        Arrays.asList(
                                Arrays.asList(3, 3, 20, 48, 45, 25),
                                Arrays.asList(4, 8, 10, 18, 10, 0),
                                Arrays.asList(2, 12, 12, 33, 21, 9),
                                Arrays.asList(5, 12, 17, 65, 53, 36),
                                Arrays.asList(1, 17, 3, 21, 4, 1)
                        ),
                        26.6,
                        14.2,
                        1
                }
        );
    }
}
