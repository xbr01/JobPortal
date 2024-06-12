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

public class AcceptedResumesPage extends JFrame {
    private JPanel contentPane;
    private JPanel resumesPanel;
    private int jobId; // Variable to store the jobId

    public AcceptedResumesPage(int jobId) {
        this.jobId = jobId; // Store the jobId
        setTitle("Accepted Resumes for Job ID: " + jobId);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 10));

        resumesPanel = new JPanel();
        resumesPanel.setLayout(new BoxLayout(resumesPanel, BoxLayout.Y_AXIS));
        resumesPanel.setBorder(BorderFactory.createTitledBorder("Accepted Resumes"));
        resumesPanel.setBackground(new Color(245, 245, 245));
        contentPane.add(new JScrollPane(resumesPanel), BorderLayout.CENTER);

        //LOGGER.info("Fetching and displaying accepted resumes for job ID: " + jobId);
        fetchAndDisplayAcceptedResumes(jobId); // Fetch and display accepted resumes

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Close the current page and return to the previous page (JobRequestsPage)
                dispose();
            }
        });
        buttonPanel.add(backButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void fetchAndDisplayAcceptedResumes(int jobId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT employee_name, resume_link FROM accepted_resumes WHERE job_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String employeeName = rs.getString("employee_name");
                String resumeLink = rs.getString("resume_link");
                addResumeToPanel(employeeName, resumeLink, resumesPanel);
            }
        } catch (SQLException e) {
            //LOGGER.log(Level.SEVERE, "Error fetching accepted resumes for job ID: " + jobId, e);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    //LOGGER.log(Level.SEVERE, "Error closing connection for job ID: " + jobId, e);
                    e.printStackTrace();
                }
            }
        }
    }

    private void addResumeToPanel(String employeeName, String resumeLink, JPanel panel) {
        JPanel resumePanel = new JPanel();
        resumePanel.setLayout(new BorderLayout(5, 5));
        resumePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel nameLabel = new JLabel("Employee: " + employeeName);
        JLabel resumeLabel = new JLabel("Resume Link: " + resumeLink);
        resumePanel.add(nameLabel, BorderLayout.NORTH);
        resumePanel.add(resumeLabel, BorderLayout.CENTER);
        panel.add(resumePanel);
        panel.revalidate();
        panel.repaint();
    }
}
