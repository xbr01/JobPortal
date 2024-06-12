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
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 10));

        // Top panel with welcome message
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(new JLabel("Welcome to the Job Portal!"));
        topPanel.add(new JLabel("Find your dream job today!"));
        contentPane.add(topPanel, BorderLayout.NORTH);

        // Center panel with image and features
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Image of a job applicant
        JLabel applicantImage = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("path/to/applicantImage.jpg"); // Ensure this path is correct
            applicantImage.setIcon(icon);
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
        centerPanel.add(new JScrollPane(featuresArea));

        contentPane.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with login navigation
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton navigateToLoginButton = new JButton("Login");
        navigateToLoginButton.setBackground(new Color(0, 122, 255));
        navigateToLoginButton.setForeground(Color.WHITE);
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