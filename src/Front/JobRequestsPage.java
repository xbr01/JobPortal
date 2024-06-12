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

// Fetch and display job requests for a specific job
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
        // Add button to navigate to accepted resumes page
        JButton viewAcceptedResumesButton = new JButton("View Accepted Resumes");
        viewAcceptedResumesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AcceptedResumesPage acceptedResumesPage = new AcceptedResumesPage(jobId);
                acceptedResumesPage.setVisible(true);
            }
        });
        buttonPanel.add(viewAcceptedResumesButton);

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
                addRequestToPanel(employeeName, resumeLink, requestsPanel, jobId);
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

    private void addRequestToPanel(String employeeName, String resumeLink, JPanel panel, int jobId) {
        JPanel requestPanel = new JPanel();
        requestPanel.setLayout(new BorderLayout(5, 5));
        requestPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel nameLabel = new JLabel("Applicant: " + employeeName);
        JLabel resumeLabel = new JLabel("Resume Link: " + resumeLink);
        requestPanel.add(nameLabel, BorderLayout.NORTH);
        requestPanel.add(resumeLabel, BorderLayout.CENTER);

        JButton rejectButton = new JButton("Reject");
        rejectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rejectResume(jobId, employeeName, resumeLink);
                panel.remove(requestPanel); // Remove the request from the panel
                panel.revalidate();
                panel.repaint();
            }
        });

        JButton acceptButton = new JButton("Accept");
        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                acceptResume(jobId, employeeName, resumeLink);
                panel.remove(requestPanel); // Remove the request from the panel
                panel.revalidate();
                panel.repaint();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(rejectButton);
        buttonPanel.add(acceptButton);
        requestPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(requestPanel);
        panel.revalidate();
        panel.repaint();
    }

    private void rejectResume(int jobId, String employeeName, String resumeLink) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM resumes WHERE job_id = ? AND employee_name = ? AND resume_link = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, jobId);
            pstmt.setString(2, employeeName);
            pstmt.setString(3, resumeLink);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(contentPane, "Resume rejected successfully.");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error rejecting resume", ex);
            JOptionPane.showMessageDialog(contentPane, "Error rejecting resume. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error closing connection", ex);
                }
            }
        }
    }

    private void acceptResume(int jobId, String employeeName, String resumeLink) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO accepted_resumes (job_id, employee_name, resume_link) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, jobId);
            pstmt.setString(2, employeeName);
            pstmt.setString(3, resumeLink);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(contentPane, "Resume accepted successfully.");
            // You can navigate to a new page to display accepted resumes here
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error accepting resume", ex);
            JOptionPane.showMessageDialog(contentPane, "Error accepting resume. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error closing connection", ex);
                }
            }
        }
    }
}
