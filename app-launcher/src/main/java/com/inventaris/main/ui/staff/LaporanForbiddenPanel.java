package com.inventaris.main.ui.staff;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class LaporanForbiddenPanel extends JPanel {
    private final Runnable onBackToDashboard;

    public LaporanForbiddenPanel(Runnable onBackToDashboard) {
        this.onBackToDashboard = onBackToDashboard;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 30, 10, 30));

        // 1. Lock Icon
        JLabel lblIcon = new JLabel();
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        ImageIcon lockIconObj = null;
        try {
            lockIconObj = new ImageIcon(Objects.requireNonNull(getClass().getResource("/com/inventaris/main/ui/icons/403-lock icon.png")));
        } catch (Exception e) {
            System.err.println("Gagal memuat ikon 403-lock icon.png dari resource: " + e.getMessage());
            // Fallback to relative file path
            try {
                java.io.File file = new java.io.File("gambar/lock_icon.png");
                if (file.exists()) {
                    lockIconObj = new ImageIcon(file.getAbsolutePath());
                } else {
                    file = new java.io.File("D:/Kuliah - Semester 4/OOP/Final Project/InventarisBarang/gambar/lock_icon.png");
                    if (file.exists()) {
                        lockIconObj = new ImageIcon(file.getAbsolutePath());
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        if (lockIconObj != null) {
            lblIcon.setIcon(lockIconObj);
        } else {
            // Final fallback
            lblIcon.setText("🔒");
            lblIcon.setFont(new Font("Inter", Font.PLAIN, 36));
            lblIcon.setForeground(Color.decode("#0F172A"));
        }

        // 2. "403" Text (large serif font to match the image)
        JLabel lblCode = new JLabel("403");
        lblCode.setFont(new Font("Liberation Serif", Font.PLAIN, 72));
        lblCode.setForeground(Color.decode("#0F172A"));
        lblCode.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. "Akses Ditolak" Header
        JLabel lblTitle = new JLabel("Akses Ditolak");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(Color.decode("#0F172A"));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 4. Description Text
        JLabel lblDesc = new JLabel("<html><div style='text-align: center;'>Halaman ini terenkapsulasi khusus<br>untuk otorisasi tingkat Admin.<br>Kredensial operasional Anda tidak<br>memiliki hak akses ke modul ini.</div></html>");
        lblDesc.setFont(new Font("Inter", Font.PLAIN, 14));
        lblDesc.setForeground(Color.decode("#64748B")); // Slate 500
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 5. Button "Kembali ke Dashboard Staff"
        JButton btnBack = new JButton("Kembali ke Dashboard Staff");
        btnBack.setFont(new Font("Inter", Font.BOLD, 14));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(Color.decode("#0066CC")); // Primary Blue
        btnBack.putClientProperty("JButton.buttonType", "roundRect");
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Exact size matching the design (rounded button)
        btnBack.setMaximumSize(new Dimension(280, 47));
        btnBack.setPreferredSize(new Dimension(280, 47));
        btnBack.setMinimumSize(new Dimension(280, 47));

        btnBack.addActionListener(e -> {
            if (onBackToDashboard != null) {
                onBackToDashboard.run();
            }
        });

        // Add components with spacing matching the analyzed layout coordinates
        add(Box.createVerticalGlue());
        add(lblIcon);
        add(Box.createVerticalStrut(20));
        add(lblCode);
        add(Box.createVerticalStrut(25));
        add(lblTitle);
        add(Box.createVerticalStrut(20));
        add(lblDesc);
        add(Box.createVerticalStrut(39));
        add(btnBack);
        add(Box.createVerticalGlue());
    }
}
