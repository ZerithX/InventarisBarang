package com.inventaris.main.ui.staff;

import com.inventaris.auth.domain.User;
import com.inventaris.inventory.domain.Barang;
import com.inventaris.inventory.domain.Kategori;
import com.inventaris.inventory.service.InventoryService;
import com.inventaris.transaction.domain.BarangMasuk;
import com.inventaris.transaction.domain.TipeTransaksi;
import com.inventaris.transaction.service.TransactionService;
import com.inventaris.main.ui.components.BottomSheetOverlay;
import com.inventaris.main.ui.components.ConfirmDialogs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FormTransaksiMasukPanel extends JPanel {
    private final User staffUser;
    private final InventoryService inventoryService;
    private final TransactionService transactionService;
    private final BottomSheetOverlay bottomSheetOverlay;
    private final Runnable refreshCallback;

    private JComboBox<String> cbBarang;
    private JPanel infoStokBox;
    private JLabel lblStokSaatIni;

    // Fields for New Item (tambah barang baru)
    private JPanel newBarangContainer;
    private JTextField txtNamaBarangBaru;
    private JComboBox<String> cbKategoriBaru;
    private JTextArea txtDeskripsiBaru;

    private JTextField txtJumlah;
    private JTextField txtTanggal;
    private JTextArea txtKeterangan;
    private JButton btnSimpan;

    private JPanel bodyPanel;
    private JScrollPane scrollPane;

    private List<Barang> barangList = new ArrayList<>();
    private List<Kategori> kategoriList = new ArrayList<>();

    public FormTransaksiMasukPanel(User staffUser, InventoryService inventoryService, TransactionService transactionService,
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

        JLabel lblTitle = new JLabel("Transaksi Masuk", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 18));
        lblTitle.setForeground(Color.decode("#1E293B"));

        // Placeholder untuk keseimbangan layout agar tulisan center secara presisi
        JLabel lblPlaceholder = new JLabel("<html><b style='font-size:20px;color:white;'>&larr;</b></html>");
        lblPlaceholder.setBorder(new EmptyBorder(0, 0, 0, 20));

        headerPanel.add(lblBack, BorderLayout.WEST);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(lblPlaceholder, BorderLayout.EAST);

        // Form Body Panel inside JScrollPane to handle new item fields expansion
        this.bodyPanel = new JPanel() {
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

        // 1. Label Pilih Barang
        JLabel lblPilihBarang = new JLabel("PILIH BARANG");
        lblPilihBarang.setFont(new Font("Inter", Font.BOLD, 11));
        lblPilihBarang.setForeground(Color.GRAY);
        lblPilihBarang.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(lblPilihBarang);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        // ComboBox Pilih Barang
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

        // 2. Info Stok Box (Light blue box)
        infoStokBox = new JPanel();
        infoStokBox.setLayout(new BoxLayout(infoStokBox, BoxLayout.Y_AXIS));
        infoStokBox.setBackground(Color.decode("#EFF6FF")); // Light Blue 50
        infoStokBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#BFDBFE"), 1, true), // Blue 200 border
                new EmptyBorder(10, 15, 10, 15)
        ));
        infoStokBox.setMaximumSize(new Dimension(800, 70));
        infoStokBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblStokHeader = new JLabel("ⓘ  STOK SAAT INI");
        lblStokHeader.setFont(new Font("Inter", Font.BOLD, 11));
        lblStokHeader.setForeground(Color.decode("#1E40AF")); // Blue 800
        lblStokHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblStokSaatIni = new JLabel("0 Unit");
        lblStokSaatIni.setFont(new Font("Inter", Font.BOLD, 18));
        lblStokSaatIni.setForeground(Color.decode("#1E293B"));
        lblStokSaatIni.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoStokBox.add(lblStokHeader);
        infoStokBox.add(Box.createRigidArea(new Dimension(0, 4)));
        infoStokBox.add(lblStokSaatIni);
        bodyPanel.add(infoStokBox);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // 3. New Barang Container (Hidden by default, shown when "+ Tambah Barang Baru" is selected)
        newBarangContainer = new JPanel();
        newBarangContainer.setLayout(new BoxLayout(newBarangContainer, BoxLayout.Y_AXIS));
        newBarangContainer.setBackground(Color.WHITE);
        newBarangContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        newBarangContainer.setVisible(false);

        // Nama Barang Baru
        JLabel lblNamaBaru = new JLabel("NAMA BARANG BARU");
        lblNamaBaru.setFont(new Font("Inter", Font.BOLD, 11));
        lblNamaBaru.setForeground(Color.GRAY);
        lblNamaBaru.setAlignmentX(Component.LEFT_ALIGNMENT);
        newBarangContainer.add(lblNamaBaru);
        newBarangContainer.add(Box.createRigidArea(new Dimension(0, 6)));

        txtNamaBarangBaru = new JTextField();
        txtNamaBarangBaru.setFont(new Font("Inter", Font.PLAIN, 14));
        txtNamaBarangBaru.putClientProperty("JComponent.roundRect", true);
        txtNamaBarangBaru.setMaximumSize(new Dimension(800, 40));
        txtNamaBarangBaru.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        txtNamaBarangBaru.setMargin(new Insets(5, 10, 5, 10));
        txtNamaBarangBaru.setAlignmentX(Component.LEFT_ALIGNMENT);
        newBarangContainer.add(txtNamaBarangBaru);
        newBarangContainer.add(Box.createRigidArea(new Dimension(0, 12)));

        // Kategori Baru
        JLabel lblKategoriBaru = new JLabel("KATEGORI BARANG");
        lblKategoriBaru.setFont(new Font("Inter", Font.BOLD, 11));
        lblKategoriBaru.setForeground(Color.GRAY);
        lblKategoriBaru.setAlignmentX(Component.LEFT_ALIGNMENT);
        newBarangContainer.add(lblKategoriBaru);
        newBarangContainer.add(Box.createRigidArea(new Dimension(0, 6)));

        cbKategoriBaru = new JComboBox<>();
        cbKategoriBaru.setFont(new Font("Inter", Font.PLAIN, 14));
        cbKategoriBaru.putClientProperty("JComponent.roundRect", true);
        cbKategoriBaru.setMaximumSize(new Dimension(800, 40));
        cbKategoriBaru.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        cbKategoriBaru.setBackground(Color.WHITE);
        cbKategoriBaru.setAlignmentX(Component.LEFT_ALIGNMENT);
        newBarangContainer.add(cbKategoriBaru);
        newBarangContainer.add(Box.createRigidArea(new Dimension(0, 12)));

        // Deskripsi Baru
        JLabel lblDeskripsiBaru = new JLabel("DESKRIPSI BARANG (opsional)");
        lblDeskripsiBaru.setFont(new Font("Inter", Font.BOLD, 11));
        lblDeskripsiBaru.setForeground(Color.GRAY);
        lblDeskripsiBaru.setAlignmentX(Component.LEFT_ALIGNMENT);
        newBarangContainer.add(lblDeskripsiBaru);
        newBarangContainer.add(Box.createRigidArea(new Dimension(0, 6)));

        txtDeskripsiBaru = new JTextArea(3, 20);
        txtDeskripsiBaru.setFont(new Font("Inter", Font.PLAIN, 13));
        txtDeskripsiBaru.setLineWrap(true);
        txtDeskripsiBaru.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDeskripsiBaru);
        descScroll.setBorder(BorderFactory.createLineBorder(Color.decode("#CBD5E1"), 1, true));
        descScroll.setMaximumSize(new Dimension(800, 70));
        descScroll.setPreferredSize(new Dimension(Integer.MAX_VALUE, 70));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        newBarangContainer.add(descScroll);
        newBarangContainer.add(Box.createRigidArea(new Dimension(0, 12)));

        bodyPanel.add(newBarangContainer);

        // 4. Jumlah Barang
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
        bodyPanel.add(txtJumlah);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // 5. Tanggal Transaksi
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

        // 6. Keterangan (opsional)
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

        this.scrollPane = new JScrollPane(bodyPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Forward mouse wheel events from text areas to parent scroll pane to prevent scroll trap/lag
        java.awt.event.MouseWheelListener forwardingListener = e -> {
            if (scrollPane != null) {
                scrollPane.dispatchEvent(javax.swing.SwingUtilities.convertMouseEvent(e.getComponent(), e, scrollPane));
            }
        };
        descScroll.addMouseWheelListener(forwardingListener);
        txtDeskripsiBaru.addMouseWheelListener(forwardingListener);
        ketScroll.addMouseWheelListener(forwardingListener);
        txtKeterangan.addMouseWheelListener(forwardingListener);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData() {
        cbBarang.removeAllItems();
        cbBarang.addItem("Pilih Barang...");
        cbBarang.addItem("+ Tambah Barang Baru");

        try {
            barangList = inventoryService.getAllBarang();
            for (Barang b : barangList) {
                cbBarang.addItem(b.getNama());
            }

            kategoriList = inventoryService.getAllKategori();
            cbKategoriBaru.removeAllItems();
            cbKategoriBaru.addItem("-- Pilih Kategori --");
            for (Kategori k : kategoriList) {
                cbKategoriBaru.addItem(k.getNama());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onBarangSelectionChanged() {
        int idx = cbBarang.getSelectedIndex();
        if (idx == 0) { // Placeholder
            infoStokBox.setVisible(true);
            newBarangContainer.setVisible(false);
            lblStokSaatIni.setText("0 Unit");
        } else if (idx == 1) { // Tambah Barang Baru
            infoStokBox.setVisible(false);
            newBarangContainer.setVisible(true);
            txtNamaBarangBaru.setText("");
            cbKategoriBaru.setSelectedIndex(0);
            txtDeskripsiBaru.setText("");
        } else { // Barang terdaftar
            infoStokBox.setVisible(true);
            newBarangContainer.setVisible(false);
            Barang b = barangList.get(idx - 2);
            lblStokSaatIni.setText(b.getStok() + " Unit");
        }

        // Revalidate dan repaint bodyPanel terlebih dahulu agar scroll pane mengetahui perubahan ukuran konten
        if (bodyPanel != null) {
            bodyPanel.revalidate();
            bodyPanel.repaint();
        }

        if (scrollPane != null) {
            scrollPane.revalidate();
            scrollPane.repaint();
        }

        revalidate();
        repaint();
    }

    private void simpanTransaksi() {
        int selectionIndex = cbBarang.getSelectedIndex();
        if (selectionIndex == 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih barang terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil Jumlah Barang
        int jumlah;
        try {
            jumlah = Integer.parseInt(txtJumlah.getText().trim());
            if (jumlah <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            txtJumlah.putClientProperty("JComponent.outline", "error");
            JOptionPane.showMessageDialog(this, "Jumlah barang harus berupa angka positif!", "Format Salah", JOptionPane.ERROR_MESSAGE);
            return;
        }
        txtJumlah.putClientProperty("JComponent.outline", null);

        if (selectionIndex == 1) {
            // Validasi Input Barang Baru
            String namaBaru = txtNamaBarangBaru.getText().trim();
            if (namaBaru.isEmpty()) {
                txtNamaBarangBaru.putClientProperty("JComponent.outline", "error");
                JOptionPane.showMessageDialog(this, "Nama barang baru tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            txtNamaBarangBaru.putClientProperty("JComponent.outline", null);

            int katIdx = cbKategoriBaru.getSelectedIndex();
            if (katIdx == 0) {
                cbKategoriBaru.putClientProperty("JComponent.outline", "error");
                JOptionPane.showMessageDialog(this, "Silakan pilih kategori untuk barang baru!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cbKategoriBaru.putClientProperty("JComponent.outline", null);
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
        Barang targetBarang;

        if (selectionIndex == 1) {
            String namaBaru = txtNamaBarangBaru.getText().trim();
            int katIdx = cbKategoriBaru.getSelectedIndex();
            Kategori targetKategori = kategoriList.get(katIdx - 1);
            String deskripsiBaru = txtDeskripsiBaru.getText().trim();

            try {
                // Buat barang baru di db dengan stok awal 0
                Barang bBaru = new Barang(namaBaru, targetKategori, 0, deskripsiBaru);
                inventoryService.saveBarang(bBaru);
                targetBarang = bBaru;
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal membuat data barang baru ke database: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            targetBarang = barangList.get(selectionIndex - 2);
        }

        // Tulis dan eksekusi transaksi masuk
        String keteranganText = txtKeterangan.getText().trim();
        BarangMasuk transaksi = new BarangMasuk(targetBarang, jumlah, staffUser, keteranganText);

        try {
            transactionService.executeTransaction(transaksi);
            
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            bottomSheetOverlay.closeSheet();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi: " + e.getMessage(), "Error Transaksi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
