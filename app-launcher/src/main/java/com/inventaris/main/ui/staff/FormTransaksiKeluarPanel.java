package com.inventaris.main.ui.staff;

import com.inventaris.auth.domain.User;
import com.inventaris.inventory.domain.Barang;
import com.inventaris.inventory.service.InventoryService;
import com.inventaris.transaction.domain.BarangKeluar;
import com.inventaris.transaction.service.TransactionService;
import com.inventaris.main.ui.components.BottomSheetOverlay;
import com.inventaris.main.ui.components.ConfirmDialogs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FormTransaksiKeluarPanel extends JPanel {
    private final User staffUser;
    private final InventoryService inventoryService;
    private final TransactionService transactionService;
    private final BottomSheetOverlay bottomSheetOverlay;
    private final Runnable refreshCallback;

    private JComboBox<String> cbBarang;
    private JPanel infoStokBox;
    private JLabel lblStokSaatIni;

    private JTextField txtJumlah;
    private JLabel lblWarning; // Warning: Stok tidak mencukupi.
    private JTextField txtTanggal;
    private JTextArea txtKeterangan;
    private JButton btnSimpan;

    private List<Barang> barangList = new ArrayList<>();
    private int currentStok = 0;

    public FormTransaksiKeluarPanel(User staffUser, InventoryService inventoryService, TransactionService transactionService,
                                    BottomSheetOverlay bottomSheetOverlay, Runnable refreshCallback) {
        this.staffUser = staffUser;
        this.inventoryService = inventoryService;
        this.transactionService = transactionService;
        this.bottomSheetOverlay = bottomSheetOverlay;
        this.refreshCallback = refreshCallback;

        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header Panel (Centered Title & Balanced Back Button)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#EBEBEB")));
        headerPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel lblBack = new JLabel("<html><b style='font-size:20px;color:#1E293B;'>&larr;</b></html>");
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.setBorder(new EmptyBorder(0, 20, 0, 0));
        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                bottomSheetOverlay.closeSheet();
            }
        });

        JLabel lblTitle = new JLabel("Transaksi Barang Keluar", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 18));
        lblTitle.setForeground(Color.decode("#1E293B"));

        // Placeholder untuk keseimbangan layout agar tulisan center secara presisi
        JLabel lblPlaceholder = new JLabel("<html><b style='font-size:20px;color:white;'>&larr;</b></html>");
        lblPlaceholder.setBorder(new EmptyBorder(0, 0, 0, 20));

        headerPanel.add(lblBack, BorderLayout.WEST);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(lblPlaceholder, BorderLayout.EAST);

        // Body Panel
        JPanel bodyPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                Container parent = getParent();
                if (parent instanceof JViewport) {
                    d.width = parent.getWidth();
                }
                return d;
            }
        };
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. Pilih Barang
        JLabel lblPilihBarang = new JLabel("PILIH BARANG");
        lblPilihBarang.setFont(new Font("Inter", Font.BOLD, 11));
        lblPilihBarang.setForeground(Color.GRAY);
        lblPilihBarang.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(lblPilihBarang);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        cbBarang = new JComboBox<>();
        cbBarang.setFont(new Font("Inter", Font.PLAIN, 14));
        cbBarang.putClientProperty("JComponent.roundRect", true);
        cbBarang.setMaximumSize(new Dimension(800, 40));
        cbBarang.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        cbBarang.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbBarang.setBackground(Color.WHITE);
        cbBarang.addActionListener(e -> onBarangSelectionChanged());
        bodyPanel.add(cbBarang);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // 2. Info Stok Box (Warning style: reddish/orange tone if stock is low or standard)
        infoStokBox = new JPanel();
        infoStokBox.setLayout(new BoxLayout(infoStokBox, BoxLayout.Y_AXIS));
        infoStokBox.setBackground(Color.decode("#FFF5F5")); // Light Red/Orange 50
        infoStokBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#FED7D7"), 1, true), // Red 200 border
                new EmptyBorder(10, 15, 10, 15)
        ));
        infoStokBox.setMaximumSize(new Dimension(800, 70));
        infoStokBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblStokHeader = new JLabel("ⓘ  STOK SAAT INI");
        lblStokHeader.setFont(new Font("Inter", Font.BOLD, 11));
        lblStokHeader.setForeground(Color.decode("#9B2C2C")); // Red/Orange 800
        lblStokHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblStokSaatIni = new JLabel("0 Unit");
        lblStokSaatIni.setFont(new Font("Inter", Font.BOLD, 18));
        lblStokSaatIni.setForeground(Color.decode("#9B2C2C"));
        lblStokSaatIni.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoStokBox.add(lblStokHeader);
        infoStokBox.add(Box.createRigidArea(new Dimension(0, 4)));
        infoStokBox.add(lblStokSaatIni);
        bodyPanel.add(infoStokBox);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // 3. Jumlah Barang
        JLabel lblJumlah = new JLabel("JUMLAH BARANG");
        lblJumlah.setFont(new Font("Inter", Font.BOLD, 11));
        lblJumlah.setForeground(Color.GRAY);
        lblJumlah.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(lblJumlah);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        txtJumlah = new JTextField("0");
        txtJumlah.setFont(new Font("Inter", Font.PLAIN, 14));
        txtJumlah.putClientProperty("JComponent.roundRect", true);
        txtJumlah.setMaximumSize(new Dimension(800, 40));
        txtJumlah.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        txtJumlah.setMargin(new Insets(5, 10, 5, 10));
        txtJumlah.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Listener for input validation (exceeds stock)
        txtJumlah.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateInput(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateInput(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateInput(); }
        });
        
        bodyPanel.add(txtJumlah);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        // Warning Label: Stok tidak mencukupi (hidden by default)
        lblWarning = new JLabel("<html><b>&#x24D8;</b> Stok tidak mencukupi. Transaksi tidak dapat diproses!</html>");
        lblWarning.setFont(new Font("Inter", Font.PLAIN, 12));
        lblWarning.setForeground(Color.decode("#E53E3E")); // Red 600
        lblWarning.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblWarning.setVisible(false);
        bodyPanel.add(lblWarning);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // 4. Tanggal Transaksi
        JLabel lblTanggal = new JLabel("TANGGAL TRANSAKSI");
        lblTanggal.setFont(new Font("Inter", Font.BOLD, 11));
        lblTanggal.setForeground(Color.GRAY);
        lblTanggal.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(lblTanggal);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        txtTanggal = new JTextField();
        txtTanggal.setFont(new Font("Inter", Font.PLAIN, 14));
        txtTanggal.putClientProperty("JComponent.roundRect", true);
        txtTanggal.setMaximumSize(new Dimension(800, 40));
        txtTanggal.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        txtTanggal.setMargin(new Insets(5, 10, 5, 10));
        txtTanggal.setEditable(false);
        txtTanggal.setBackground(Color.decode("#F8F9FA"));
        txtTanggal.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("id", "ID")));
        txtTanggal.setText(formattedDate);
        bodyPanel.add(txtTanggal);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // 5. Keterangan (opsional)
        JLabel lblKeterangan = new JLabel("KETERANGAN (opsional)");
        lblKeterangan.setFont(new Font("Inter", Font.BOLD, 11));
        lblKeterangan.setForeground(Color.GRAY);
        lblKeterangan.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(lblKeterangan);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        txtKeterangan = new JTextArea(3, 20);
        txtKeterangan.setFont(new Font("Inter", Font.PLAIN, 13));
        txtKeterangan.setLineWrap(true);
        txtKeterangan.setWrapStyleWord(true);
        txtKeterangan.putClientProperty("JTextField.placeholderText", "Tambahkan catatan transaksi...");
        JScrollPane ketScroll = new JScrollPane(txtKeterangan);
        ketScroll.setBorder(BorderFactory.createLineBorder(Color.decode("#CBD5E1"), 1, true));
        ketScroll.setMaximumSize(new Dimension(800, 75));
        ketScroll.setPreferredSize(new Dimension(Integer.MAX_VALUE, 75));
        ketScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(ketScroll);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Bottom Action Button (Simpan Transaksi)
        btnSimpan = new JButton("Simpan Transaksi");
        btnSimpan.setFont(new Font("Inter", Font.BOLD, 14));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setBackground(Color.decode("#0D52D6"));
        btnSimpan.putClientProperty("JButton.buttonType", "roundRect");
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.setMaximumSize(new Dimension(800, 45));
        btnSimpan.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        btnSimpan.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSimpan.addActionListener(e -> simpanTransaksi());
        bodyPanel.add(btnSimpan);

        JScrollPane scrollPane = new JScrollPane(bodyPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Forward mouse wheel events from text area to parent scroll pane to prevent scroll trap/lag
        java.awt.event.MouseWheelListener forwardingListener = e -> {
            if (scrollPane != null) {
                scrollPane.dispatchEvent(javax.swing.SwingUtilities.convertMouseEvent(e.getComponent(), e, scrollPane));
            }
        };
        ketScroll.addMouseWheelListener(forwardingListener);
        txtKeterangan.addMouseWheelListener(forwardingListener);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData() {
        cbBarang.removeAllItems();
        cbBarang.addItem("pilih barang...");

        try {
            barangList = inventoryService.getAllBarang();
            for (Barang b : barangList) {
                cbBarang.addItem(b.getNama());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onBarangSelectionChanged() {
        int idx = cbBarang.getSelectedIndex();
        if (idx <= 0) {
            currentStok = 0;
            lblStokSaatIni.setText("0 Unit");
        } else {
            Barang b = barangList.get(idx - 1);
            currentStok = b.getStok();
            lblStokSaatIni.setText(currentStok + " Unit");
        }
        validateInput();
    }

    private void validateInput() {
        if (txtJumlah == null) return;
        String valStr = txtJumlah.getText().trim();
        if (valStr.isEmpty() || valStr.equals("0")) {
            txtJumlah.putClientProperty("JComponent.outline", null);
            lblWarning.setVisible(false);
            btnSimpan.setEnabled(true);
            return;
        }

        try {
            int inputVal = Integer.parseInt(valStr);
            if (inputVal > currentStok) {
                // Set outline error (merah)
                txtJumlah.putClientProperty("JComponent.outline", "error");
                lblWarning.setVisible(true);
                btnSimpan.setEnabled(false);
            } else {
                txtJumlah.putClientProperty("JComponent.outline", null);
                lblWarning.setVisible(false);
                btnSimpan.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            // Outline error untuk input bukan angka
            txtJumlah.putClientProperty("JComponent.outline", "error");
            lblWarning.setVisible(false);
            btnSimpan.setEnabled(false);
        }
    }

    private void simpanTransaksi() {
        int selectionIndex = cbBarang.getSelectedIndex();
        if (selectionIndex <= 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih barang terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int jumlah;
        try {
            jumlah = Integer.parseInt(txtJumlah.getText().trim());
            if (jumlah <= 0) {
                throw new NumberFormatException();
            }
            if (jumlah > currentStok) {
                JOptionPane.showMessageDialog(this, "Transaksi tidak dapat disimpan karena stok tidak mencukupi!", "Transaksi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            txtJumlah.putClientProperty("JComponent.outline", "error");
            JOptionPane.showMessageDialog(this, "Jumlah barang harus berupa angka positif!", "Format Salah", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tampilkan Popup Konfirmasi Simpan Transaksi
        JPanel confirmPanel = ConfirmDialogs.createSaveConfirmationDialog(
            "Simpan Transaksi",
            "Apakah Anda yakin data transaksi sudah sesuai?",
            () -> {
                bottomSheetOverlay.closeDialog();
                prosesSimpanTransaksi(selectionIndex, jumlah);
            },
            () -> {
                bottomSheetOverlay.closeDialog();
            }
        );
        bottomSheetOverlay.openDialog(confirmPanel, 340, 180);
    }

    private void prosesSimpanTransaksi(int selectionIndex, int jumlah) {
        Barang targetBarang = barangList.get(selectionIndex - 1);
        String keteranganText = txtKeterangan.getText().trim();
        BarangKeluar transaksi = new BarangKeluar(targetBarang, jumlah, staffUser, keteranganText);

        try {
            transactionService.executeTransaction(transaksi);
            
            // Log aktivitas staff input barang keluar
            com.inventaris.core.util.ActivityLogger.log(
                staffUser.getId(),
                staffUser.getName(),
                "STAFF",
                "TRANSAKSI_KELUAR",
                "Menginput transaksi barang keluar: " + targetBarang.getNama() + " sejumlah " + jumlah + " unit. Keterangan: " + keteranganText
            );

            if (refreshCallback != null) {
                refreshCallback.run();
            }
            bottomSheetOverlay.closeSheet();
        } catch (Exception e) {
            boolean isDeleted = false;
            try {
                java.util.Optional<com.inventaris.inventory.domain.Barang> checkB = 
                    new com.inventaris.inventory.repository.BarangRepository().findById(targetBarang.getId());
                if (checkB.isEmpty()) {
                    isDeleted = true;
                }
            } catch (Exception ignored) {}

            if (isDeleted) {
                System.err.println("[404] Barang tidak ditemukan di DB saat transaksi: id=" + targetBarang.getId() + " | " + e.getMessage());
                show404Error();
            } else {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi: " + e.getMessage(), "Error Transaksi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void show404Error() {
        bottomSheetOverlay.openDialog(
            com.inventaris.main.ui.components.ConfirmDialogs.createNotFoundErrorDialog(() -> {
                bottomSheetOverlay.closeDialog();
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            }), 340, 240
        );
    }
}
