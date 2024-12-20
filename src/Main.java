import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the number of processes: ");
        int numProcesses = scanner.nextInt();

        System.out.println("Enter the context switching time: ");
        int contextSwitching = scanner.nextInt();

        List<Process> originalProcesses = new ArrayList<>();

        // Input processes
        for (int i = 0; i < numProcesses; i++) {
            System.out.println("Enter details for Process " + (i + 1) + ":");
            System.out.print("Process Name: ");
            String name = scanner.next();
            System.out.print("Process Colour: ");
            String colour = scanner.next().toLowerCase();
            Color color = getRealColour(colour);
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();
            System.out.print("Priority: ");
            int priority = scanner.nextInt();
            System.out.print("Initial Quantum: ");
            int quantum = scanner.nextInt();

            originalProcesses.add(new Process(name, arrivalTime, burstTime, priority, quantum, color));
        }

        // Execute Scheduling Algorithms and visualize
        System.out.println("\nExecuting Non-Preemptive Priority Scheduling...");
        List<Process> priorityProcesses = deepCopyProcesses(originalProcesses);
        Scheduler.nonPreemptivePriorityScheduling(priorityProcesses);
        ProcessTimelineGUI.displayTimeline(priorityProcesses);

        System.out.println("\nExecuting Non-Preemptive SJF...");
        List<Process> sjfProcesses = deepCopyProcesses(originalProcesses);
        Scheduler.nonPreemptiveSJF(sjfProcesses);
        ProcessTimelineGUI.displayTimeline(sjfProcesses);

        System.out.println("\nExecuting SRTF...");
        List<Process> srtfProcesses = deepCopyProcesses(originalProcesses);
        Scheduler.SRTF(srtfProcesses);
        ProcessTimelineGUI.displayTimeline(srtfProcesses);

        System.out.println("\nExecuting FCAI Scheduling...");
        List<Process> fcaiProcesses = deepCopyProcesses(originalProcesses);
        Scheduler.fcaiScheduling(fcaiProcesses, contextSwitching);
        ProcessTimelineGUI.displayTimeline(fcaiProcesses);

        scanner.close();
    }

    // Helper method to deep copy processes
    private static List<Process> deepCopyProcesses(List<Process> processes) {
        List<Process> copy = new ArrayList<>();
        for (Process p : processes) {
            copy.add(new Process(p.name, p.arrivalTime, p.burstTime, p.priority, p.quantum, p.colour));
        }
        return copy;
    }

    private static Color getRealColour(String colorString) {
        try {
            return (Color) Color.class.getField(colorString).get(null);
        } catch (Exception e) {
            return null;
        }
    }

}
