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
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobRequestsPage extends JFrame {

    private JPanel contentPane;
    private JPanel requestsPanel;
    private static final Logger LOGGER = Logger.getLogger(JobRequestsPage.class.getName());

    public JobRequestsPage(int jobId, String jobTitle, JFrame employerPage) {
        setTitle("Job Requests for " + jobTitle);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 10));

        requestsPanel = new JPanel();
        requestsPanel.setLayout(new BoxLayout(requestsPanel, BoxLayout.Y_AXIS));
        requestsPanel.setBorder(BorderFactory.createTitledBorder("Requests"));
        requestsPanel.setBackground(new Color(245, 245, 245));
        contentPane.add(new JScrollPane(requestsPanel), BorderLayout.CENTER);

        LOGGER.info("Fetching and displaying requests for job ID: " + jobId);
        fetchAndDisplayRequests(jobId); // Fetch and display job requests

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                employerPage.setVisible(true);
                dispose();
            }
        });
        buttonPanel.add(backButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void fetchAndDisplayRequests(int jobId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT employee_name, resume_link " +
                         "FROM resumes " +
                         "WHERE job_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String employeeName = rs.getString("employee_name");
                String resumeLink = rs.getString("resume_link");
                addRequestToPanel(employeeName, resumeLink, requestsPanel);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching job requests for job ID: " + jobId, e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection for job ID: " + jobId, e);
                }
            }
        }
    }

    private void addRequestToPanel(String employeeName, String resumeLink, JPanel panel) {
        JPanel requestPanel = new JPanel();
        requestPanel.setLayout(new BorderLayout(5, 5));
        requestPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel nameLabel = new JLabel("Applicant: " + employeeName);
        JLabel resumeLabel = new JLabel("Resume Link: " + resumeLink);
        requestPanel.add(nameLabel, BorderLayout.NORTH);
        requestPanel.add(resumeLabel, BorderLayout.CENTER);
        panel.add(requestPanel);
        panel.revalidate();
        panel.repaint();
    }
}
