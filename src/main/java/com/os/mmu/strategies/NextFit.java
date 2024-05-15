package com.os.mmu.strategies;

import com.os.Process;
import com.os.mmu.Segment;
import java.util.ListIterator;

public class NextFit extends MemoryManagementStrategy {

    private long lastPosition;

    public NextFit() {
        this.lastPosition = 0; // Initialize the last position to the start of the list
    }

    @Override
    public ListIterator<Segment> findFit(Process process) {
        // Local variable to the function
        long lastPositionLocal = lastPosition;

        // Search the index of the segment at which the search has stopped before
        resetIterator();
        Segment curSegment;
        long maxUnit;
        int lastIndex = 0;
        while(segmentIterator.hasNext()) {
            curSegment = segmentIterator.next();
            maxUnit = curSegment.getBase() + curSegment.getLimit();
            if (maxUnit > lastPositionLocal) {
                break;
            }
            lastIndex++;
        }

        // Start searching from the last position found
        segmentIterator = mapping.listIterator(lastIndex);
        boolean wrappedAround = false;

        // Continue searching until the end of the list
        while (segmentIterator.hasNext() || !wrappedAround) {
            // Stop at the segment corresponding to the lastPosition variable
            if (segmentIterator.nextIndex() > lastIndex+1) {
                break;
            }

            // If we have reached the end, wrap around to the start
            if (!segmentIterator.hasNext()) {
                segmentIterator = mapping.listIterator();
                lastPositionLocal = 0;
                wrappedAround = true; // We have wrapped around
            }

            // Get the current segment
            curSegment = segmentIterator.next();

            // Check if it fits the process
            if (!curSegment.isAllocated()) {
                if (curSegment.getBase() < lastPositionLocal) {
                    Segment segmentToConsider = new Segment(lastPositionLocal, curSegment.getLimit() - lastPositionLocal);
                    if (fits(process, segmentToConsider)) {
                        segmentIterator.remove();
                        Segment headSegment = new Segment(curSegment.getBase(), lastPositionLocal - curSegment.getBase());
                        segmentIterator.add(headSegment);
                        segmentIterator.add(segmentToConsider);

                        // Save the last position where a suitable segment was found
                        lastPosition += (process.getSize() + unit - 1) / unit;

                        return segmentIterator;
                    }
                } else {
                    if (fits(process, curSegment)) {
                        // Save the last position where a suitable segment was found
                        lastPosition = curSegment.getBase() + (process.getSize() + unit - 1) / unit;
                        return segmentIterator;
                    }
                }
            }

        }

        // No suitable segment found
        return null;
    }
}