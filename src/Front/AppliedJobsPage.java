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

public class AppliedJobsPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel appliedJobsPanel;
    private EmployeePage employeePage;
    private int employeeId;

    public AppliedJobsPage(EmployeePage employeePage, int employeeId) {
        this.employeePage = employeePage;
        this.employeeId = employeeId;
        setTitle("Applied Jobs Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 10));

        appliedJobsPanel = new JPanel();
        appliedJobsPanel.setLayout(new BoxLayout(appliedJobsPanel, BoxLayout.Y_AXIS));
        appliedJobsPanel.setBorder(BorderFactory.createTitledBorder("Applied Jobs"));
        appliedJobsPanel.setBackground(new Color(245, 245, 245));
        contentPane.add(new JScrollPane(appliedJobsPanel), BorderLayout.CENTER);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back to Employee Page");
        backButton.setBackground(new Color(0, 122, 255));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigateToEmployeePage();
            }
        });
        backPanel.add(backButton);
        contentPane.add(backPanel, BorderLayout.NORTH);

        fetchAndDisplayAppliedJobs();
    }

    private void fetchAndDisplayAppliedJobs() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //check the logic here fromy the tables cross check if
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT r.job_id, j.job_title, r.employee_name, r.resume_link " +
                         "FROM resumes r " +
                         "JOIN jobs j ON r.job_id = j.job_id " +
                         "WHERE r.employee_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int jobId = rs.getInt("job_id");
                String jobTitle = rs.getString("job_title");
                String employeeName = rs.getString("employee_name");
                String resumeLink = rs.getString("resume_link");
                boolean isAccepted = checkIfAccepted(jobId, employeeName);
                addAppliedJob(jobId, jobTitle, resumeLink, isAccepted);
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

    private boolean checkIfAccepted(int jobId, String employeeName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isAccepted = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) AS count FROM accepted_resumes WHERE job_id = ? AND employee_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, jobId);
            pstmt.setString(2, employeeName);
            rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt("count") > 0) {
                isAccepted = true;
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
        return isAccepted;
    }

    public void addAppliedJob(int jobId, String jobTitle, String resumeLink, boolean isAccepted) {
        JPanel jobPanel = new JPanel();
        jobPanel.setLayout(new BorderLayout(5, 5));
        jobPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel jobLabel = new JLabel(jobTitle);
        JLabel resumeLinkLabel = new JLabel("Resume Link: " + resumeLink);

        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BorderLayout(5, 5));
        labelsPanel.add(jobLabel, BorderLayout.CENTER);
        labelsPanel.add(resumeLinkLabel, BorderLayout.SOUTH);

        if (isAccepted) {
            //JLabel selectedLabel = new JLabel("Status: Selected");
            //selectedLabel.setForeground(Color.GREEN);
            //labelsPanel.add(selectedLabel, BorderLayout.NORTH);
        } else {
            // JLabel selectedLabel = new JLabel("Status: not Selected");
            // selectedLabel.setForeground(Color.GREEN);
            // labelsPanel.add(selectedLabel, BorderLayout.NORTH);

        }

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(255, 59, 48));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeJobFromDatabase(jobId, employeeId);
                appliedJobsPanel.remove(jobPanel);
                appliedJobsPanel.revalidate();
                appliedJobsPanel.repaint();
            }
        });

        jobPanel.add(labelsPanel, BorderLayout.CENTER);
        jobPanel.add(deleteButton, BorderLayout.EAST);
        appliedJobsPanel.add(jobPanel);
        appliedJobsPanel.revalidate();
        appliedJobsPanel.repaint();
    }

    private void removeJobFromDatabase(int jobId, int employeeId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM resumes WHERE job_id = ? AND employee_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, jobId);
            pstmt.setInt(2, employeeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void navigateToEmployeePage() {
        employeePage.setVisible(true);
        this.dispose();
    }
}
