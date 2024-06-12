package Front;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppliedJobsPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel appliedJobsPanel;
    private EmployeePage employeePage;

    public AppliedJobsPage(EmployeePage employeePage) {
        this.employeePage = employeePage;
        setTitle("Applied Jobs Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 10));

        appliedJobsPanel = new JPanel();
        appliedJobsPanel.setLayout(new BoxLayout(appliedJobsPanel, BoxLayout.Y_AXIS));
        appliedJobsPanel.setBorder(BorderFactory.createTitledBorder("Applied Jobs"));
        appliedJobsPanel.setBackground(new Color(245, 245, 245));
        contentPane.add(new JScrollPane(appliedJobsPanel), BorderLayout.CENTER);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back to Employee Page");
        backButton.setBackground(new Color(0, 122, 255));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigateToEmployeePage();
            }
        });
        backPanel.add(backButton);
        contentPane.add(backPanel, BorderLayout.NORTH);
    }

    public void addAppliedJob(String jobTitle) {
        JPanel jobPanel = new JPanel();
        jobPanel.setLayout(new BorderLayout(5, 5));
        jobPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel jobLabel = new JLabel(jobTitle);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(255, 59, 48));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                appliedJobsPanel.remove(jobPanel);
                appliedJobsPanel.revalidate();
                appliedJobsPanel.repaint();
            }
        });
        jobPanel.add(jobLabel, BorderLayout.CENTER);
        jobPanel.add(deleteButton, BorderLayout.EAST);
        appliedJobsPanel.add(jobPanel);
        appliedJobsPanel.revalidate();
        appliedJobsPanel.repaint();
    }

    private void navigateToEmployeePage() {
        employeePage.setVisible(true);
        this.dispose();
    }
}
