package com.inventaris.main.ui.admin;

import com.formdev.flatlaf.FlatClientProperties;
import com.inventaris.inventory.domain.Kategori;
import com.inventaris.inventory.service.InventoryService;
import com.inventaris.main.ui.components.BottomSheetOverlay;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.UUID;

public class FormKategoriPanel extends JPanel {
    private final Kategori existingKategori;
    private final InventoryService inventoryService;
    private final BottomSheetOverlay bottomSheetOverlay;
    private final Runnable refreshCallback;

    private JTextField txtName;

    public FormKategoriPanel(Kategori existingKategori, InventoryService inventoryService,
                             BottomSheetOverlay bottomSheetOverlay, Runnable refreshCallback) {
        this.existingKategori = existingKategori;
        this.inventoryService = inventoryService;
        this.bottomSheetOverlay = bottomSheetOverlay;
        this.refreshCallback = refreshCallback;

        initComponents();
    }

    private void initComponents() {
        boolean isEdit = existingKategori != null;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        // Header: Title & Close Icon
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblTitle = new JLabel(isEdit ? "Edit Category" : "Add New Category");
        lblTitle.setFont(new Font("Newsreader 16pt", Font.PLAIN, 24));
        lblTitle.setForeground(Color.decode("#111111"));

        JLabel lblClose = new JLabel("✕");
        lblClose.setFont(new Font("Inter", Font.PLAIN, 20));
        lblClose.setForeground(Color.GRAY);
        lblClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                bottomSheetOverlay.closeSheet();
            }
        });
        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblClose, BorderLayout.EAST);
        add(header);
        add(Box.createVerticalStrut(8));
        add(createSeparator());
        add(Box.createVerticalStrut(12));

        // Field 1: Nama Kategori
        JPanel pnlName = new JPanel(new BorderLayout());
        pnlName.setOpaque(false);
        JLabel lblName = new JLabel("<html>Nama Kategori <font color='red'>*</font></html>");
        lblName.setFont(new Font("Inter", Font.BOLD, 13));
        pnlName.add(lblName, BorderLayout.NORTH);

        txtName = new JTextField(isEdit ? existingKategori.getNama() : "");
        txtName.setFont(new Font("Inter", Font.PLAIN, 14));
        txtName.setPreferredSize(new Dimension(350, 40));
        txtName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtName.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        txtName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DCDCDC"), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        pnlName.add(txtName, BorderLayout.CENTER);
        add(pnlName);
        add(Box.createVerticalStrut(25));

        // Action Buttons Row
        JPanel pnlButtons = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlButtons.setOpaque(false);
        pnlButtons.setPreferredSize(new Dimension(350, 44));
        pnlButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        // Batal Button
        JPanel btnBatal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(Color.decode("#DCDCDC"));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                g2.dispose();
            }
        };
        btnBatal.setOpaque(false);
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lblBatal = new JLabel("Batal", SwingConstants.CENTER);
        lblBatal.setFont(new Font("Inter", Font.BOLD, 14));
        lblBatal.setForeground(Color.decode("#0D52D6"));
        btnBatal.add(lblBatal, BorderLayout.CENTER);
        btnBatal.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                bottomSheetOverlay.closeSheet();
            }
        });

        // Simpan Button
        JPanel btnSimpan = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#0D52D6"));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
            }
        };
        btnSimpan.setOpaque(false);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lblSimpan = new JLabel("Simpan", SwingConstants.CENTER);
        lblSimpan.setFont(new Font("Inter", Font.BOLD, 14));
        lblSimpan.setForeground(Color.WHITE);
        btnSimpan.add(lblSimpan, BorderLayout.CENTER);
        btnSimpan.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                String nama = txtName.getText().trim();
                if (nama.isEmpty()) {
                    JOptionPane.showMessageDialog(FormKategoriPanel.this, "Nama kategori wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    if (isEdit) {
                        Kategori updatedKategori = new Kategori(existingKategori.getId(), nama);
                        inventoryService.updateKategori(updatedKategori);
                    } else {
                        String newId = UUID.randomUUID().toString();
                        Kategori newKategori = new Kategori(newId, nama);
                        inventoryService.saveKategori(newKategori);
                    }

                    // Run refresh callback to update dashboard & kelola view
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }

                    bottomSheetOverlay.closeSheet();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(FormKategoriPanel.this, "Gagal menyimpan kategori ke database: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pnlButtons.add(btnBatal);
        pnlButtons.add(btnSimpan);
        add(pnlButtons);
    }

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(Color.decode("#F0F0F0"));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, sep.getPreferredSize().height));
        return sep;
    }
}
