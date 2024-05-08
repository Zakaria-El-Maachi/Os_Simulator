package com.os.mmu;

import com.os.Process;
import com.os.mmu.strategies.MemoryManagementStrategy;

public interface MemoryManager {
    Segment allocate(Process process) throws Exception;
    void deallocate(int processID) throws Exception;
    long convert(int processID, long address) throws Exception;
    void setImplementation(MemoryManagementStrategy strategy);
}
