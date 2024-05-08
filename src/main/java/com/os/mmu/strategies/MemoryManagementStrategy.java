package com.os.mmu.strategies;

import com.os.Process;
import com.os.mmu.Segment;
import java.util.LinkedList;
import java.util.ListIterator;

public abstract class MemoryManagementStrategy {

    protected long unit;

    protected LinkedList<Segment> mapping;

    protected ListIterator<Segment> segmentIterator;
    public void setMapping(LinkedList<Segment> mapping) {
        this.mapping = mapping;
        this.segmentIterator = this.mapping.listIterator();
    }

    public void setUnit(long unit) {
        this.unit = unit;
    }

    protected void resetIterator() {
        this.segmentIterator = mapping.listIterator();
    }

    public abstract ListIterator<Segment> findFit(Process process);

    public ListIterator<Segment> findSegment(int processID) {
        ListIterator<Segment> it = mapping.listIterator();
        Segment curSegment;
        while(it.hasNext()) {
            curSegment = it.next();
            if(curSegment.isAllocated() && curSegment.getProcess().getProcessID() == processID) {
                return it;
            }
        }
        return null;
    }

    public long fitLeftover(Process process, Segment s) {
        return s.getLimit() - (process.getSize() + unit - 1) / unit;
    }

    protected boolean fits(Process process, Segment s) {
        return fitLeftover(process, s) >= 0;
    }
}
