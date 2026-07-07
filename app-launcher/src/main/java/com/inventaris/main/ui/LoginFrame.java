package com.inventaris.main.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.inventaris.auth.domain.MenuLauncher;
import com.inventaris.auth.domain.Role;
import com.inventaris.auth.domain.User;
import com.inventaris.auth.domain.Admin;
import com.inventaris.auth.domain.Staff;
import com.inventaris.auth.repository.UserRepository;
import com.inventaris.auth.service.AuthService;
import com.inventaris.core.exception.AuthException;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class LoginFrame extends JFrame {
    private JPanel panelUtama;
    private JLabel lblLogo;
    private JLabel lblSubtitile;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblJudul;
    private JButton btnLogin;

    private final AuthService authService;

    public LoginFrame() {
        this.authService = new AuthService(new UserRepository());
        setContentPane(panelUtama);
        setTitle("Sistem Inventaris - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Daftarkan MenuLauncher secara polimorfik untuk menghindari circular dependency
        User.setMenuLauncher(new MenuLauncher() {
            @Override
            public void launchAdminMenu(Admin admin) {
                new DashboardAdmin().setVisible(true);
            }

            @Override
            public void launchStaffMenu(Staff staff) {
                new DashboardStaff(staff).setVisible(true);
            }
        });

        applyFlatLafStyling();
        initListeners();

        setSize(400, 750);
        setLocationRelativeTo(null);
    }

    private void applyFlatLafStyling() {
        panelUtama.setBackground(Color.white);
        lblJudul.setFont(new Font("Newsreader 16pt", Font.BOLD, 32));
        lblSubtitile.setFont(new Font("inter", Font.PLAIN, 14));
        txtUsername.setFont(new Font("inter", Font.PLAIN, 14));
        txtPassword.setFont(new Font("inter", Font.PLAIN, 14));

        try {
            ImageIcon rawLogo = new ImageIcon(Objects.requireNonNull(getClass().getResource("/com/inventaris/main/ui/icons/logo.png")));
            Image scaledLogo = rawLogo.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(scaledLogo));
            lblLogo.setText("");
        } catch (Exception e) {
            System.err.println("Gagal memuat ikon logo.png: " + e.getMessage());
        }

        txtUsername.setBorder(UIManager.getBorder("TextField.border"));
        txtUsername.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        try {
            ImageIcon userIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/com/inventaris/main/ui/icons/user.png")));
            txtUsername.putClientProperty("JTextField.leadingIcon", userIcon);
        } catch (Exception e) {
            System.err.println("Gagal memuat ikon user.png: " + e.getMessage());
        }
        txtUsername.putClientProperty("JTextField.placeholderText", "Masukkan username Anda");
        txtUsername.putClientProperty(FlatClientProperties.STYLE, "arc: 12;" + "margin: 6,17,6,12;");

        txtPassword.setBorder(UIManager.getBorder("TextField.border"));
        try {
            ImageIcon lockIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/com/inventaris/main/ui/icons/lock.png")));
            txtPassword.putClientProperty("JTextField.leadingIcon", lockIcon);
        } catch (Exception e) {
            System.err.println("Gagal memuat ikon lock.png: " + e.getMessage());
        }
        txtPassword.putClientProperty("JTextField.placeholderText", "Masukkan password Anda");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "arc: 12;" + "margin: 6,14,6,12;" + "showRevealButton: true;");

        btnLogin.setBackground(new Color(0, 82, 204)); // Biru pekat
        btnLogin.setForeground(Color.WHITE);
        btnLogin.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12;" +
                        "margin: 10,20,10,20;" +
                        "borderWidth: 0;" +
                        "focusWidth: 0;"); // Menghilangkan garis border luar bawaan Jbutton
    }

    private void initListeners() {
        btnLogin.addActionListener(e -> handleLogin());

        txtUsername.addActionListener(e -> handleLogin());
        txtPassword.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try {
            User user = authService.login(username, password);
            JOptionPane.showMessageDialog(this,
                    "Login Berhasil!\nSelamat datang, " + user.getName() + " (" + user.getRole() + ")",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);

            user.tampilkanMenu();

            this.dispose();

        } catch (AuthException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Gagal", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan sistem: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
