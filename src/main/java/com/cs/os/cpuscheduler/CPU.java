package com.cs.os.cpuscheduler;

class CPU {
    public void executeProcess(Process process, float time) {
        // Simulate process execution by sleeping for the given time in milliseconds
        try {
            System.out.println("Executing process: " + process.getProcessID() + " for " + time + " units");
            Thread.sleep((int) time * 100); // Each unit of time is represented as 100 milliseconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

