package com.inventaris.main.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConfirmDialogs {

    /**
     * Create delete confirmation dialog content panel.
     * Matches the design in D:\Kuliah - Semester 4\OOP\Final Project\InventarisBarang\gambar\admin-popup-alert-hapus.png
     */
    public static JPanel createDeleteConfirmationDialog(String titleText, String descText, Runnable onConfirm, Runnable onCancel) {
        JPanel dialog = new JPanel(new BorderLayout());
        dialog.setBackground(Color.WHITE);
        dialog.setOpaque(false);

        // NORTH: Header with light red background and warning badge
        JPanel headerPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#FFF5F5"));
                // Clip rounded corners for the top of the header panel to match the outer dialog
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 20, 20);
                g2.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(320, 110));

        // Warning Badge (Light red circle)
        JPanel warningCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#FEE2E2"));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        warningCircle.setOpaque(false);
        warningCircle.setPreferredSize(new Dimension(64, 64));

        JLabel lblWarning = new JLabel("⚠");
        lblWarning.setFont(new Font("Inter", Font.BOLD, 30));
        lblWarning.setForeground(Color.decode("#DC2626"));
        warningCircle.add(lblWarning);

        headerPanel.add(warningCircle);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // CENTER: Content Section with Title & Description
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 25, 10, 25));

        JLabel lblTitle = new JLabel(titleText);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 17));
        lblTitle.setForeground(Color.decode("#111827"));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblTitle);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextPane txtDesc = new JTextPane();
        txtDesc.setContentType("text/html");
        txtDesc.setText("<html><center style='font-family:Inter;font-size:12px;color:#4B5563;line-height:1.4;'>" + descText + "</center></html>");
        txtDesc.setEditable(false);
        txtDesc.setOpaque(false);
        txtDesc.setBorder(null);
        txtDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(txtDesc);

        dialog.add(contentPanel, BorderLayout.CENTER);

        // SOUTH: Footer with light gray/lavender background and action buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#F9FAFB"));
                // Clip rounded corners for the bottom of the footer
                g2.fillRoundRect(0, -20, getWidth(), getHeight() + 20, 20, 20);
                g2.dispose();
            }
        };
        footerPanel.setOpaque(false);
        footerPanel.setPreferredSize(new Dimension(320, 65));
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#E5E7EB")));

        JButton btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Inter", Font.BOLD, 13));
        btnBatal.setForeground(Color.decode("#1F2937"));
        btnBatal.setBackground(Color.WHITE);
        btnBatal.putClientProperty("JButton.buttonType", "roundRect");
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.setPreferredSize(new Dimension(85, 36));
        btnBatal.addActionListener(e -> onCancel.run());

        JButton btnHapus = new JButton("Hapus");
        btnHapus.setFont(new Font("Inter", Font.BOLD, 13));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.setBackground(Color.decode("#DC2626"));
        btnHapus.putClientProperty("JButton.buttonType", "roundRect");
        btnHapus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapus.setPreferredSize(new Dimension(85, 36));
        btnHapus.addActionListener(e -> onConfirm.run());

        footerPanel.add(btnBatal);
        footerPanel.add(btnHapus);
        dialog.add(footerPanel, BorderLayout.SOUTH);

        return dialog;
    }

    /**
     * Create save confirmation dialog content panel.
     * Matches the design in D:\Kuliah - Semester 4\OOP\Final Project\InventarisBarang\gambar\staff-popup-alert-simpan.png
     */
    public static JPanel createSaveConfirmationDialog(String titleText, String descText, Runnable onConfirm, Runnable onCancel) {
        JPanel dialog = new JPanel(new BorderLayout());
        dialog.setOpaque(false);
        dialog.setBorder(new EmptyBorder(25, 25, 20, 25));

        // Center panel to display left-aligned Title and Question
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel(titleText);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 18));
        lblTitle.setForeground(Color.decode("#111827"));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblTitle);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblDesc = new JLabel("<html><body style='width: 250px; font-family: Inter; font-size: 13px; color: #4B5563;'>" + descText + "</body></html>");
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblDesc);

        dialog.add(contentPanel, BorderLayout.CENTER);

        // Bottom panel for action buttons aligned to the right
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Flat button style for BATAL
        JButton btnBatal = new JButton("BATAL");
        btnBatal.setFont(new Font("Inter", Font.BOLD, 12));
        btnBatal.setForeground(Color.decode("#0A47B8"));
        btnBatal.setBackground(Color.WHITE);
        btnBatal.setBorderPainted(false);
        btnBatal.setContentAreaFilled(false);
        btnBatal.setFocusPainted(false);
        btnBatal.setOpaque(false);
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.addActionListener(e -> onCancel.run());

        JButton btnYa = new JButton("YA, SIMPAN");
        btnYa.setFont(new Font("Inter", Font.BOLD, 12));
        btnYa.setForeground(Color.WHITE);
        btnYa.setBackground(Color.decode("#003EA8"));
        btnYa.putClientProperty("JButton.buttonType", "roundRect");
        btnYa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnYa.setPreferredSize(new Dimension(110, 36));
        btnYa.addActionListener(e -> onConfirm.run());

        footerPanel.add(btnBatal);
        footerPanel.add(btnYa);
        dialog.add(footerPanel, BorderLayout.SOUTH);

        return dialog;
    }

    /**
     * Create 404 Not Found dialog content panel for deleted items race condition.
     */
    public static JPanel createNotFoundErrorDialog(Runnable onReload) {
        JPanel dialog = new JPanel(new BorderLayout());
        dialog.setOpaque(false);
        dialog.setBorder(new EmptyBorder(25, 25, 20, 25));

        // Center panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 0. Icon Barang
        JLabel lblIcon = new JLabel();
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            java.net.URL imgUrl = ConfirmDialogs.class.getResource("/com/inventaris/main/ui/icons/icon_barang.png");
            if (imgUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                Image scaledImg = originalIcon.getImage().getScaledInstance(38, 38, Image.SCALE_SMOOTH);
                lblIcon.setIcon(new ImageIcon(scaledImg));
            } else {
                lblIcon.setText("📦");
                lblIcon.setFont(new Font("Inter", Font.PLAIN, 48));
            }
        } catch (Exception e) {
            lblIcon.setText("📦");
            lblIcon.setFont(new Font("Inter", Font.PLAIN, 48));
        }
        contentPanel.add(lblIcon);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // 1. "404" Text (using Liberation Serif)
        JLabel lblCode = new JLabel("404");
        lblCode.setFont(new Font("Liberation Serif", Font.BOLD, 44));
        lblCode.setForeground(Color.decode("#F59E0B")); // Amber color for 404
        lblCode.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. Title
        JLabel lblTitle = new JLabel("Barang Tidak Ditemukan");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 17));
        lblTitle.setForeground(Color.decode("#111827"));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(lblCode);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(lblTitle);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 3. Desc
        JLabel lblDesc = new JLabel("<html><center style='width: 250px; font-family: Inter; font-size: 11px; color: #4B5563;'>Barang yang Anda pilih sudah tidak terdaftar (mungkin telah dihapus Admin).</center></html>");
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblDesc);

        dialog.add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnReload = new JButton("TUTUP & MUAT ULANG");
        btnReload.setFont(new Font("Inter", Font.BOLD, 12));
        btnReload.setForeground(Color.WHITE);
        btnReload.setBackground(Color.decode("#003EA8"));
        btnReload.putClientProperty("JButton.buttonType", "roundRect");
        btnReload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReload.setPreferredSize(new Dimension(170, 36));
        btnReload.addActionListener(e -> onReload.run());

        footerPanel.add(btnReload);
        dialog.add(footerPanel, BorderLayout.SOUTH);

        return dialog;
    }
}
