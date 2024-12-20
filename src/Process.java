import java.util.*;
import java.awt.Color;

public class Process {
    String name;
    int arrivalTime, burstTime, priority, remainingTime, quantum, completionTime;
    double FCAIFactor;
    List<int[]> timeline; 
    List<Integer> quantumHistory;
    int waitingTime, turnaroundTime;
    Color colour;

    public Process(String name, int arrivalTime, int burstTime, int priority, int quantum, Color colour) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.quantum = quantum;
        this.colour = colour;
        this.timeline = new ArrayList<>();
        this.quantumHistory = new ArrayList<>();
        this.quantumHistory.add(quantum); 
    }

    public void calculateTurnaroundTime() {
        this.turnaroundTime = completionTime - arrivalTime;
        this.waitingTime = turnaroundTime - burstTime;
    }

    public void calculateFCAIFactor(double V1, double V2) {
        this.FCAIFactor = (10 - priority) + (arrivalTime / V1) + (remainingTime / V2);
    }

    public void addToTimeline(int start, int end) {
        timeline.add(new int[]{start, end});
    }
}
