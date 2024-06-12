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

public class JobDetailPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    //private EmployeePage employeePage;
    private JTextField resumeField;
    private int jobId; // Store job ID
    private int employeeId; // Store employee ID

    public JobDetailPage(EmployeePage employeePage, int jobId, String jobTitle, int employeeId) {
        //this.employeePage = employeePage;
        this.jobId = jobId; // Initialize job ID
        this.employeeId = employeeId; // Initialize employee ID
        setTitle("Job Detail - " + jobTitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 10));

        JPanel jobDetailsPanel = new JPanel();
        jobDetailsPanel.setLayout(new BoxLayout(jobDetailsPanel, BoxLayout.Y_AXIS));
        jobDetailsPanel.add(new JLabel("Job Title: " + jobTitle));
        jobDetailsPanel.add(new JLabel("Job Description: ")); // You can add more details here
        contentPane.add(jobDetailsPanel, BorderLayout.CENTER);

        JPanel applyPanel = new JPanel();
        applyPanel.setLayout(new FlowLayout());
        applyPanel.add(new JLabel("Resume:"));
        resumeField = new JTextField(20);
        applyPanel.add(resumeField);

        JButton applyButton = new JButton("Apply");
        applyButton.setBackground(new Color(0, 122, 255));
        applyButton.setForeground(Color.WHITE);
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitResume();
            }
        });
        applyPanel.add(applyButton);
        contentPane.add(applyPanel, BorderLayout.SOUTH);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(255, 165, 0));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                employeePage.setVisible(true);
                dispose();
            }
        });
        backPanel.add(backButton);
        contentPane.add(backPanel, BorderLayout.NORTH);
    }

    //top get employee name to enter into resumes
    private String getEmployeeName(int employeeId) {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String employeeName = "";

    try {
        conn = DBConnection.getConnection();
        String sql = "SELECT employee_name FROM employees WHERE employee_id = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, employeeId);
        rs = pstmt.executeQuery();
        if (rs.next()) {
            employeeName = rs.getString("employee_name");
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

    return employeeName;
}


private void submitResume() {
    String resumeText = resumeField.getText();
    if (!resumeText.isEmpty()) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String employeeName = getEmployeeName(employeeId); // Fetch employee name
            String sql = "INSERT INTO resumes (employee_id, employee_name, job_id, resume_text) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, employeeName); // Insert employee name
            pstmt.setInt(3, jobId);
            pstmt.setString(4, resumeText);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(contentPane, "Resume submitted successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(contentPane, "Failed to submit resume. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    } else {
        JOptionPane.showMessageDialog(contentPane, "Resume field cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
}