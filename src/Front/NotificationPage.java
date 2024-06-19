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

public class NotificationPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel notificationsPanel;
    private String employeeName;

    public NotificationPage(String employeeName) {
        this.employeeName = employeeName;
        setTitle("Notifications");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 10));

        notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
        notificationsPanel.setBorder(BorderFactory.createTitledBorder("Notifications"));
        notificationsPanel.setBackground(new Color(245, 245, 245));
        contentPane.add(new JScrollPane(notificationsPanel), BorderLayout.CENTER);

        fetchAndDisplayNotifications();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(backButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void fetchAndDisplayNotifications() {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT jobs.job_title FROM accepted_resumes JOIN jobs ON accepted_resumes.job_id = jobs.job_id WHERE accepted_resumes.employee_name = ?";
                    
                                
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employeeName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int jobId = rs.getInt("job_id");
                String jobTitle = rs.getString("job_title");
                addNotificationToPanel(jobId, jobTitle, notificationsPanel);
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

    private void addNotificationToPanel(int jobId, String jobTitle, JPanel panel) {
        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new BorderLayout(5, 5));
        notificationPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel statusLabel = new JLabel("Job Title: " + jobTitle);
        notificationPanel.add(statusLabel, BorderLayout.CENTER);

        JButton acceptButton = new JButton("Accept");
        JButton declineButton = new JButton("Decline");

        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                respondToNotification(jobId, "accepted");
                panel.remove(notificationPanel);
                panel.revalidate();
                panel.repaint();
            }
        });

        declineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                respondToNotification(jobId, "declined");
                panel.remove(notificationPanel);
                panel.revalidate();
                panel.repaint();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(acceptButton);
        buttonPanel.add(declineButton);
        notificationPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(notificationPanel);
        panel.revalidate();
        panel.repaint();
    }

    private void respondToNotification(int jobId, String response) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO final_responses (job_id, employee_name, response) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, jobId);
            pstmt.setString(2, employeeName);
            pstmt.setString(3, response);
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
}
