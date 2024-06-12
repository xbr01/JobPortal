package Front;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddJobPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField jobTitleField;
    private JTextArea jobDescriptionArea;
    private JTextArea jobRequirementsArea;
    private JTextField educationalQualificationField;
    private JTextField workExperienceField;
    private JTextField baseSalaryField;
    private EmployerPage employerPage;
    private int employerId;

    public AddJobPage(EmployerPage employerPage, int employerId) {
        this.employerPage = employerPage;
        this.employerId = employerId;
        setTitle("Add Job");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        JPanel jobTitlePanel = new JPanel();
        jobTitlePanel.setLayout(new FlowLayout());
        jobTitlePanel.add(new JLabel("Job Title:"));
        jobTitleField = new JTextField(20);
        jobTitlePanel.add(jobTitleField);

        JPanel jobDescriptionPanel = new JPanel();
        jobDescriptionPanel.setLayout(new FlowLayout());
        jobDescriptionPanel.add(new JLabel("Job Description:"));
        jobDescriptionArea = new JTextArea(5, 20);
        jobDescriptionPanel.add(new JScrollPane(jobDescriptionArea));

        JPanel jobRequirementsPanel = new JPanel();
        jobRequirementsPanel.setLayout(new FlowLayout());
        jobRequirementsPanel.add(new JLabel("Job Requirements:"));
        jobRequirementsArea = new JTextArea(5, 20);
        jobRequirementsPanel.add(new JScrollPane(jobRequirementsArea));

        JPanel educationalQualificationPanel = new JPanel();
        educationalQualificationPanel.setLayout(new FlowLayout());
        educationalQualificationPanel.add(new JLabel("Educational Qualification:"));
        educationalQualificationField = new JTextField(20);
        educationalQualificationPanel.add(educationalQualificationField);

        JPanel workExperiencePanel = new JPanel();
        workExperiencePanel.setLayout(new FlowLayout());
        workExperiencePanel.add(new JLabel("Work Experience:"));
        workExperienceField = new JTextField(20);
        workExperiencePanel.add(workExperienceField);

        JPanel baseSalaryPanel = new JPanel();
        baseSalaryPanel.setLayout(new FlowLayout());
        baseSalaryPanel.add(new JLabel("Base Salary:"));
        baseSalaryField = new JTextField(20);
        baseSalaryPanel.add(baseSalaryField);

        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(52, 199, 89));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addJob();
            }
        });

        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(255, 165, 0));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigateBack();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        contentPane.add(jobTitlePanel);
        contentPane.add(jobDescriptionPanel);
        contentPane.add(jobRequirementsPanel);
        contentPane.add(educationalQualificationPanel);
        contentPane.add(workExperiencePanel);
        contentPane.add(baseSalaryPanel);
        contentPane.add(buttonPanel);
    }

    private void addJob() {
        String jobTitle = jobTitleField.getText();
        String jobDescription = jobDescriptionArea.getText();
        String jobRequirements = jobRequirementsArea.getText();
        String educationalQualification = educationalQualificationField.getText();
        String workExperience = workExperienceField.getText();
        String baseSalary = baseSalaryField.getText();

        if (jobTitle.isEmpty() || jobDescription.isEmpty() || jobRequirements.isEmpty() ||
                educationalQualification.isEmpty() || workExperience.isEmpty() || baseSalary.isEmpty()) {
            JOptionPane.showMessageDialog(contentPane, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO jobs (job_title, job_description, job_requirements, educational_qualification, work_experience, base_salary, employer_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, jobTitle);
            pstmt.setString(2, jobDescription);
            pstmt.setString(3, jobRequirements);
            pstmt.setString(4, educationalQualification);
            pstmt.setString(5, workExperience);
            pstmt.setString(6, baseSalary);
            pstmt.setInt(7, employerId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(contentPane, "Job added successfully.");
            //employerPage.refreshJobs();
            employerPage.setVisible(true);
            this.dispose(); // Close the add job page
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(contentPane, "Database error. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void navigateBack() {
        employerPage.setVisible(true);
        this.dispose();
    }
}
