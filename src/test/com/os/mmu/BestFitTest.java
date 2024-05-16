package com.os.mmu;

import com.os.Process;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThrows;

public class BestFitTest extends MemoryManagementUnitTestBase {
    @Override
    protected int getAlgorithm() { return 3; }

    @Override
    protected int getMemorySize() { return 1024; }

    @Override
    protected int getUnitSize() { return 64; }

    @Test
    public void testMemoryAllocationAndDeallocation() {
        try {
            // Allocate multiple processes
            Process process1 = new Process(1, 0, 0, 128);
            Segment allocated1 = mmu.allocate(process1);
            assertEquals(0, allocated1.getBase());

            Process process2 = new Process(2, 0, 0, 256);
            Segment allocated2 = mmu.allocate(process2);
            assertEquals(2, allocated2.getBase());

            // Allocate another process
            Process process3 = new Process(3, 0, 0, 512);
            Segment allocated3 = mmu.allocate(process3);
            assertEquals(6, allocated3.getBase());

            // Deallocate process 2
            mmu.deallocate(process2.getProcessID());

            // Allocate another process
            Process process4 = new Process(4, 0, 0, 192);
            Segment allocated4 = mmu.allocate(process4);
            assertEquals(2, allocated4.getBase());

            // Allocate another process with a larger size than available memory should fail
            Process process5 = new Process(5, 0, 0, 1000);
            assertThrows(Exception.class, () -> mmu.allocate(process5));

            // Deallocate process 1
            mmu.deallocate(process1.getProcessID());

            // Allocate another process
            Process process6 = new Process(6, 0, 0, 50);
            Segment allocated6 = mmu.allocate(process6);
            assertEquals(5, allocated6.getBase());

        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

}