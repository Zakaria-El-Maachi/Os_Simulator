package com.os.mmu;

import com.os.Process;
import com.os.mmu.strategies.MemoryManagementStrategy;

import java.util.LinkedList;

public interface MemoryManager {
    Segment allocate(Process process) throws Exception;
    void deallocate(int processID) throws Exception;
    long convert(int processID, long address) throws Exception;
    void setImplementation(MemoryManagementStrategy strategy);

    long getSize();
    long getUnit();
    LinkedList<Segment> getMapping();
    long getAllocatedMemory();

    void setParams(long size, long unit);
}
