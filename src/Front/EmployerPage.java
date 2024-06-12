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

public class EmployerPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel jobsPanel;
    private Main loginPage;
    private int employerId;

    public EmployerPage(Main loginPage, int employerId) {
        this.loginPage = loginPage;
        this.employerId = employerId;
        setTitle("Employer Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 10));

        jobsPanel = new JPanel();
        jobsPanel.setLayout(new BoxLayout(jobsPanel, BoxLayout.Y_AXIS));
        jobsPanel.setBorder(BorderFactory.createTitledBorder("Your Job Listings"));
        jobsPanel.setBackground(new Color(245, 245, 245));
        contentPane.add(new JScrollPane(jobsPanel), BorderLayout.CENTER);

        fetchAndDisplayJobs();

        JButton addJobButton = new JButton("Add Job");
        addJobButton.setBackground(new Color(52, 199, 89));
        addJobButton.setForeground(Color.WHITE);
        addJobButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigateToAddJobPage();
            }
        });
        contentPane.add(addJobButton, BorderLayout.SOUTH);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(255, 69, 0));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigateToLoginPage();
            }
        });
        logoutPanel.add(logoutButton);
        contentPane.add(logoutPanel, BorderLayout.NORTH);
    }

    void fetchAndDisplayJobs() {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT job_id, job_title FROM jobs WHERE employer_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int jobId = rs.getInt("job_id");
                String jobTitle = rs.getString("job_title");
                addJobToPanel(jobId, jobTitle, jobsPanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    private void addJobToPanel(int jobId, String jobTitle, JPanel panel) {
        JPanel jobPanel = new JPanel();
        jobPanel.setLayout(new BorderLayout(5, 5));
        jobPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel jobLabel = new JLabel(jobTitle);
        JButton removeButton = new JButton("Remove");
        removeButton.setBackground(new Color(255, 59, 48));
        removeButton.setForeground(Color.WHITE);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.remove(jobPanel);
                panel.revalidate();
                panel.repaint();
                removeJobFromDatabase(jobId, jobTitle);
            }
        });
        jobPanel.add(jobLabel, BorderLayout.CENTER);
        jobPanel.add(removeButton, BorderLayout.EAST);
        jobPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showJobRequests(jobId, jobTitle);
            }
        });
        panel.add(jobPanel);
        panel.revalidate();
        panel.repaint();
    }

    private void removeJobFromDatabase(int jobId, String jobTitle) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM jobs WHERE job_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, jobId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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

    private void showJobRequests(int jobId, String jobTitle) {
        JobRequestsPage jobRequestsPage = new JobRequestsPage(jobId, jobTitle, this);
        jobRequestsPage.setVisible(true);
        this.setVisible(false);
    }

    private void navigateToAddJobPage() {
        AddJobPage addJobPage = new AddJobPage(this, employerId);
        addJobPage.setVisible(true);
        this.dispose();
    }

    private void navigateToLoginPage() {
        loginPage.setVisible(true);
        this.dispose();
    }
}
