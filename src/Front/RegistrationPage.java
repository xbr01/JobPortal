package Front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JRadioButton employeeButton;
    private JRadioButton employerButton;
    private ButtonGroup roleGroup;
    //private JFrame parentFrame;

    public RegistrationPage(JFrame parentFrame) {
        //this.parentFrame = parentFrame;
        setTitle("Registration Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new GridBagLayout());

        // Create constraints for layout
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.WEST;

        // Username label and text field
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(usernameLabel, constraints);
        constraints.gridx = 1;
        add(usernameField, constraints);

        // Email label and text field
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        constraints.gridx = 0;
        constraints.gridy = 1;
        add(emailLabel, constraints);
        constraints.gridx = 1;
        add(emailField, constraints);

        // Password label and password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        constraints.gridx = 0;
        constraints.gridy = 2;
        add(passwordLabel, constraints);
        constraints.gridx = 1;
        add(passwordField, constraints);

        // Radio buttons for role selection
        JLabel roleLabel = new JLabel("Role:");
        employeeButton = new JRadioButton("Employee");
        employerButton = new JRadioButton("Employer");
        roleGroup = new ButtonGroup();
        roleGroup.add(employeeButton);
        roleGroup.add(employerButton);

        constraints.gridx = 0;
        constraints.gridy = 3;
        add(roleLabel, constraints);
        constraints.gridx = 1;
        add(employeeButton, constraints);
        constraints.gridy = 4;
        add(employerButton, constraints);

        // Register and Cancel buttons
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");
        constraints.gridx = 0;
        constraints.gridy = 5;
        add(registerButton, constraints);
        constraints.gridx = 1;
        add(cancelButton, constraints);

        // Action listener for Register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                int role = employeeButton.isSelected() ? 0 : 1;

                if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    if (registerUser(username, email, password, role)) {
                        JOptionPane.showMessageDialog(null, "Registration successful!");
                        // Show login page after registration
                        parentFrame.setVisible(true);
                        dispose(); // Close the registration frame
                    } else {
                        JOptionPane.showMessageDialog(null, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action listener for Cancel button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.setVisible(true);
                dispose(); // Close the registration frame
            }
        });

        // Center the frame and make it visible
        setLocationRelativeTo(null);
    }

    private boolean registerUser(String name, String email, String password, int role) {
        try {
            // Get a database connection
            Connection connection = DBConnection.getConnection();

            // Prepare the SQL statement for registration table
            String registerSql = "INSERT INTO registration (name, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement registerStatement = connection.prepareStatement(registerSql);

            // Set the parameters for registration table
            registerStatement.setString(1, name);
            registerStatement.setString(2, email);
            registerStatement.setString(3, password);
            registerStatement.setInt(4, role);

            // Execute the registration statement
            int rowsInserted = registerStatement.executeUpdate();

            // Close the registration statement
            registerStatement.close();

            // Prepare the SQL statement for login table
            //String loginSql = "INSERT INTO login (name, email, password, role) VALUES (?, ?, ?, ?)";
            //PreparedStatement loginStatement = connection.prepareStatement(loginSql);

            // Set the parameters for login table
            // loginStatement.setString(1, name);
            // loginStatement.setString(2, email);
            // loginStatement.setString(3, password);
            // loginStatement.setInt(4, role);

            // // Execute the login statement
            // rowsInserted += loginStatement.executeUpdate();

            // // Close the login statement and connection
            // loginStatement.close();
            // connection.close();

            // Check if the insertion was successful
            return rowsInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}