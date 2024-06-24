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

public class Main extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JRadioButton employeeRadio;
    private JRadioButton employerRadio;
    private ButtonGroup radioGroup;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main frame = new Main();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Main() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBackground(new Color(240, 248, 255));
        setContentPane(contentPane);

        // Create and style radio buttons
        employeeRadio = new JRadioButton("Employee");
        employerRadio = new JRadioButton("Employer");
        styleRadioButton(employeeRadio);
        styleRadioButton(employerRadio);
        radioGroup = new ButtonGroup();
        radioGroup.add(employeeRadio);
        radioGroup.add(employerRadio);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout());
        radioPanel.setBackground(new Color(240, 248, 255));
        radioPanel.add(employeeRadio);
        radioPanel.add(employerRadio);

        // Create and style username panel
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout());
        usernamePanel.setBackground(new Color(240, 248, 255));
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField = new JTextField(20);
        styleTextField(usernameField);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        // Create and style password panel
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout());
        passwordPanel.setBackground(new Color(240, 248, 255));
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        // Create and style buttons
        loginButton = new JButton("Login");
        styleButton(loginButton, new Color(0, 122, 255));
        registerButton = new JButton("Register");
        styleButton(registerButton, new Color(0, 122, 255));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        contentPane.add(radioPanel);
        contentPane.add(usernamePanel);
        contentPane.add(passwordPanel);
        contentPane.add(buttonPanel);

        // Add action listeners for buttons
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (employeeRadio.isSelected()) {
                    loginUser(0); // 0 for employee role
                } else if (employerRadio.isSelected()) {
                    loginUser(1); // 1 for employer role
                } else {
                    JOptionPane.showMessageDialog(contentPane, "Please select either Employee or Employer.");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open the registration page
                RegistrationPage registrationPage = new RegistrationPage(Main.this);
                registrationPage.setVisible(true);
                setVisible(false); // Hide the login frame
            }
        });
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230), 2));
        textField.setBackground(new Color(255, 255, 255));
    }

    private void styleRadioButton(JRadioButton radioButton) {
        radioButton.setFont(new Font("Arial", Font.PLAIN, 16));
        radioButton.setBackground(new Color(240, 248, 255));
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    private void loginUser(int role) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (!validateInput(username, password)) {
            return;
        }

        try {
            Connection connection = DBConnection.getConnection();
            String sql = "SELECT id FROM registration WHERE name = ? AND password = ? AND role = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setInt(3, role);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Successful login
                int userId = resultSet.getInt("id");
                if (role == 0) {
                    // Redirect to employee page
                    EmployeePage employeePage = new EmployeePage(this, userId);
                    employeePage.setVisible(true);
                } else {
                    // Redirect to employer page with employer ID
                    EmployerPage employerPage = new EmployerPage(this, userId);
                    employerPage.setVisible(true);
                }
                this.dispose(); // Close the login page
            } else {
                JOptionPane.showMessageDialog(contentPane, "Invalid username, password, or role.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(contentPane, "Database error. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}
