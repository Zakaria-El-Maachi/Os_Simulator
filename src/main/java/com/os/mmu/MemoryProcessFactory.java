package com.os.mmu;

import com.os.Process;

public interface MemoryProcessFactory {
    Process createProcess(long size);
    void reset();
}
