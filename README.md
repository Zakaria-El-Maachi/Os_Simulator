# CPU Scheduler Simulator

## Table of Contents

- [Project Description](#project-description)
- [Modeling / System Design](#modeling--system-design)
    - [Design Patterns](#design-patterns)
    - [UML](#uml)
- [User Interface](#user-interface)
    - [Overview](#overview)
    - [Visualizations](#visualizations)
    - [Additional Metrics](#additional-metrics)
- [Testing and Analysis](#testing-and-analysis)
- [How to run](#how-to-run)

## Project Description

This project aims to create a CPU scheduler simulator implementing various scheduling algorithms commonly used in operating systems. The implemented algorithms are:

- First-Come, First-Served (FCFS)
- Shortest Job First (SJF)
- Round Robin (RR)
- Priority Scheduling (Non-preemptive)
- Priority Scheduling (Preemptive) using:
    - First-Come-First-Served (FCFS)
    - Round Robin (RR)
- Shortest Remaining Time First (SRTF)
- Highest Response Ratio Next (HRRN)
- Lottery Scheduling



## Modeling / System Design

### Design Patterns

- **Strategy Pattern**: Allows dynamic switching of scheduling algorithms.
- **Observer Pattern**: Enables objects to subscribe to changes.
- **Factory Pattern**: Centralizes process creation.
- **State Pattern**: Models process states.
- **Template Pattern**: Streamlines algorithmic design.

### UML
![UML](./images/uml.png)


## User Interface

### Overview

A user-friendly GUI enhances the simulation experience, allowing intuitive navigation and parameter manipulation.

### Visualizations

- **Table View**: Detailed scheduling information for each process.
- **Gantt Chart**: Visualization of process execution order.
- **Charts View**: Additional graphical representations.
- **Video Simulation**: Interactive animation for real-time visualization.

### Additional Metrics

- **Average Turnaround Time**
- **Average Waiting Time**
- **Throughput**: Processes completed per unit time.
- **CPU Utilization**: Percentage of time the CPU is busy processing tasks.
- **Context Switches**: Number of times the CPU switches between processes.
- **Waiting Time Variance**: Variance in waiting times among processes.
- **Maximum Waiting Time**: Longest time a process spends waiting in the ready queue.


## Testing and Analysis

Unit tests have been conducted to evaluate the performance of each scheduling algorithm based on a predefined set of processes. These tests analyze the effectiveness of each algorithm individually.


## How to Run

Before running the CPU Scheduler Simulator, ensure you have JDK (Java Development Kit) version 21 or higher installed on your system.

1. Navigate to the root directory of the project.
2. Run the following command to build the project using Maven:

    ```
    mvn clean install
    ```

3. Once the build process is complete, navigate to the `target` directory.
4. Locate the JAR file named `CPUScheduler-1.0-SNAPSHOT-shaded.jar`.
5. Execute the JAR file by running the following command:

    ```
    java -jar CPUScheduler-1.0-SNAPSHOT-shaded.jar
    ```

6. The CPU Scheduler Simulator will start, and you can begin exploring the various scheduling algorithms and features offered by the application.

Ensure that your system meets the necessary requirements and follow these steps precisely to run the simulator successfully.



