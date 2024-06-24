package Front;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LandingPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public LandingPage() {
        setTitle("Job Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setBackground(new Color(240, 248, 255));
        setContentPane(contentPane);

        // Top panel with welcome message
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(240, 248, 255));
        JLabel welcomeLabel = new JLabel("Welcome to the Job Portal!");
        JLabel dreamJobLabel = new JLabel("Find your dream job today!");
        styleLabel(welcomeLabel, Font.BOLD, 20);
        styleLabel(dreamJobLabel, Font.PLAIN, 16);
        topPanel.add(welcomeLabel);
        topPanel.add(dreamJobLabel);
        contentPane.add(topPanel, BorderLayout.NORTH);

        // Center panel with image and features
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(240, 248, 255));

        // Image of a job applicant
        JLabel applicantImage = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("path/to/applicantImage.jpg"); // Ensure this path is correct
            Image scaledImage = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            applicantImage.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            applicantImage.setText("Image not found");
        }
        centerPanel.add(applicantImage);

        // Adding features of the website
        JTextArea featuresArea = new JTextArea(10, 40);
        featuresArea.setText(
            "Website Features:\n" +
            "- Browse thousands of job listings\n" +
            "- Apply to jobs with one click\n" +
            "- Upload and manage your resume\n" +
            "- Get job recommendations based on your profile\n" +
            "- Connect with top employers"
        );
        featuresArea.setEditable(false);
        featuresArea.setWrapStyleWord(true);
        featuresArea.setLineWrap(true);
        featuresArea.setFont(new Font("Arial", Font.PLAIN, 16));
        featuresArea.setBackground(new Color(240, 248, 255));
        featuresArea.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230), 2));
        centerPanel.add(new JScrollPane(featuresArea));

        contentPane.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with login navigation
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(240, 248, 255));
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton navigateToLoginButton = new JButton("Login");
        styleButton(navigateToLoginButton, new Color(0, 122, 255));
        navigateToLoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main loginPage = new Main();
                loginPage.setVisible(true);
                dispose();
            }
        });

        bottomPanel.add(navigateToLoginButton);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleLabel(JLabel label, int fontStyle, int fontSize) {
        label.setFont(new Font("Arial", fontStyle, fontSize));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LandingPage frame = new LandingPage();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
