package com.cs.os.cpuscheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*List<Integer> arrivalTimes = new ArrayList<>(Arrays.asList(4, 3, 2, 1, 9));
        List<Integer> burstTimes = new ArrayList<>(Arrays.asList(2, 3, 10, 20, 1));*/


        /*List<Integer> arrivalTimes = new ArrayList<>(Arrays.asList(4, 3, 2, 1, 60));
        List<Integer> burstTimes = new ArrayList<>(Arrays.asList(2, 3, 10, 20, 1));*/

        /*List<Integer> arrivalTimes = new ArrayList<>(Arrays.asList(10, 20, 30, 40));
        List<Integer> burstTimes = new ArrayList<>(Arrays.asList(1, 1, 1, 1));*/

        /*List<Integer> arrivalTimes = new ArrayList<>(Arrays.asList(0, 4));
        List<Integer> burstTimes = new ArrayList<>(Arrays.asList(10, 2));;
        List<Integer> priorities = new ArrayList<>(Arrays.asList(2, 1));*/


        /*List<Integer> arrivalTimes = new ArrayList<>(Arrays.asList(0, 0, 6, 11, 12));
        List<Integer> burstTimes = new ArrayList<>(Arrays.asList(4, 3, 7, 4, 2));;
        List<Integer> priorities = new ArrayList<>(Arrays.asList(1, 2, 1, 3, 2));*/

        /*List<Integer> arrivalTimes = new ArrayList<>(Arrays.asList(1, 4, 6, 2, 100));
        List<Integer> burstTimes = new ArrayList<>(Arrays.asList(10, 15, 2, 20, 2));;
        List<Integer> priorities = new ArrayList<>(Arrays.asList(3, 2, 1, 1, 3));*/

        /*List<Integer> arrivalTimes = new ArrayList<>(Arrays.asList(0, 1, 3, 5, 6));
        List<Integer> burstTimes = new ArrayList<>(Arrays.asList(5, 3, 6, 1, 4));*/

        /*List<Integer> arrivalTimes = new ArrayList<>(Arrays.asList(1, 3, 5, 12, 15, 20, 50, 66));
        List<Integer> burstTimes = new ArrayList<>(Arrays.asList(3, 5, 12, 10, 1, 2, 20, 4));*/

        /*List<Integer> arrivalTimes = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        List<Integer> burstTimes = new ArrayList<>(Arrays.asList(4, 5, 6, 1));
        List<Integer> priorities = new ArrayList<>(Arrays.asList(2, 1, 2, 1));*/

        List<Integer> arrivalTimes = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        List<Integer> burstTimes = new ArrayList<>(Arrays.asList(4, 5, 6, 1));
        List<Integer> priorities = new ArrayList<>(Arrays.asList(2, 1, 2, 1));

        int tq = 3;

        List<Process> processes = new ArrayList<>();
        for (int i = 1; i <= arrivalTimes.size(); i++) {
            Process process = new Process(i, arrivalTimes.get(i-1), burstTimes.get(i-1), priorities.get(i-1));
            processes.add(process);
        }

        /* Uncomment to run */
        /*FCFS algo = new FCFS();*/
        /*SJF algo = new SJF();*/
        /*PrioritySchedulingNonPreemptive algo = new PrioritySchedulingNonPreemptive();*/
        /*PrioritySchedulingPreemptive algo = new PrioritySchedulingPreemptive();*/
        /*RoundRobin algo = new RoundRobin(tq);*/
        RRPriorityScheduling algo = new RRPriorityScheduling(tq);

        CPUScheduler cs = new CPUScheduler(algo);

        for (Process p : processes) {
            cs.addProcess(p);
        }

        cs.runScheduler();

        for (Process p : processes) {
            System.out.println(p);
            System.out.println("============");
        }
    }
}
