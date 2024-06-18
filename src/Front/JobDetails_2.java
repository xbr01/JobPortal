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

public class JobDetails_2 extends JFrame {

    private JPanel contentPane;
    private EmployeePage employeePage;
    private int jobId;
    private int employeeId;
    private String employeeName;

    public JobDetails_2(EmployeePage employeePage, int jobId, String jobTitle, int employeeId, String employeeName) {
        this.employeePage = employeePage;
        this.jobId = jobId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;

        setTitle("Job Details: " + jobTitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400); // Increased the size for better UI display
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 10));

        JPanel detailsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.add(detailsPanel, BorderLayout.CENTER);

        String[] jobDetails = getJobDetails(jobId);
        if (jobDetails == null) {
            jobDetails = new String[7]; // Initialize with empty strings
            for (int i = 0; i < jobDetails.length; i++) {
                jobDetails[i] = "";
            }
        }

        JLabel titleLabel = new JLabel("Job Title:");
        detailsPanel.add(titleLabel);
        JLabel titleValueLabel = new JLabel(jobTitle);
        detailsPanel.add(titleValueLabel);

        JLabel descriptionLabel = new JLabel("Description:");
        detailsPanel.add(descriptionLabel);
        JTextArea descriptionTextArea = new JTextArea(jobDetails[1]);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setEditable(false);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionTextArea);
        detailsPanel.add(descriptionScrollPane);

        JLabel requirementsLabel = new JLabel("Requirements:");
        detailsPanel.add(requirementsLabel);
        JTextArea requirementsTextArea = new JTextArea(jobDetails[2]);
        requirementsTextArea.setLineWrap(true);
        requirementsTextArea.setWrapStyleWord(true);
        requirementsTextArea.setEditable(false);
        JScrollPane requirementsScrollPane = new JScrollPane(requirementsTextArea);
        detailsPanel.add(requirementsScrollPane);

        JLabel qualificationLabel = new JLabel("Educational Qualification:");
        detailsPanel.add(qualificationLabel);
        JLabel qualificationValueLabel = new JLabel(jobDetails[3]);
        detailsPanel.add(qualificationValueLabel);

        JLabel experienceLabel = new JLabel("Work Experience:");
        detailsPanel.add(experienceLabel);
        JLabel experienceValueLabel = new JLabel(jobDetails[4]);
        detailsPanel.add(experienceValueLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitResume();
            }
        });
        buttonPanel.add(applyButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigateToEmployeePage();
            }
        });
        buttonPanel.add(backButton);

        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    private String[] getJobDetails(int jobId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] jobDetailsArray = new String[7];

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT job_title, job_description, job_requirements, educational_qualification, " +
                         "work_experience, base_salary, employer_id FROM jobs WHERE job_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, jobId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                jobDetailsArray[0] = rs.getString("job_title");
                jobDetailsArray[1] = rs.getString("job_description");
                jobDetailsArray[2] = rs.getString("job_requirements");
                jobDetailsArray[3] = rs.getString("educational_qualification");
                jobDetailsArray[4] = rs.getString("work_experience");
                jobDetailsArray[5] = rs.getString("base_salary");
                jobDetailsArray[6] = rs.getString("employer_id");
            } else {
                System.out.println("No job details found for jobId: " + jobId); // Debug statement
                return null;
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

        return jobDetailsArray;
    }

    private void submitResume() {
        Connection conn = null;
        PreparedStatement fetchPstmt = null;
        PreparedStatement insertPstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();

            // Fetch the employer_id associated with the job_id
            String fetchEmployerSql = "SELECT employer_id FROM jobs WHERE job_id = ?";
            fetchPstmt = conn.prepareStatement(fetchEmployerSql);
            fetchPstmt.setInt(1, jobId);
            rs = fetchPstmt.executeQuery();
            int employerId = -1;
            if (rs.next()) {
                employerId = rs.getInt("employer_id");
            }
            rs.close();
            fetchPstmt.close();

            // Check if employer_id was successfully fetched
            if (employerId == -1) {
                JOptionPane.showMessageDialog(contentPane, "Failed to retrieve employer information. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fetch the resume link from the profile table
            String resumeLink = getResumeLink(employeeId);
            if (resumeLink == null || resumeLink.isEmpty()) {
                JOptionPane.showMessageDialog(contentPane, "No resume found in profile. Please update your profile.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert a new resume entry
            String insertSql = "INSERT INTO resumes (employee_id, employee_name, job_id, resume_link, employer_id) VALUES (?, ?, ?, ?, ?)";
            insertPstmt = conn.prepareStatement(insertSql);
            insertPstmt.setInt(1, employeeId);
            insertPstmt.setString(2, employeeName);
            insertPstmt.setInt(3, jobId);
            insertPstmt.setString(4, resumeLink);
            insertPstmt.setInt(5, employerId);
            insertPstmt.executeUpdate();
            JOptionPane.showMessageDialog(contentPane, "Resume submitted successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(contentPane, "Failed to submit resume. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (fetchPstmt != null) fetchPstmt.close();
                if (insertPstmt != null) insertPstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Method to get resume link from profile
    private String getResumeLink(int employeeId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String resumeLink = "";

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT resume_link FROM profile WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                resumeLink = rs.getString("resume_link");
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

        return resumeLink;
    }

    private void navigateToEmployeePage() {
        employeePage.setVisible(true);
        this.dispose();
    }
}
