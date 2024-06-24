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
            String sql = "SELECT p.name, p.email, p.skills, p.work_experience, p.education, r.resume_link, r.employee_id " +
                         "FROM resumes r " +
                         "JOIN profile p ON r.employee_id = p.user_id " +
                         "WHERE r.job_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String employeeName = rs.getString("name");
                String email = rs.getString("email");
                String skills = rs.getString("skills");
                String workExperience = rs.getString("work_experience");
                String education = rs.getString("education");
                String resumeLink = rs.getString("resume_link");
                int employeeId = rs.getInt("employee_id");
                addRequestToPanel(employeeName, email, skills, workExperience, education, resumeLink, employeeId, requestsPanel, jobId);
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

    private void addRequestToPanel(String employeeName, String email, String skills, String workExperience, String education, String resumeLink, int employeeId, JPanel panel, int jobId) {
        JPanel requestPanel = new JPanel();
        requestPanel.setLayout(new BorderLayout(5, 5));
        requestPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel nameLabel = new JLabel("Name: " + employeeName);
        JLabel emailLabel = new JLabel("Email: " + email);
        JLabel skillsLabel = new JLabel("Skills: " + skills);
        JLabel workExperienceLabel = new JLabel("Work Experience: " + workExperience);
        JLabel educationLabel = new JLabel("Education: " + education);
        JLabel resumeLabel = new JLabel("Resume Link: " + resumeLink);
        
        infoPanel.add(nameLabel);
        infoPanel.add(emailLabel);
        infoPanel.add(skillsLabel);
        infoPanel.add(workExperienceLabel);
        infoPanel.add(educationLabel);
        infoPanel.add(resumeLabel);
        
        requestPanel.add(infoPanel, BorderLayout.CENTER);

        JButton rejectButton = new JButton("Reject");
        rejectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rejectResume(jobId, employeeId);
                panel.remove(requestPanel); // Remove the request from the panel
                panel.revalidate();
                panel.repaint();
            }
        });

        JButton acceptButton = new JButton("Accept");
        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                acceptResume(jobId, employeeId, employeeName, resumeLink);
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

    private void rejectResume(int jobId, int employeeId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM resumes WHERE job_id = ? AND employee_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, jobId);
            pstmt.setInt(2, employeeId);
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

    private void acceptResume(int jobId, int employeeId, String employeeName, String resumeLink) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
    
            // Insert into accepted_resumes
            String insertSql = "INSERT INTO accepted_resumes (job_id, employee_name, resume_link) VALUES (?, ?, ?)";
            try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                insertPstmt.setInt(1, jobId);
                insertPstmt.setString(2, employeeName);
                insertPstmt.setString(3, resumeLink);
                LOGGER.info("Inserting into accepted_resumes: job_id=" + jobId + ", employee_name=" + employeeName + ", resume_link=" + resumeLink);
                insertPstmt.executeUpdate();
            }
    
            // Delete from resumes
            // String deleteSql = "DELETE FROM resumes WHERE job_id = ? AND employee_id = ?";
            // try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
            //     deletePstmt.setInt(1, jobId);
            //     deletePstmt.setInt(2, employeeId);
            //     LOGGER.info("Deleting from resumes: job_id=" + jobId + ", employee_id=" + employeeId);
            //     deletePstmt.executeUpdate();
            // }
    
            conn.commit(); // Commit transaction
            JOptionPane.showMessageDialog(contentPane, "Resume accepted successfully.");
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction", rollbackEx);
                }
            }
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
