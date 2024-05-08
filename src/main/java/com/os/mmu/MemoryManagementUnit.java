package com.os.mmu;

import com.os.Process;
import com.os.mmu.strategies.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class MemoryManagementUnit implements MemoryManager{
    private long size, unit, numProcesses;
    private final LinkedList<Segment> mapping;
    private MemoryManagementStrategy strategy;

    private final Exception ProcessNotFound = new Exception("No process found with that ID."),
                            AllocationFail = new Exception("Allocation failed. Not enough memory."),
                            AddressException = new Exception("Address longer than the limit of the process.");

    public MemoryManagementUnit(long size, long unit) {
        this.size = size;
        this.unit = unit;
        this.numProcesses = 0;
        this.mapping = new LinkedList<>();
        this.mapping.add(new Segment(0, size/unit));
    }

    @Override
    public void setImplementation(MemoryManagementStrategy strategy) {
        this.strategy = strategy;
        this.strategy.setMapping(this.mapping);
        this.strategy.setUnit(unit);
    }

    public long getEffectiveSize() {
        return size - size%unit;
    }

    @Override
    public Segment allocate(Process process) throws Exception{
        ListIterator<Segment> iterator = strategy.findFit(process);

        if(iterator == null) throw AllocationFail;

        numProcesses++;
        // Create a new segment for the allocated process
        Segment segment = iterator.previous();
        long base = segment.getBase();
        long requiredUnits = (process.getSize() + unit - 1) / unit;

        // Remove the current segment since it will be allocated
        iterator.remove();
        Segment allocatedSegment = new Segment(base, requiredUnits);
        allocatedSegment.setProcess(process);
        iterator.add(allocatedSegment);

        // Add the remaining segment to the list
        long remainingSize = strategy.fitLeftover(process, segment);
        if(remainingSize > 0) {
            long newBase = base + requiredUnits;
            Segment remainingSegment = new Segment(newBase, remainingSize);
            iterator.add(remainingSegment);
        }

        // Return the process ID as an indication of successful allocation
        return allocatedSegment;
    }

    @Override
    public void deallocate(int processID) throws Exception{
        ListIterator<Segment> iterator = strategy.findSegment(processID);

        if(iterator == null) throw ProcessNotFound;

        numProcesses--;
        Segment segment;
        long holeUnits = 0;
        long base = size;

        if(iterator.hasNext()) {
            segment = iterator.next();
            if(segment.isAllocated()) {
                iterator.previous();
            } else {
                holeUnits += segment.getLimit();
                base = Math.min(base, segment.getBase());
                iterator.remove();
            }
        }

        segment = iterator.previous();
        holeUnits += segment.getLimit();
        base = Math.min(base, segment.getBase());
        iterator.remove();

        if(iterator.hasPrevious()) {
            segment = iterator.previous();
            if(segment.isAllocated()) {
                iterator.next();
            } else {
                holeUnits += segment.getLimit();
                base = Math.min(base, segment.getBase());
                iterator.remove();
            }
        }

        iterator.add(new Segment(base, holeUnits));

    }

    @Override
    public long convert(int processID, long address) throws Exception {
        ListIterator<Segment> it = strategy.findSegment(processID);
        if(it == null)
            throw ProcessNotFound;
        Segment allocated = it.previous();
        if(address > allocated.getLimit())
            throw AddressException;
        return allocated.getBase() + address;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Memory Map:\n");

        // Iterate through the linked list of segments and build the output
        Iterator<Segment> iterator = mapping.iterator();
        boolean firstNode = true;

        while (iterator.hasNext()) {
            Segment segment = iterator.next();

            // Get process ID or -1 if unallocated
            long processId = segment.isAllocated() ? segment.getProcess().getProcessID() : -1;
            long baseAddress = segment.getBase();
            long limit = segment.getLimit();
            String status = segment.isAllocated() ? "Allocated" : "Free";

            // Create the table for the current segment
            sb.append("+------------+------------+\n");
            sb.append(String.format("| Process ID | %10d |\n", processId));
            sb.append(String.format("| Base Addr  | %10d |\n", baseAddress));
            sb.append(String.format("| Limit      | %10d |\n", limit));
            sb.append(String.format("| Status     | %10s |\n", status));
            sb.append("+------------+------------+\n");

            // Add a long arrow between nodes, except for the last node
            if (iterator.hasNext()) {
                sb.append(" ".repeat(13) + "|\n");
                sb.append("↓ " + "-".repeat(10) + " | " + "-".repeat(10) + " ↓\n");
                sb.append(" ".repeat(13) + "↓\n");
            }
        }

        // Return the final representation
        return sb.toString();
    }



    public long getNumProcesses() {
        return numProcesses;
    }

}
