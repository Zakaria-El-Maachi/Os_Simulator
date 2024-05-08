package com.os.mmu;

import com.os.Process;

public class Segment {
    private long base, limit;
    private Process process;

    public Segment(long base, long limit){
        this.base = base;
        this.limit = limit;
    }

    public long getBase() {
        return base;
    }

    public void setBase(long base) {
        this.base = base;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public boolean isAllocated() {
        return process != null;
    }


}
