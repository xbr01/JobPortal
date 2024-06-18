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

public class EmployeeProfilePage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private EmployeePage employeePage;
    private int userId;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField skillsField;
    private JTextField workExperienceField;
    private JTextField educationField;
    private JTextField resumeLinkField;
    private JButton editButton;
    private JButton saveButton;

    public EmployeeProfilePage(EmployeePage employeePage, int userId) {
        this.employeePage = employeePage;
        this.userId = userId;

        setTitle("Employee Profile Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 10));

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(BorderFactory.createTitledBorder("Profile"));
        profilePanel.setBackground(new Color(245, 245, 245));
        contentPane.add(new JScrollPane(profilePanel), BorderLayout.CENTER);

        nameField = createField(profilePanel, "Name:");
        emailField = createField(profilePanel, "Email:");
        skillsField = createField(profilePanel, "Skills:");
        workExperienceField = createField(profilePanel, "Work Experience:");
        educationField = createField(profilePanel, "Educational Qualification:");
        resumeLinkField = createField(profilePanel, "Resume Link:");

        editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setFieldsEditable(true);
                saveButton.setVisible(true);
                editButton.setVisible(false);
            }
        });
        profilePanel.add(editButton);

        saveButton = new JButton("Save");
        saveButton.setVisible(false);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveProfile();
            }
        });
        profilePanel.add(saveButton);

        fetchAndDisplayProfile();

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

        setFieldsEditable(false);
    }

    private JTextField createField(JPanel panel, String label) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BorderLayout(5, 5));
        fieldPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel fieldLabel = new JLabel(label);
        JTextField textField = new JTextField();
        fieldPanel.add(fieldLabel, BorderLayout.WEST);
        fieldPanel.add(textField, BorderLayout.CENTER);
        panel.add(fieldPanel);
        return textField;
    }

    private void fetchAndDisplayProfile() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT name, email, skills, work_experience, education, resume_link FROM profile WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email"));
                skillsField.setText(rs.getString("skills"));
                workExperienceField.setText(rs.getString("work_experience"));
                educationField.setText(rs.getString("education"));
                resumeLinkField.setText(rs.getString("resume_link"));
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

    private void setFieldsEditable(boolean editable) {
        nameField.setEditable(editable);
        emailField.setEditable(editable);
        skillsField.setEditable(editable);
        workExperienceField.setEditable(editable);
        educationField.setEditable(editable);
        resumeLinkField.setEditable(editable);
    }

    private void saveProfile() {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT OR REPLACE INTO profile (user_id, name, email, skills, work_experience, education, resume_link) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, nameField.getText());
            pstmt.setString(3, emailField.getText());
            pstmt.setString(4, skillsField.getText());
            pstmt.setString(5, workExperienceField.getText());
            pstmt.setString(6, educationField.getText());
            pstmt.setString(7, resumeLinkField.getText());
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

        setFieldsEditable(false);
        saveButton.setVisible(false);
        editButton.setVisible(true);
    }

    private void navigateToEmployeePage() {
        employeePage.setVisible(true);
        this.dispose();
    }
}
