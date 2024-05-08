package com.os.mmu.strategies;

import com.os.Process;
import com.os.mmu.Segment;
import java.util.ListIterator;

public class NextFit extends MemoryManagementStrategy {

    private int lastPosition;

    public NextFit() {
        this.lastPosition = 0; // Initialize the last position to the start of the list
    }

    @Override
    public ListIterator<Segment> findFit(Process process) {
        // Start searching from the last position found
        segmentIterator = mapping.listIterator(lastPosition);
        Segment curSegment;
        boolean wrappedAround = false;

        // Continue searching until the end of the list
        while (segmentIterator.hasNext() || !wrappedAround) {
            // If we have reached the end, wrap around to the start
            if (!segmentIterator.hasNext() && !wrappedAround) {
                segmentIterator = mapping.listIterator();
                wrappedAround = true; // We have wrapped around
            }

            // Get the current segment
            curSegment = segmentIterator.next();

            // Check if it fits the process
            if (!curSegment.isAllocated() && fits(process, curSegment)) {
                // Save the last position where a suitable segment was found
                lastPosition = segmentIterator.previousIndex();
                return segmentIterator;
            }
        }

        // No suitable segment found
        return null;
    }
}
