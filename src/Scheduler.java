import java.util.*;

public class Scheduler {
    public static void nonPreemptivePriorityScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.priority));
        int time = 0;
        double totalWaitTime = 0, totalTurnaroundTime = 0;

        StringBuilder timeline = new StringBuilder();

        System.out.println("\nNon-Preemptive Priority Scheduling:");
        for (Process p : processes) {
            if (p.arrivalTime > time) time = p.arrivalTime;

            int start = time;
            p.completionTime = time + p.burstTime;
            p.calculateTurnaroundTime();
            totalWaitTime += p.waitingTime;
            totalTurnaroundTime += p.turnaroundTime;

            timeline.append(p.name).append(" {(").append(start).append(",").append(p.completionTime).append(")} ");

            time = p.completionTime;

            System.out.println(p.name + " | Arrival Time: " + p.arrivalTime + " | Burst Time: " + p.burstTime +
                    " | Waiting Time: " + p.waitingTime + " | Turnaround Time: " + p.turnaroundTime);
        }
        System.out.println("Timelines:\n" + timeline);
        System.out.println("Average Waiting Time: " + (totalWaitTime / processes.size()));
        System.out.println("Average Turnaround Time: " + (totalTurnaroundTime / processes.size()));
    }

    public static void nonPreemptiveSJF(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int time = 0;
        double totalWaitTime = 0, totalTurnaroundTime = 0;
        List<Process> readyQueue = new ArrayList<>();

        StringBuilder timeline = new StringBuilder();

        System.out.println("\nNon-Preemptive Shortest Job First (SJF):");
        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            for (Iterator<Process> iterator = processes.iterator(); iterator.hasNext(); ) {
                Process p = iterator.next();
                if (p.arrivalTime <= time) {
                    readyQueue.add(p);
                    iterator.remove();
                }
            }

            if (!readyQueue.isEmpty()) {
                readyQueue.sort(Comparator.comparingInt(p -> p.burstTime));
                Process current = readyQueue.remove(0);

                int start = time;
                time += current.burstTime;
                current.completionTime = time;
                current.calculateTurnaroundTime();

                totalWaitTime += current.waitingTime;
                totalTurnaroundTime += current.turnaroundTime;

                timeline.append(current.name).append(" {(").append(start).append(",").append(time).append(")} ");

                System.out.println(current.name + " | Arrival Time: " + current.arrivalTime + " | Burst Time: " + current.burstTime +
                        " | Waiting Time: " + current.waitingTime + " | Turnaround Time: " + current.turnaroundTime);
            } else {
                time++;
            }
        }

        System.out.println("Timelines:\n" + timeline);
        System.out.println("Average Waiting Time: " + (totalWaitTime / 4)); // Ensure correct size
        System.out.println("Average Turnaround Time: " + (totalTurnaroundTime / 4));
    }

    public static void SRTF(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int time = 0, completed = 0;
        double totalWaitTime = 0, totalTurnaroundTime = 0;

        Map<String, List<int[]>> timelineMap = new LinkedHashMap<>();
        for (Process p : processes) {
            timelineMap.put(p.name, new ArrayList<>());
        }

        System.out.println("\nShortest Remaining Time First (SRTF) Scheduling:");
        while (completed < processes.size()) {
            Process current = null;

            for (Process p : processes) {
                if (p.arrivalTime <= time && p.remainingTime > 0) {
                    if (current == null || p.remainingTime < current.remainingTime) {
                        current = p;
                    }
                }
            }

            if (current != null) {
                if (timelineMap.get(current.name).isEmpty() ||
                        timelineMap.get(current.name).get(timelineMap.get(current.name).size() - 1)[1] != time) {
                    timelineMap.get(current.name).add(new int[]{time, time + 1});
                } else {
                    timelineMap.get(current.name).get(timelineMap.get(current.name).size() - 1)[1]++;
                }

                current.remainingTime--;
                time++;

                if (current.remainingTime == 0) {
                    completed++;
                    current.completionTime = time;
                    current.calculateTurnaroundTime();

                    totalWaitTime += current.waitingTime;
                    totalTurnaroundTime += current.turnaroundTime;

                    System.out.println(current.name + " | Arrival Time: " + current.arrivalTime + " | Burst Time: " +
                            current.burstTime + " | Waiting Time: " + current.waitingTime + " | Turnaround Time: " + current.turnaroundTime);
                }
            } else {
                time++;
            }
        }

        System.out.println("Timelines:");
        for (Map.Entry<String, List<int[]>> entry : timelineMap.entrySet()) {
            String process = entry.getKey();
            StringBuilder intervals = new StringBuilder();
            for (int[] interval : entry.getValue()) {
                intervals.append("(").append(interval[0]).append(",").append(interval[1]).append(") ");
            }
            System.out.println(process + " {" + intervals.toString().trim() + "}");
        }

        System.out.println("Average Waiting Time: " + (totalWaitTime / processes.size()));
        System.out.println("Average Turnaround Time: " + (totalTurnaroundTime / processes.size()));
    }

    public static void fcaiScheduling(List<Process> processes, int contextSwitchTime) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int time = 0;
        double totalWaitTime = 0, totalTurnaroundTime = 0;

        PriorityQueue<Process> readyQueue = new PriorityQueue<>((p1, p2) -> Double.compare(p1.FCAIFactor, p2.FCAIFactor));

        int lastArrivalTime = processes.stream().mapToInt(p -> p.arrivalTime).max().orElse(0);
        int maxBurstTime = processes.stream().mapToInt(p -> p.burstTime).max().orElse(0);
        double V1 = lastArrivalTime / 10.0;
        double V2 = maxBurstTime / 10.0;

        processes.forEach(p -> p.calculateFCAIFactor(V1, V2));

        List<String> executionOrder = new ArrayList<>();

        System.out.println("\nFCAI Scheduling:");

        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            Iterator<Process> it = processes.iterator();
            while (it.hasNext()) {
                Process p = it.next();
                if (p.arrivalTime <= time) {
                    readyQueue.add(p);
                    it.remove();
                }
            }

            if (readyQueue.isEmpty()) {
                time++;
                continue;
            }

            Process current = readyQueue.poll();
            executionOrder.add(current.name);

            int nonPreemptiveTime = (int) Math.ceil(0.4 * current.quantum);
            int executionTime = Math.min(nonPreemptiveTime, current.remainingTime);

            int start = time;
            time += executionTime;
            current.remainingTime -= executionTime;
            current.addToTimeline(start, time);

            if (current.remainingTime > 0) {
                if (executionTime < nonPreemptiveTime) {
                    current.quantum += (nonPreemptiveTime - executionTime); 
                } else {
                    current.quantum += 2; 
                }
                current.quantumHistory.add(current.quantum);
                current.calculateFCAIFactor(V1, V2);
                readyQueue.add(current);
            } else {
                current.completionTime = time;
            }

            time += contextSwitchTime;
        }

        for (Process p : processes) {
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            totalWaitTime += p.waitingTime;
            totalTurnaroundTime += p.turnaroundTime;
        }

        System.out.println("Execution Order: " + String.join(" -> ", executionOrder));

        System.out.println("\nQuantum Time History for each process:");
        for (Process p : processes) {
            System.out.println(p.name + ": " + p.quantumHistory);
        }

        System.out.println("\nWaiting Time and Turnaround Time for each process:");
        for (Process p : processes) {
            System.out.printf("%s: Waiting Time = %d, Turnaround Time = %d\n", p.name, p.waitingTime, p.turnaroundTime);
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", totalWaitTime / processes.size());
        System.out.printf("Average Turnaround Time: %.2f\n", totalTurnaroundTime / processes.size());
    }

}
