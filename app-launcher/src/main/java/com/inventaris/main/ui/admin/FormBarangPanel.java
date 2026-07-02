package com.inventaris.main.ui.admin;

import com.formdev.flatlaf.FlatClientProperties;
import com.inventaris.inventory.domain.Barang;
import com.inventaris.inventory.domain.Kategori;
import com.inventaris.inventory.service.InventoryService;
import com.inventaris.main.ui.components.BottomSheetOverlay;
import com.inventaris.auth.domain.Session;
import com.inventaris.auth.domain.User;
import com.inventaris.transaction.service.TransactionService;
import com.inventaris.transaction.repository.TransaksiRepository;
import com.inventaris.inventory.repository.BarangRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class FormBarangPanel extends JPanel {
    private final Barang existingBarang;
    private final InventoryService inventoryService;
    private final BottomSheetOverlay bottomSheetOverlay;
    private final Runnable refreshCallback;

    private JTextField txtName;
    private JComboBox<String> cmbKat;
    private JTextField txtStok;
    private JTextArea taDesc;

    public FormBarangPanel(Barang existingBarang, InventoryService inventoryService,
                           BottomSheetOverlay bottomSheetOverlay, Runnable refreshCallback) {
        this.existingBarang = existingBarang;
        this.inventoryService = inventoryService;
        this.bottomSheetOverlay = bottomSheetOverlay;
        this.refreshCallback = refreshCallback;

        initComponents();
    }

    private void initComponents() {
        boolean isEdit = existingBarang != null;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        // Header: Title & Close Icon
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblTitle = new JLabel(isEdit ? "Edit Item" : "Add New Item");
        lblTitle.setFont(new Font("Georgia", Font.PLAIN, 24));
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

        // Field 1: Nama Barang
        JPanel pnlName = new JPanel(new BorderLayout());
        pnlName.setOpaque(false);
        JLabel lblName = new JLabel("<html>Nama Barang <font color='red'>*</font></html>");
        lblName.setFont(new Font("Inter", Font.BOLD, 13));
        pnlName.add(lblName, BorderLayout.NORTH);

        txtName = new JTextField(isEdit ? existingBarang.getNama() : "");
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
        add(Box.createVerticalStrut(15));

        // Field 2: Kategori
        JPanel pnlKat = new JPanel(new BorderLayout());
        pnlKat.setOpaque(false);
        JLabel lblKatHeader = new JLabel("Kategori");
        lblKatHeader.setFont(new Font("Inter", Font.BOLD, 13));
        pnlKat.add(lblKatHeader, BorderLayout.NORTH);

        cmbKat = new JComboBox<>();
        cmbKat.setFont(new Font("Inter", Font.PLAIN, 14));
        cmbKat.setPreferredSize(new Dimension(350, 40));
        cmbKat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cmbKat.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");

        try {
            List<Kategori> kats = inventoryService.getAllKategori();
            for (Kategori k : kats) {
                cmbKat.addItem(k.getNama());
            }
            if (isEdit && existingBarang.getKategori() != null) {
                cmbKat.setSelectedItem(existingBarang.getKategori().getNama());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pnlKat.add(cmbKat, BorderLayout.CENTER);
        add(pnlKat);
        add(Box.createVerticalStrut(15));

        // Field 3: Stok Awal
        JPanel pnlStok = new JPanel(new BorderLayout());
        pnlStok.setOpaque(false);
        JLabel lblStokHeader = new JLabel(isEdit ? "Stok" : "Stok Awal");
        lblStokHeader.setFont(new Font("Inter", Font.BOLD, 13));
        pnlStok.add(lblStokHeader, BorderLayout.NORTH);

        txtStok = new JTextField(isEdit ? String.valueOf(existingBarang.getStok()) : "0");
        txtStok.setFont(new Font("Inter", Font.PLAIN, 14));
        txtStok.setPreferredSize(new Dimension(350, 40));
        txtStok.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtStok.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        txtStok.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DCDCDC"), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        pnlStok.add(txtStok, BorderLayout.CENTER);
        add(pnlStok);
        add(Box.createVerticalStrut(15));

        // Field 4: Deskripsi
        JPanel pnlDesc = new JPanel(new BorderLayout());
        pnlDesc.setOpaque(false);
        JLabel lblDescHeader = new JLabel("Deskripsi");
        lblDescHeader.setFont(new Font("Inter", Font.BOLD, 13));
        pnlDesc.add(lblDescHeader, BorderLayout.NORTH);

        taDesc = new JTextArea(isEdit ? existingBarang.getDeskripsi() : "");
        taDesc.setFont(new Font("Inter", Font.PLAIN, 13));
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);
        taDesc.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scrollDesc = new JScrollPane(taDesc);
        scrollDesc.setPreferredSize(new Dimension(350, 70));
        scrollDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        scrollDesc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DCDCDC"), 1, true),
                new EmptyBorder(2, 2, 2, 2)
        ));
        pnlDesc.add(scrollDesc, BorderLayout.CENTER);
        add(pnlDesc);
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
                    JOptionPane.showMessageDialog(FormBarangPanel.this, "Nama barang wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int stok = 0;
                try {
                    stok = Integer.parseInt(txtStok.getText().trim());
                    if (stok < 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(FormBarangPanel.this, "Stok harus berupa angka positif!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String selectedKatName = (String) cmbKat.getSelectedItem();
                Kategori selectedKat = null;
                try {
                    List<Kategori> kats = inventoryService.getAllKategori();
                    for (Kategori k : kats) {
                        if (k.getNama().equals(selectedKatName)) {
                            selectedKat = k;
                            break;
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                if (selectedKat == null) {
                    JOptionPane.showMessageDialog(FormBarangPanel.this, "Pilih kategori terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String desc = taDesc.getText().trim();

                try {
                    // Ambil user dari Session
                    User admin = Session.getLoggedInUser();
                    String adminId = admin != null ? admin.getId() : "SYSTEM";
                    String adminName = admin != null ? admin.getName() : "SYSTEM";
                    String adminRole = admin != null ? admin.getRole().toString() : "ADMIN";

                    if (isEdit) {
                        // Bandingkan perubahan field untuk log detail aktivitas
                        List<String> editedFields = new java.util.ArrayList<>();
                        
                        if (!existingBarang.getNama().equals(nama)) {
                            editedFields.add("Nama ('" + existingBarang.getNama() + "' -> '" + nama + "')");
                        }
                        if (existingBarang.getKategori() == null || !existingBarang.getKategori().getId().equals(selectedKat.getId())) {
                            String oldKat = existingBarang.getKategori() != null ? existingBarang.getKategori().getNama() : "null";
                            editedFields.add("Kategori ('" + oldKat + "' -> '" + selectedKat.getNama() + "')");
                        }
                        String oldDesc = existingBarang.getDeskripsi() != null ? existingBarang.getDeskripsi() : "";
                        if (!oldDesc.equals(desc)) {
                            editedFields.add("Deskripsi");
                        }
                        
                        int oldStok = existingBarang.getStok();
                        if (oldStok != stok) {
                            editedFields.add("Stok (" + oldStok + " -> " + stok + ")");
                        }
                        
                        // Simpan perubahan barang ke database (pertahankan oldStok dahulu)
                        Barang updatedBarang = new Barang(existingBarang.getId(), nama, selectedKat, oldStok, desc);
                        inventoryService.updateBarang(updatedBarang);
                        
                        // Jika stok berubah, buat transaksi penyesuaian otomatis
                        if (oldStok != stok) {
                            int selisih = stok - oldStok;
                            TransactionService txService = new TransactionService(
                                new TransaksiRepository(), 
                                new BarangRepository()
                            );
                            
                            if (selisih > 0) {
                                com.inventaris.transaction.domain.BarangMasuk adjustTx = 
                                    new com.inventaris.transaction.domain.BarangMasuk(updatedBarang, selisih, admin, "Penyesuaian stok manual (Edit Barang oleh Admin)");
                                txService.executeTransaction(adjustTx);
                            } else {
                                com.inventaris.transaction.domain.BarangKeluar adjustTx = 
                                    new com.inventaris.transaction.domain.BarangKeluar(updatedBarang, -selisih, admin, "Penyesuaian stok manual (Edit Barang oleh Admin)");
                                txService.executeTransaction(adjustTx);
                            }
                        }
                        
                        // Log aktivitas detail perubahan field
                        String logDetail = "Mengedit barang: " + nama + " (ID: " + existingBarang.getId() + ").";
                        if (!editedFields.isEmpty()) {
                            logDetail += " Field yang diubah: " + String.join(", ", editedFields);
                        } else {
                            logDetail += " Tidak ada perubahan field.";
                        }
                        
                        com.inventaris.core.util.ActivityLogger.log(
                            adminId,
                            adminName,
                            adminRole,
                            "EDIT_BARANG",
                            logDetail
                        );
                    } else {
                        String newId = UUID.randomUUID().toString();
                        Barang newBarang = new Barang(newId, nama, selectedKat, 0, desc); // Mulai dari 0 agar saat ditambah transaksi mutasinya valid
                        inventoryService.saveBarang(newBarang);
                        
                        // Jika ada stok awal > 0, buat transaksi penyesuaian awal
                        if (stok > 0) {
                            com.inventaris.transaction.domain.BarangMasuk initTx = 
                                new com.inventaris.transaction.domain.BarangMasuk(newBarang, stok, admin, "Stok awal barang baru oleh Admin");
                            TransactionService txService = new TransactionService(
                                new TransaksiRepository(), 
                                new BarangRepository()
                            );
                            txService.executeTransaction(initTx);
                        }
                        
                        // Log aktivitas
                        com.inventaris.core.util.ActivityLogger.log(
                            adminId,
                            adminName,
                            adminRole,
                            "TAMBAH_BARANG",
                            "Menambah barang baru: " + nama + " (ID: " + newId + ") dengan Kategori: " + selectedKat.getNama() + " dan Stok Awal: " + stok
                        );
                    }

                    // Run refresh callback to update dashboard & kelola view
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }

                    bottomSheetOverlay.closeSheet();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(FormBarangPanel.this, "Gagal menyimpan barang: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
