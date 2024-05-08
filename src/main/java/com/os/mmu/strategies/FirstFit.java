package com.os.mmu.strategies;

import com.os.Process;
import com.os.mmu.Segment;
import java.util.ListIterator;

public class FirstFit extends MemoryManagementStrategy {

    @Override
    public ListIterator<Segment> findFit(Process process) {
        resetIterator();
        Segment curSegment;
        while(segmentIterator.hasNext()){
            curSegment = segmentIterator.next();
            if(!curSegment.isAllocated() && fits(process, curSegment)) return segmentIterator;
        }
        return null;
    }

}
