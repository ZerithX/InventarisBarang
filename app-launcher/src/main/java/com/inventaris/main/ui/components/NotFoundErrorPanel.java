package com.inventaris.main.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class NotFoundErrorPanel extends JPanel {
    private final BottomSheetOverlay bottomSheetOverlay;
    private final Runnable onReload;

    public NotFoundErrorPanel(BottomSheetOverlay bottomSheetOverlay, Runnable onReload) {
        this.bottomSheetOverlay = bottomSheetOverlay;
        this.onReload = onReload;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 30, 10, 30));

        // 1. Icon (Fallback text for 404)
        JLabel lblIcon = new JLabel();
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIcon.setText("📦❓");
        lblIcon.setFont(new Font("Inter", Font.PLAIN, 48));
        lblIcon.setForeground(Color.decode("#64748B"));

        // 2. "404" Text (using Liberation Serif)
        JLabel lblCode = new JLabel("404");
        lblCode.setFont(new Font("Liberation Serif", Font.PLAIN, 72));
        lblCode.setForeground(Color.decode("#F59E0B")); // Amber color for 404
        lblCode.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. "Barang Tidak Ditemukan" Header
        JLabel lblTitle = new JLabel("Barang Tidak Ditemukan");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(Color.decode("#0F172A"));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 4. Description Text
        JLabel lblDesc = new JLabel("<html><div style='text-align: center;'>Barang yang Anda pilih sudah tidak<br>terdaftar dalam sistem (mungkin telah dihapus).<br>Silakan muat ulang data barang.</div></html>");
        lblDesc.setFont(new Font("Inter", Font.PLAIN, 14));
        lblDesc.setForeground(Color.decode("#64748B")); // Slate 500
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 5. Button "Tutup & Muat Ulang"
        JButton btnReload = new JButton("Tutup & Muat Ulang");
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
            if (bottomSheetOverlay != null) {
                bottomSheetOverlay.closeSheet();
            }
            if (onReload != null) {
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
