package com.os.mmu;

import java.util.Scanner;
import com.os.Process;
import com.os.mmu.strategies.MemoryManagementStrategy;
import com.os.mmu.strategies.StrategyFactory;

public class REPL {
    private final MemoryManager memoryManager;
    private final Scanner scanner = new Scanner(System.in);
    private final MemoryProcessFactory processFactory = new SimpleProcessFactory();

    public REPL(long size, long unit, int strategyType) {
        this.memoryManager = new MemoryManagementUnit(size, unit);
        StrategyFactory strategyFactory = StrategyFactory.getInstance();
        MemoryManagementStrategy strategy = strategyFactory.createStrategy(strategyType);
        this.memoryManager.setImplementation(strategy);
    }

    public void run() {
        System.out.println("Memory Management Unit REPL");
        System.out.println("Type 'help' for a list of available commands.");
        System.out.println("Enter 'exit' to quit.");

        while (true) {
            // Read the command from the user
            System.out.print("> ");
            String command = scanner.nextLine();

            // Check for exit command
            if (command.trim().equalsIgnoreCase("exit")) {
                System.out.println("Exiting Program.");
                break;
            }

            // Parse and execute the command
            try {
                parseAndExecuteCommand(command);
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                System.out.println("Invalid command or arguments. Please try again.");
            }
        }

        // Close the scanner when done
        scanner.close();
    }

    private void parseAndExecuteCommand(String command) {
        // Split the command and its arguments
        String[] parts = command.split("\\s+");
        String cmdType = parts[0].toLowerCase();
        try {

            switch (cmdType) {
                case "cr":
                    // Create process and allocate memory
                    long requestedMemory = Long.parseLong(parts[1]);
                    Process process = processFactory.createProcess(requestedMemory);

                    // Use memoryManager to allocate the process
                    Segment allocated = memoryManager.allocate(process);

                    // Output the process ID and allocation details
                    System.out.printf("Process ID: %d, Base: %d, Limit: %d KB\n",
                            process.getProcessID(),
                            allocated.getBase() * memoryManager.getUnit(),
                            allocated.getLimit() * memoryManager.getUnit());
                    break;

                case "dl":
                    // Delete the process with the given ID
                    int processIDToDelete = Integer.parseInt(parts[1]);
                    memoryManager.deallocate(processIDToDelete);

                    System.out.println("Process " + processIDToDelete + " deallocated.");

                    break;

                case "cv":
                    // Convert virtual address to physical address
                    int processID = Integer.parseInt(parts[1]);
                    long virtualAddress = Long.parseLong(parts[2]);

                    long physicalAddress = memoryManager.convert(processID, virtualAddress);

                    System.out.println("Physical address: " + physicalAddress);

                    break;

                case "pm":
                    // Print memory map (the implementation depends on MemoryManager Overriden toString)
                    System.out.println(memoryManager);
                    break;

                case "help":
                    // Display help message
                    displayHelp();
                    break;

                default:
                    System.out.println("Unknown command. Please try again.");
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("> cr AMOUNT_OF_REQUESTED_MEMORY");
        System.out.println("  Create a process and allocate the requested amount of memory (in KB).");
        System.out.println("  Returns the process ID, base, and limit of allocated memory.");
        System.out.println("> dl PROCESS_ID");
        System.out.println("  Delete the specified process and free its allocated memory.");
        System.out.println("> cv PROCESS_ID VIRTUAL_ADDRESS");
        System.out.println("  Convert the specified virtual address to the physical address for the process.");
        System.out.println("> pm");
        System.out.println("  Print the memory map showing what memory is allocated and to which process.");
        System.out.println("Type 'exit' to quit.");
    }
}
