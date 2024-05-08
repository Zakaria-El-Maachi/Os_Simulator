package com.os.cpuscheduler;

import com.os.Process;

class CPU {

    private CpuState state = CpuState.Idle;
    public void executeProcess(Process process, int time) {
        // Simulate process execution by sleeping for the given time in milliseconds
        try {
            state = CpuState.Running;
            System.out.println("Executing process: " + Process.intToAlphabet(process.getProcessID()) + " for " + time + " units");
            Thread.sleep((int) time * 100); // Each unit of time is represented as 100 milliseconds
            state = CpuState.Idle;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CpuState getState(){
        return state;
    }

}

