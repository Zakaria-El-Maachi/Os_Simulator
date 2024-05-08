package com.os.mmu.strategies;

public class StrategyFactory {
    // Private static instance of the factory
    private static StrategyFactory instance;

    // Private constructor to prevent instantiation from other classes
    private StrategyFactory() {
    }

    // Public static method to provide access to the singleton instance
    public static StrategyFactory getInstance() {
        // Create the instance if it doesn't exist yet
        if (instance == null) {
            instance = new StrategyFactory();
        }
        // Return the existing instance
        return instance;
    }

    // Method to create a memory management strategy based on the input integer
    public MemoryManagementStrategy createStrategy(int strategyType) {
        return switch (strategyType) {
            case 1 ->
                // Create and return an instance of the FirstFit strategy
                    new FirstFit();
            case 2 ->
                // Create and return an instance of the NextFit strategy
                    new NextFit();
            case 3 ->
                // Create and return an instance of the BestFit strategy
                    new BestFit();
            case 4 ->
                // Create and return an instance of the WorstFit strategy
                    new WorstFit();
            default -> throw new IllegalArgumentException("Invalid strategy type. Valid values are 1, 2, 3, or 4.");
        };
    }
}

