import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

class FitnessTracker {
    private Map<LocalDate, Integer> stepsByDate;
    private String[] dietPlan;
    public FitnessTracker() {
        this.stepsByDate = new HashMap<>();
        this.dietPlan = new String[]{
                "Calorie Goal: 2000 calories",
                "Breakfast: Cereal and fruit",
                "Lunch: Grilled chicken salad",
                "Dinner: Salmon with veggies",
                "Snacks: Nuts and yogurt"
        };
    }
    public void addSteps(int steps, LocalDate date) {
        stepsByDate.put(date, steps);
    }
    public Map<LocalDate, Integer> getStepsByDate() {
        return stepsByDate;
    }
    public String[] getDietPlan() {
        return dietPlan;
    }
}
public class FT1 extends JFrame {
    private FitnessTracker tracker;
    private JButton addStepsButton;
    private JButton displayStatsButton;
    private JButton displayDietButton;
    private JButton exitButton;
    private JButton showDateButton;
    private JButton showGraphButton;
    private JTextArea outputArea;
    private JTextField stepsInput;
    private JTextField dateInput;
    private GraphPanel graphPanel;
    public FT1() {
        super("Fitness Tracker App");
        tracker = new FitnessTracker();
        addStepsButton = new JButton("Add Steps");
        displayStatsButton = new JButton("Display Fitness Stats");
        displayDietButton = new JButton("Display Diet Plan");
        exitButton = new JButton("Exit");
        showDateButton = new JButton("Show Date");
        showGraphButton = new JButton("Show Graph");
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Arial", Font.PLAIN, 20));
        stepsInput = new JTextField(10);
        dateInput = new JTextField(10);
        graphPanel = new GraphPanel();
        addStepsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Enter steps walked:"));
                panel.add(stepsInput);
                panel.add(new JLabel("Enter date (YYYY-MM-DD):"));
                panel.add(dateInput);
                int result = JOptionPane.showConfirmDialog(null, panel, "Enter Information",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int steps = Integer.parseInt(stepsInput.getText());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate date = LocalDate.parse(dateInput.getText(), formatter);
                        tracker.addSteps(steps, date);
                        outputArea.append("Steps added: " + steps + " on " + date + "\n");
                        graphPanel.setStepsByDate(tracker.getStepsByDate());
                        graphPanel.repaint();
                    } catch (NumberFormatException | DateTimeParseException ex) {
                        JOptionPane.showMessageDialog(null, "Please enter valid input.");
                    }
                }
            }
        });
        displayStatsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                outputArea.append("Fitness Tracker Status:\n");
                Map<LocalDate, Integer> stepsByDate = tracker.getStepsByDate();
                for (Map.Entry<LocalDate, Integer> entry : stepsByDate.entrySet()) {
                    outputArea.append(entry.getKey() + ": " + entry.getValue() + " steps\n");
                }
            }
        });
        displayDietButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                outputArea.append("Diet Plan:\n");
                String[] dietPlan = tracker.getDietPlan();
                for (String plan : dietPlan) {
                    outputArea.append(plan + "\n");
                }
            }
        });
        showDateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LocalDate currentDate = LocalDate.now();
                outputArea.append("Current Date: " + currentDate + "\n");
            }
        });
        showGraphButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame graphFrame = new JFrame("Fitness Tracker Graph");
                graphFrame.setSize(400, 300);
                graphFrame.add(graphPanel);
                graphFrame.setLocationRelativeTo(null);
                graphFrame.setVisible(true);
            }
        });
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Terminate the application
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 1));
        buttonPanel.add(addStepsButton);
        buttonPanel.add(displayStatsButton);
        buttonPanel.add(displayDietButton);
        buttonPanel.add(showDateButton);
        buttonPanel.add(showGraphButton);
        buttonPanel.add(exitButton);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(graphPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    class GraphPanel extends JPanel {
        private Map<LocalDate, Integer> stepsByDate;
        public void setStepsByDate(Map<LocalDate, Integer> stepsByDate) {
            this.stepsByDate = stepsByDate;
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (stepsByDate != null && !stepsByDate.isEmpty()) {
                g.setColor(Color.BLUE);
                int width = getWidth();
                int height = getHeight();
                int maxY = stepsByDate.values().stream().max(Integer::compareTo).orElse(0);
                int maxX = stepsByDate.size();
                int x = 50;
                g.drawLine(50, 50, 50, height - 50); // Y-axis line
                g.drawLine(50, height - 50, width - 50, height - 50); // X-axis line
                int prevX = 0, prevY = 0;
                for (LocalDate date : stepsByDate.keySet()) {
                    int steps = stepsByDate.get(date);
                    int scaledY = ((steps * (height - 100)) / maxY) + 50;
                    int scaledX = x + (width - 100) / maxX;
                    g.fillOval(scaledX - 2, height - scaledY - 2, 4, 4); //Data points as dots
                    if (prevX != 0) {
                        g.drawLine(prevX, prevY, scaledX, height - scaledY); //Line connecting dots
                    }
                    g.drawString(date.toString(), scaledX - 10, height - 30); //Date labels on x-axis
                    g.drawString(String.valueOf(steps), 20, height - scaledY); //Steps labels on y-axis
                    prevX = scaledX;
                    prevY = height - scaledY;
                    x += (width - 100) / maxX;
                }
            }

            
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FT1());
    }
}
