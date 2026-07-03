package com.inventaris.main.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class ServerErrorPanel extends JPanel {
    private final JFrame parentFrame;
    private final Runnable onReload;

    public ServerErrorPanel(JFrame parentFrame, Runnable onReload) {
        this.parentFrame = parentFrame;
        this.onReload = onReload;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 30, 10, 30));

        // 1. System Error Icon
        JLabel lblIcon = new JLabel();
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        ImageIcon errorIconObj = null;
        try {
            errorIconObj = new ImageIcon(Objects.requireNonNull(getClass().getResource("/com/inventaris/main/ui/icons/500-System error icon.png")));
        } catch (Exception e) {
            System.err.println("Gagal memuat ikon 500-System error icon.png dari resource: " + e.getMessage());
            // Fallback to relative file path
            try {
                java.io.File file = new java.io.File("gambar/500-System error icon.png");
                if (file.exists()) {
                    errorIconObj = new ImageIcon(file.getAbsolutePath());
                } else {
                    file = new java.io.File("D:/Kuliah - Semester 4/OOP/Final Project/InventarisBarang/app-launcher/src/main/java/com/inventaris/main/ui/icons/500-System error icon.png");
                    if (file.exists()) {
                        errorIconObj = new ImageIcon(file.getAbsolutePath());
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        if (errorIconObj != null) {
            lblIcon.setIcon(errorIconObj);
        } else {
            // Final fallback
            lblIcon.setText("⚠️");
            lblIcon.setFont(new Font("Inter", Font.PLAIN, 36));
            lblIcon.setForeground(Color.decode("#DC2626"));
        }

        // 2. "500" Text (must use Liberation Serif as requested)
        JLabel lblCode = new JLabel("500");
        lblCode.setFont(new Font("Liberation Serif", Font.PLAIN, 72));
        lblCode.setForeground(Color.decode("#C83214")); // Red color for 500
        lblCode.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. "Kesalahan Sistem Server" Header
        JLabel lblTitle = new JLabel("Kesalahan Sistem Server");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(Color.decode("#0F172A"));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 4. Description Text
        JLabel lblDesc = new JLabel("<html><div style='text-align: center;'>Terjadi unhandled exception pada<br>pemrosesan database. Silakan<br>periksa koneksi atau hubungi<br>Administrator jaringan.</div></html>");
        lblDesc.setFont(new Font("Inter", Font.PLAIN, 14));
        lblDesc.setForeground(Color.decode("#64748B")); // Slate 500
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 5. Button "Coba Muat Ulang (Reload)"
        JButton btnReload = new JButton("\u21BB Coba Muat Ulang (Reload)");
        btnReload.setFont(new Font("Inter", Font.BOLD, 14));
        btnReload.setForeground(Color.WHITE);
        btnReload.setBackground(Color.decode("#0F172A")); // Dark Slate / Black
        btnReload.putClientProperty("JButton.buttonType", "roundRect");
        btnReload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReload.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Exact size matching the design (rounded button)
        btnReload.setMaximumSize(new Dimension(280, 55));
        btnReload.setPreferredSize(new Dimension(280, 55));
        btnReload.setMinimumSize(new Dimension(280, 55));

        btnReload.addActionListener(e -> {
            if (onReload != null) {
                // Kembalikan content pane ke panel utama awal sebelum me-reload
                // (untuk LoginFrame, kita simpan panelUtama di awal jika perlu, atau cukup jalankan reload)
                onReload.run();
            }
        });

        // Add components with spacing matching the analyzed layout coordinates
        add(Box.createVerticalGlue());
        add(lblIcon);
        add(Box.createVerticalStrut(30));
        add(lblCode);
        add(Box.createVerticalStrut(31));
        add(lblTitle);
        add(Box.createVerticalStrut(31));
        add(lblDesc);
        add(Box.createVerticalStrut(35));
        add(btnReload);
        add(Box.createVerticalGlue());
    }
}
