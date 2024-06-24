package Front;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegistrationPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JRadioButton employeeButton;
    private JRadioButton employerButton;
    private ButtonGroup roleGroup;
        private JFrame parentFrame;

    public RegistrationPage(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setTitle("Registration Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new GridBagLayout());

        // Create constraints for layout
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.WEST;

        // Panel to hold all content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(new Color(240, 248, 255));

        // Title label
        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(10, 10, 20, 10);
        constraints.anchor = GridBagConstraints.CENTER;
        contentPanel.add(titleLabel, constraints);
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;

        // Username label and text field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField = new JTextField(20);
        styleTextField(usernameField);
        constraints.gridx = 0;
        constraints.gridy = 1;
        contentPanel.add(usernameLabel, constraints);
        constraints.gridx = 1;
        contentPanel.add(usernameField, constraints);

        // Email label and text field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailField = new JTextField(20);
        styleTextField(emailField);
        constraints.gridx = 0;
        constraints.gridy = 2;
        contentPanel.add(emailLabel, constraints);
        constraints.gridx = 1;
        contentPanel.add(emailField, constraints);

        // Password label and password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        constraints.gridx = 0;
        constraints.gridy = 3;
        contentPanel.add(passwordLabel, constraints);
        constraints.gridx = 1;
        contentPanel.add(passwordField, constraints);

        // Radio buttons for role selection
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        employeeButton = new JRadioButton("Employee");
        employerButton = new JRadioButton("Employer");
        styleRadioButton(employeeButton);
        styleRadioButton(employerButton);
        roleGroup = new ButtonGroup();
        roleGroup.add(employeeButton);
        roleGroup.add(employerButton);

        constraints.gridx = 0;
        constraints.gridy = 4;
        contentPanel.add(roleLabel, constraints);
        constraints.gridx = 1;
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        rolePanel.setBackground(new Color(240, 248, 255));
        rolePanel.add(employeeButton);
        rolePanel.add(employerButton);
        contentPanel.add(rolePanel, constraints);

        // Register and Cancel buttons
        JButton registerButton = new JButton("Register");
        styleButton(registerButton, new Color(52, 199, 89));
        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(255, 69, 0));
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(registerButton, constraints);
        constraints.gridx = 1;
        contentPanel.add(cancelButton, constraints);

        // Add Action listeners for buttons
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                int role = employeeButton.isSelected() ? 0 : employerButton.isSelected() ? 1 : -1;

                if (validateInput(username, email, password, role)) {
                    if (registerUser(username, email, password, role)) {
                        JOptionPane.showMessageDialog(null, "Registration successful!");
                        parentFrame.setVisible(true);
                        dispose(); // Close the registration frame
                    } else {
                        JOptionPane.showMessageDialog(null, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.setVisible(true);
                dispose(); // Close the registration frame
            }
        });

        // Add content panel to the frame
        setContentPane(contentPanel);

        // Center the frame and make it visible
        setLocationRelativeTo(null);
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

    private boolean validateInput(String username, String email, String password, int role) {
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
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

        if (role == -1) {
            JOptionPane.showMessageDialog(this, "Please select a role.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    private boolean registerUser(String name, String email, String password, int role) {
        try {
            Connection connection = DBConnection.getConnection();
            String registerSql = "INSERT INTO registration (name, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement registerStatement = connection.prepareStatement(registerSql);
            registerStatement.setString(1, name);
            registerStatement.setString(2, email);
            registerStatement.setString(3, password);
            registerStatement.setInt(4, role);
            int rowsInserted = registerStatement.executeUpdate();
            registerStatement.close();
            return rowsInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegistrationPage(null).setVisible(true);
            }
        });
    }
}
