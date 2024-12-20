import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.Color;

public class ProcessTimelineGUI {

    public static void displayTimeline(List<Process> processes) {

        JFrame frame = new JFrame("Process Timelines");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());

        JPanel timelinePanel = new JPanel();
        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(timelinePanel);

        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(BorderFactory.createTitledBorder("Dashboard"));

        for (Process process : processes) {
            Color processColor = process.colour;

            JLabel dashboard = new JLabel("Name: " + process.name + " | Priority: " + process.priority);
            dashboard.setOpaque(true);
            dashboard.setBackground(processColor);
            dashboard.setForeground(Color.WHITE);
            dashboard.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            legendPanel.add(dashboard);

            JPanel processPanel = new JPanel();
            processPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            processPanel.setBorder(BorderFactory.createTitledBorder(process.name));
            processPanel.setPreferredSize(new Dimension(750, 60));

            for (int[] interval : process.timeline) {
                int start = interval[0];
                int end = interval[1];
                int width = Math.max(50, (end - start) * 10);
                
                JPanel intervalPanel = new JPanel();
                intervalPanel.setBackground(processColor); 
                intervalPanel.setPreferredSize(new Dimension(width, 40));
 
                processPanel.add(intervalPanel);
            }
            timelinePanel.add(processPanel);
        }

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(legendPanel, BorderLayout.EAST);
        frame.setVisible(true);
    }
}
