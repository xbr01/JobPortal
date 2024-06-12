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
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        employeeRadio = new JRadioButton("Employee");
        employerRadio = new JRadioButton("Employer");
        radioGroup = new ButtonGroup();
        radioGroup.add(employeeRadio);
        radioGroup.add(employerRadio);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout());
        radioPanel.add(employeeRadio);
        radioPanel.add(employerRadio);

        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout());
        usernamePanel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        usernamePanel.add(usernameField);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout());
        passwordPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        passwordPanel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 122, 255));
        loginButton.setForeground(Color.WHITE);

        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(0, 122, 255));
        registerButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        contentPane.add(radioPanel);
        contentPane.add(usernamePanel);
        contentPane.add(passwordPanel);
        contentPane.add(buttonPanel);

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

    private void loginUser(int role) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

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
}