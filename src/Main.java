import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Main {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Program Installer");
        frame.setSize(600, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Welcome panel
        JPanel welcomePanel = new JPanel();
        JLabel welcomeLabel = new JLabel("Books to Voice with AI Installer");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomePanel.add(welcomeLabel);

        // Content panel
        JPanel contentPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel installPathLabel = new JLabel("Install Path:");
        installPathLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        JTextField installPathField = new JTextField("/home/" + System.getProperty("user.name") + "/Documents");
        installPathField.setFont(new Font("Arial", Font.PLAIN, 20));
        JButton browseButton = new JButton("Browse...");
        browseButton.setFont(new Font("Arial", Font.PLAIN, 20));

        contentPanel.add(installPathLabel);
        contentPanel.add(installPathField);
        contentPanel.add(browseButton);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton installButton = new JButton("Install");
        installButton.setFont(new Font("Arial", Font.BOLD, 18));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 18));

        buttonPanel.add(installButton);
        buttonPanel.add(cancelButton);

        // Add action listeners
        installButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String projectRoot = installPathField.getText() + "/Books-to-Voice-with-AI-Installer";
                if (!isPython310Installed()) {
                    int choice = JOptionPane.showConfirmDialog(
                            frame,
                            "Python 3.10 is not installed. Please install it to continue.",
                            "Python Not Installed",
                            JOptionPane.CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (choice == JOptionPane.CANCEL_OPTION) {
                        System.exit(0);
                    } else {
                        System.exit(0);
                    }
                }
                System.out.println("Installation directory: " + projectRoot);
                File theDir = new File(projectRoot);
                if (!theDir.exists()) {
                    boolean created = theDir.mkdirs();
                    System.out.println("Directory created: " + created);
                }

                new Thread(() -> {
                    System.out.println("Starting installation...");
                    downloadRepository(projectRoot);
                    System.out.println("Repository downloaded.");
                    createAndInstallVenv(projectRoot + "/Books-to-Voice-with-AI");
                    System.out.println("Virtual environment created and dependencies installed.");
                    JOptionPane.showMessageDialog(frame, "Installation Complete!");
                }).start();
            }
        });

        cancelButton.addActionListener(e -> System.exit(0));

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = fileChooser.showOpenDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                installPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        // Add panels to frame
        frame.add(welcomePanel, BorderLayout.NORTH);
        frame.add(contentPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Make frame visible
        frame.setVisible(true);
    }

    private static boolean isPython310Installed() {
        boolean a;
        boolean b;
        try {
            executeCommand("python3.10 --version");
            a = true;
        } catch (Exception e) {
            a = false;
        }
        try {
            executeCommand("python310 --version");
            b = true;
        } catch (Exception e) {
            b = false;
        }
        return a || b;
    }

    private static void downloadRepository(String installPath) {
        String repoUrl = "https://github.com/Radonchnk/Books-to-Voice-with-AI";
        String installCommand = "git clone " + repoUrl + " " + installPath + "/Books-to-Voice-with-AI";
        try {
            Process process = Runtime.getRuntime().exec(installCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output;
            while ((output = reader.readLine()) != null) {
                System.out.println(output);
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void createAndInstallVenv(String projectRoot) {
        String venvCommand = projectRoot + "/venv/bin/python3";
        try {
            executeCommand("python3.10 -m venv " + projectRoot + "/venv");
        } catch (Exception e) {
            try {
                executeCommand("python310 -m venv " + projectRoot + "/venv");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            executeCommand(venvCommand + " -m pip install -r " + projectRoot + "/requirements.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            executeCommand(projectRoot + "/venv/bin/python -m unidic download");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeCommand(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", command});
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output;
        while ((output = reader.readLine()) != null) {
            System.out.println(output);
        }
        process.waitFor();
    }
}
