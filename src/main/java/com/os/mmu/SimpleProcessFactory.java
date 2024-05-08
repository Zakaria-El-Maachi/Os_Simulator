package com.os.mmu;

import com.os.Process;

import java.util.Random;

public class SimpleProcessFactory implements MemoryProcessFactory {

    private static int biggestID = 0; // To keep track of existing process IDs

    @Override
    public Process createProcess(long size) {
        // Ensure the process ID is unique
        return new Process(++biggestID, 0, 0, size);
    }

    @Override
    public void reset() {
        biggestID = 0;
    }
}
