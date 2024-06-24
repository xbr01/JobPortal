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

public class EmployeePage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel jobsPanel;
    private Main loginPage;
    private int userId;
    private String employeeName;

    public EmployeePage(Main loginPage, int userId) {

        if (loginPage == null) {
            throw new IllegalArgumentException("Login page cannot be null");
        }

        this.loginPage = loginPage;
        this.userId = userId;
        this.employeeName = getEmployeeName(userId);

        setTitle("Employee Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 10));

        jobsPanel = new JPanel();
        jobsPanel.setLayout(new BoxLayout(jobsPanel, BoxLayout.Y_AXIS));
        jobsPanel.setBorder(BorderFactory.createTitledBorder("Available Jobs"));
        jobsPanel.setBackground(new Color(245, 245, 245));
        contentPane.add(new JScrollPane(jobsPanel), BorderLayout.CENTER);

        fetchAndDisplayJobs();

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // JButton showNotificationsButton = new JButton("Show Notifications");
        // showNotificationsButton.addActionListener(new ActionListener() {
        //     public void actionPerformed(ActionEvent e) {
        //         showNotificationsPage();
        //     }
        // });
        // bottomPanel.add(showNotificationsButton);

        JButton showAppliedJobsButton = new JButton("Show Applied Jobs");
        showAppliedJobsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAppliedJobsPage();
            }
        });
        bottomPanel.add(showAppliedJobsButton);

        JButton showProfileButton = new JButton("Show Profile");
        showProfileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showProfilePage();
            }
        });
        bottomPanel.add(showProfileButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigateToLoginPage();
            }
        });
        bottomPanel.add(logoutButton);

        contentPane.add(bottomPanel, BorderLayout.SOUTH);
    }

    private String getEmployeeName(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String employeeName = "";

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT name FROM registration WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                employeeName = rs.getString("name");
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

    private void fetchAndDisplayJobs() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT job_id, job_title FROM jobs";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int jobId = rs.getInt("job_id");
                String jobTitle = rs.getString("job_title");
                JButton jobButton = new JButton(jobTitle);
                jobButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        navigateToJobDetails(jobId, jobTitle);
                    }
                });
                jobsPanel.add(jobButton);
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

        revalidate();
        repaint();
    }

    private void navigateToJobDetails(int jobId, String jobTitle) {
        JobDetails_2 jobDetails = new JobDetails_2(this, jobId, jobTitle, userId, employeeName);
        jobDetails.setVisible(true);
        this.setVisible(false);
    }

    private void navigateToLoginPage() {
        loginPage.setVisible(true);
        this.dispose();
    }

    private void showAppliedJobsPage() {
        AppliedJobsPage appliedJobsPage = new AppliedJobsPage(this,userId);
        appliedJobsPage.setVisible(true);
        this.setVisible(false);
    }

    private void showProfilePage() {
        EmployeeProfilePage profilePage = new EmployeeProfilePage(this, userId);
        profilePage.setVisible(true);
        this.setVisible(false);
    }

    // private void showNotificationsPage() {
    //     NotificationPage notificationPage = new NotificationPage(employeeName);
    //     notificationPage.setVisible(true);
    // }

    public static void main(String[] args) {
        Main loginPage = new Main(); // Replace with your actual login page instance
        EmployeePage employeePage = new EmployeePage(loginPage, 1); // Replace 1 with actual user ID
        employeePage.setVisible(true);
    }
}
