package com.os.mmu;

import com.os.Process;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThrows;

public class FirstFitTest extends MemoryManagementUnitTestBase {
    @Override
    protected int getAlgorithm() { return 1; }

    @Override
    protected int getMemorySize() { return 1024; }

    @Override
    protected int getUnitSize() { return 64; }

    @Test
    public void testMemoryAllocationAndDeallocation() {
        try {
            // Allocate multiple processes
            Process process1 = new Process(1, 0, 0, 100);
            Segment allocated1 = mmu.allocate(process1);
            assertEquals(0, allocated1.getBase());

            Process process2 = new Process(2, 0, 0, 200);
            Segment allocated2 = mmu.allocate(process2);
            assertEquals(2, allocated2.getBase());

            // Allocate another process
            Process process3 = new Process(3, 0, 0, 640);
            Segment allocated3 = mmu.allocate(process3);
            assertEquals(6, allocated3.getBase());

            // Memory is full at this point, trying to allocate any process should fail
            Process process4 = new Process(4, 0, 0, 1);
            assertThrows(Exception.class, () -> mmu.allocate(process4));

            // Deallocate process 1
            mmu.deallocate(process1.getProcessID());

            // Allocate another process with a larger size than available memory should fail
            Process process5 = new Process(5, 0, 0, 130);
            assertThrows(Exception.class, () -> mmu.allocate(process5));

            // Deallocate process 3
            mmu.deallocate(process3.getProcessID());

            // Allocate another process
            Process process6 = new Process(6, 0, 0, 50);
            Segment allocated6 = mmu.allocate(process6);
            // Check if process 6 is allocated in the same location as process 1 was deallocated
            assertEquals(allocated1.getBase(), allocated6.getBase());

            // Allocate another process
            Process process7 = new Process(7, 0, 0, 70);
            Segment allocated7 = mmu.allocate(process7);
            // Check if process 7 is allocated in the same location as process 3 was deallocated
            assertEquals(allocated3.getBase(), allocated7.getBase());

        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testAddressConversion() {
        try {
            Process process1 = new Process(1, 0, 0, 100);
            Segment allocated1 = mmu.allocate(process1);
            Process process2 = new Process(2, 0, 0, 100);
            Segment allocated2 = mmu.allocate(process2);

            /* Process 1 */
            // Calculate virtual and physical addresses
            long virtualAddress1 = 50;
            long expectedPhysicalAddress1 = 50;
            // Convert virtual address to physical address
            long physicalAddress1 = mmu.convert(process1.getProcessID(), virtualAddress1);
            // Verify the converted physical address
            assertEquals(expectedPhysicalAddress1, physicalAddress1);

            /* Process 2 */
            // Calculate virtual and physical addresses
            long virtualAddress2 = 50;
            long expectedPhysicalAddress2 = 178;
            // Convert virtual address to physical address
            long physicalAddress2 = mmu.convert(process2.getProcessID(), virtualAddress2);
            // Verify the converted physical address
            assertEquals(expectedPhysicalAddress2, physicalAddress2);

        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

}
