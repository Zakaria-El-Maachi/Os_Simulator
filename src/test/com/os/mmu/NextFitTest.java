package com.os.mmu;

import com.os.Process;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThrows;

public class NextFitTest extends MemoryManagementUnitTestBase {
    @Override
    protected int getAlgorithm() { return 2; }

    @Override
    protected int getMemorySize() { return 10; }

    @Override
    protected int getUnitSize() { return 1; }

    @Test
    public void testMemoryAllocationAndDeallocation() {
        try {
            // Allocate multiple processes
            Process process1 = new Process(1, 0, 0, 3);
            Segment allocated1 = mmu.allocate(process1);
            assertEquals(0, allocated1.getBase());

            Process process2 = new Process(2, 0, 0, 4);
            Segment allocated2 = mmu.allocate(process2);
            assertEquals(3, allocated2.getBase());

            // Allocate another process
            Process process3 = new Process(3, 0, 0, 3);
            Segment allocated3 = mmu.allocate(process3);
            assertEquals(7, allocated3.getBase());

            // Deallocate process 2
            mmu.deallocate(process2.getProcessID());

            // Allocate another process
            Process process4 = new Process(4, 0, 0, 3);
            Segment allocated4 = mmu.allocate(process4);
            assertEquals(3, allocated4.getBase());

            // Deallocate process 1
            mmu.deallocate(process1.getProcessID());

            // Deallocate process 3
            mmu.deallocate(process3.getProcessID());

            // Deallocate process 4
            mmu.deallocate(process4.getProcessID());

            // Allocate another process with a larger size than available memory should fail, next time start the search from last position even if it is inside a hole
            Process process5 = new Process(5, 0, 0, 100);
            assertThrows(Exception.class, () -> mmu.allocate(process5));

            // Allocate another process given that the last position at which the next fit algorithm stopped is in the middle of a hole
            Process process6 = new Process(6, 0, 0, 3);
            Segment allocated6 = mmu.allocate(process6);
            assertEquals(6, allocated6.getBase());

        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

}