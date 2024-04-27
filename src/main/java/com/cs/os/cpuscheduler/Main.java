package com.cs.os.cpuscheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> arrivalTimes = new ArrayList<>(Arrays.asList(4, 3, 2, 1, 9));
        List<Integer> burstTimes = new ArrayList<>(Arrays.asList(2, 3, 10, 20, 1));


        /*List<Integer> arrivalTimes = new ArrayList<>(Arrays.asList(4, 3, 2, 1, 60));
        List<Integer> burstTimes = new ArrayList<>(Arrays.asList(2, 3, 10, 20, 1));*/

        /*List<Integer> arrivalTimes = new ArrayList<>(Arrays.asList(10, 20, 30, 40));
        List<Integer> burstTimes = new ArrayList<>(Arrays.asList(1, 1, 1, 1));*/

        List<Process> processes = new ArrayList<>();
        for (int i = 1; i <= arrivalTimes.size(); i++) {
            Process process = new Process(i, arrivalTimes.get(i-1), burstTimes.get(i-1));
            processes.add(process);
        }

        /* Uncomment to run */
        /*FCFS algo = new FCFS();*/
        SJF algo = new SJF();

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
