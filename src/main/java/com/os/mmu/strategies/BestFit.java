package com.os.mmu.strategies;

import com.os.Process;
import com.os.mmu.Segment;

import java.util.ListIterator;

public class BestFit extends MemoryManagementStrategy{

    @Override
    public ListIterator<Segment> findFit(Process process) {
        resetIterator();
        int bestFitIndex = -1, index = 0;
        long minLeftover = Long.MAX_VALUE;

        while (segmentIterator.hasNext()) {
            Segment curSegment = segmentIterator.next();

            // Check if the current segment is unallocated and can accommodate the process
            if (!curSegment.isAllocated() && fits(process, curSegment)) {
                long leftover = leftover(process, curSegment);

                // If the leftover space is smaller than the previous minimum, update the best fit segment
                if (leftover < minLeftover) {
                    minLeftover = leftover;
                    bestFitIndex = index;
                }
            }
            index++;
        }

        if(bestFitIndex == -1)
            return null;
        return mapping.listIterator(++bestFitIndex);
    }


}
