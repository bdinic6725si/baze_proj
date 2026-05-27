import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class AuthForms {

    public static class LoginForm extends JFrame {

        private JTextField txtUsername;
        private JPasswordField txtPassword;

        public LoginForm() {
            setTitle("Prijava — Astronomija");
            setSize(400, 250);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(false);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel lblTitle = new JLabel("Astronomija — Admin Panel", SwingConstants.CENTER);
            lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            panel.add(lblTitle, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Korisničko ime:"), gbc);

            txtUsername = new JTextField(15);
            gbc.gridx = 1; gbc.gridy = 1;
            panel.add(txtUsername, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Lozinka:"), gbc);

            txtPassword = new JPasswordField(15);
            gbc.gridx = 1; gbc.gridy = 2;
            panel.add(txtPassword, gbc);

            JButton btnLogin = new JButton("Prijavi se");
            btnLogin.setBackground(new Color(70, 130, 180));
            btnLogin.setForeground(Color.BLACK);
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            panel.add(btnLogin, gbc);

            JButton btnRegister = new JButton("Nemam nalog — Registruj se");
            btnRegister.setBorderPainted(false);
            btnRegister.setContentAreaFilled(false);
            btnRegister.setForeground(new Color(70, 130, 180));
            gbc.gridy = 4;
            panel.add(btnRegister, gbc);

            add(panel);

            btnLogin.addActionListener(e -> handleLogin());
            btnRegister.addActionListener(e -> {
                new RegisterForm().setVisible(true);
                dispose();
            });
        }

        private void handleLogin() {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Unesite korisničko ime i lozinku.", "Greška", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (checkCredentials(username, password)) {
                JOptionPane.showMessageDialog(this, "Dobrodošli, " + username + "!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
                new MainMenuForm(username).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Pogrešno korisničko ime ili lozinka.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }

        private boolean checkCredentials(String username, String password) {
            try {
                List<String> lines = Files.readAllLines(Paths.get("korisnici.txt"));
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 2 && parts[0].trim().equals(username) && parts[1].trim().equals(password)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Fajl korisnici.txt nije pronađen.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }

    public static class RegisterForm extends JFrame {

        private JTextField txtUsername;
        private JPasswordField txtPassword;
        private JPasswordField txtConfirm;

        public RegisterForm() {
            setTitle("Registracija");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(false);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel lblTitle = new JLabel("Registracija novog korisnika", SwingConstants.CENTER);
            lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            panel.add(lblTitle, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Korisničko ime:"), gbc);
            txtUsername = new JTextField(15);
            gbc.gridx = 1;
            panel.add(txtUsername, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Lozinka:"), gbc);
            txtPassword = new JPasswordField(15);
            gbc.gridx = 1;
            panel.add(txtPassword, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            panel.add(new JLabel("Potvrdi lozinku:"), gbc);
            txtConfirm = new JPasswordField(15);
            gbc.gridx = 1;
            panel.add(txtConfirm, gbc);

            JButton btnRegister = new JButton("Registruj se");
            btnRegister.setBackground(new Color(70, 130, 180));
            btnRegister.setForeground(Color.BLACK);
            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
            panel.add(btnRegister, gbc);

            JButton btnBack = new JButton("Nazad na prijavu");
            btnBack.setBorderPainted(false);
            btnBack.setContentAreaFilled(false);
            btnBack.setForeground(new Color(70, 130, 180));
            gbc.gridy = 5;
            panel.add(btnBack, gbc);

            add(panel);

            btnRegister.addActionListener(e -> handleRegister());
            btnBack.addActionListener(e -> {
                new LoginForm().setVisible(true);
                dispose();
            });
        }

        private void handleRegister() {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String confirm  = new String(txtConfirm.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sva polja su obavezna.", "Greška", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Lozinke se ne podudaraju.", "Greška", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                File file = new File("korisnici.txt");
                if (file.exists()) {
                    List<String> lines = Files.readAllLines(file.toPath());
                    for (String line : lines) {
                        String[] parts = line.split(",");
                        if (parts.length > 0 && parts[0].trim().equals(username)) {
                            JOptionPane.showMessageDialog(this, "Korisničko ime već postoji.", "Greška", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
                FileWriter fw = new FileWriter("korisnici.txt", true);
                fw.write(username + "," + password + "\n");
                fw.close();
                JOptionPane.showMessageDialog(this, "Registracija uspešna!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
                new LoginForm().setVisible(true);
                dispose();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Greška pri čuvanju korisnika.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}