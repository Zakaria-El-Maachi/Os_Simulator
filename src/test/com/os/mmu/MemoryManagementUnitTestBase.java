package com.os.mmu;

import com.os.mmu.strategies.MemoryManagementStrategy;
import com.os.mmu.strategies.StrategyFactory;
import org.junit.Before;

public abstract class MemoryManagementUnitTestBase {
    protected MemoryManagementUnit mmu;

    protected abstract int getAlgorithm();
    protected abstract int getMemorySize();
    protected abstract int getUnitSize();

    @Before
    public void setUp() {
        // Initialize MMU with test parameters specific to each algorithm
        int memorySize = getMemorySize();
        int unitSize = getUnitSize();
        this.mmu = new MemoryManagementUnit(memorySize, unitSize);
        StrategyFactory strategyFactory = StrategyFactory.getInstance();
        MemoryManagementStrategy strategy = strategyFactory.createStrategy(getAlgorithm());
        this.mmu.setImplementation(strategy);
    }

}