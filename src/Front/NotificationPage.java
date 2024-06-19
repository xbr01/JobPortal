package Front;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationPage extends JFrame {

    private JPanel contentPane;
    private JPanel notificationsPanel;
    private String employeeName;

    public NotificationPage(String employeeName) {
        this.employeeName = employeeName;
        setTitle("Notifications");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 10));

        notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
        notificationsPanel.setBorder(BorderFactory.createTitledBorder("Accepted Resumes"));
        notificationsPanel.setBackground(new Color(245, 245, 245));
        contentPane.add(new JScrollPane(notificationsPanel), BorderLayout.CENTER);

        fetchAndDisplayAcceptedResumes(employeeName);

        // Add Back button
        JButton backButton = new JButton("Back to Employee Page");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigateToEmployeePage();
            }
        });
        contentPane.add(backButton, BorderLayout.NORTH);
    }

    private void fetchAndDisplayAcceptedResumes(String employeeName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            System.out.println(employeeName);
            String sql = "SELECT job_id, employee_name, resume_link FROM accepted_resumes WHERE employee_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employeeName);
            rs = pstmt.executeQuery();
            if (!rs.next()) {
                System.out.println("No results found for employee: " + employeeName);
            } else {
                do {
                    int jobId = rs.getInt("job_id");
                    String employeeName1 = rs.getString("employee_name");
                    String resumeLink = rs.getString("resume_link");
                    System.out.println("Fetched Employee Name: " + employeeName1);
                    System.out.println("Fetched Resume Link: " + resumeLink);
                    addNotificationToPanel(jobId, employeeName1, resumeLink);
                } while (rs.next());
                }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void addNotificationToPanel(int jobId, String employeeName, String resumeLink) {
        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new BorderLayout(5, 5));
        notificationPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel jobIdLabel = new JLabel("Job ID: " + jobId);
        JLabel employeeNameLabel = new JLabel("Employee Name: " + employeeName);
        JLabel resumeLinkLabel = new JLabel("Resume Link: " + resumeLink);

        infoPanel.add(jobIdLabel);
        infoPanel.add(employeeNameLabel);
        infoPanel.add(resumeLinkLabel);

        notificationPanel.add(infoPanel, BorderLayout.CENTER);
        notificationsPanel.add(notificationPanel);
        notificationsPanel.revalidate();
        notificationsPanel.repaint();
    }

    private void navigateToEmployeePage() {
        // Assuming you have a reference to the EmployeePage instance
        EmployeePage employeePage = new EmployeePage(null, 0); // Replace with actual parameters if needed
        employeePage.setVisible(true);
        this.dispose();
    }
}
