package com.cs.os.cpuscheduler;

class CPU {
    public void executeProcess(Process process, int time) {
        // Simulate process execution by sleeping for the given time in milliseconds
        try {
            System.out.println("Executing process: " + process.getProcessID() + " for " + time + " units");
            Thread.sleep(time * 100L); // Each unit of time is represented as 100 milliseconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

