package com.inventaris.main.ui.admin;

import com.inventaris.inventory.domain.Kategori;
import com.inventaris.inventory.service.InventoryService;
import com.inventaris.report.domain.LaporanItem;
import com.inventaris.report.service.ReportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LaporanPanel extends JPanel {
    private final InventoryService inventoryService;
    private final ReportService reportService;

    private JComboBox<String> cbKategori;
    private JPanel listPanel;
    private JLabel lblPeriode;
    
    private List<Kategori> kategoriList;
    private int currentYear;
    private int currentMonth;

    public LaporanPanel(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        this.reportService = new ReportService();
        this.kategoriList = new ArrayList<>();
        
        // Dapatkan bulan dan tahun berjalan saat ini
        LocalDate today = LocalDate.now();
        this.currentYear = today.getYear();
        this.currentMonth = today.getMonthValue();

        initComponents();
        loadKategoriData();
        
        // Daftarkan listener setelah combobox diisi untuk menghindari query berulang
        cbKategori.addActionListener(e -> loadLaporanData());
        
        loadLaporanData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8F9FA"));

        // 1. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#EBEBEB")),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Judul Laporan & Periode (Kiri)
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Laporan Stok Barang");
        lblTitle.setFont(new Font("Newsreader 16pt", Font.BOLD, 22));
        lblTitle.setForeground(Color.DARK_GRAY);

        // Format nama bulan dalam Bahasa Indonesia
        String namaBulan = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
        lblPeriode = new JLabel("Periode: " + namaBulan + " " + currentYear);
        lblPeriode.setFont(new Font("Inter", Font.PLAIN, 14));
        lblPeriode.setForeground(Color.GRAY);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(lblPeriode);

        // Tombol Cetak PDF (Kanan)
        JButton btnCetak = new JButton("Cetak PDF");
        btnCetak.setFont(new Font("Inter", Font.BOLD, 13));
        btnCetak.setForeground(Color.decode("#1976D2"));
        btnCetak.setBackground(Color.WHITE);
        btnCetak.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCetak.putClientProperty("JButton.buttonType", "outlined");
        btnCetak.putClientProperty("JComponent.roundRect", true);
        btnCetak.setBorder(BorderFactory.createCompoundBorder(
                btnCetak.getBorder(),
                new EmptyBorder(5, 15, 5, 15)
        ));

        btnCetak.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Fitur ekspor PDF dilewati untuk saat ini.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnCetak, BorderLayout.EAST);

        // 2. Filter & List Wrapper
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.decode("#F8F9FA"));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // JComboBox Kategori
        cbKategori = new JComboBox<>();
        cbKategori.setFont(new Font("Inter", Font.PLAIN, 14));
        cbKategori.putClientProperty("Component.arc", 8);
        cbKategori.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        cbKategori.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cbKategori.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbKategori.setBackground(Color.WHITE);

        contentPanel.add(cbKategori);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // List Panel (Untuk menampung card)
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.decode("#F8F9FA"));
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // Sembunyikan scrollbar bawaan
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBackground(Color.decode("#F8F9FA"));
        scrollPane.getViewport().setBackground(Color.decode("#F8F9FA"));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(scrollPane);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void loadKategoriData() {
        cbKategori.removeAllItems();
        cbKategori.addItem("Kategori: Semua Kategori");
        
        try {
            kategoriList = inventoryService.getAllKategori();
            for (Kategori k : kategoriList) {
                cbKategori.addItem("Kategori: " + k.getNama());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadLaporanData() {
        if (listPanel == null) return;
        listPanel.removeAll();

        // Tentukan kategori ID yang dipilih
        String targetKategoriId = "ALL";
        int selectedIndex = cbKategori.getSelectedIndex();
        if (selectedIndex > 0 && selectedIndex - 1 < kategoriList.size()) {
            targetKategoriId = kategoriList.get(selectedIndex - 1).getId();
        }

        try {
            List<LaporanItem> items = reportService.getLaporanPerkembanganStok(targetKategoriId, currentYear, currentMonth);

            if (items.isEmpty()) {
                JPanel emptyWrapper = new JPanel(new GridBagLayout());
                emptyWrapper.setBackground(Color.decode("#F8F9FA"));
                emptyWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
                emptyWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

                JLabel emptyLabel = new JLabel("Tidak ada data barang untuk kategori ini", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Inter", Font.ITALIC, 14));
                emptyLabel.setForeground(Color.GRAY);

                emptyWrapper.add(emptyLabel);
                listPanel.add(emptyWrapper);
            } else {
                for (LaporanItem item : items) {
                    listPanel.add(createCardPanel(item));
                    listPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                }
            }

            // Agar card menumpuk di atas dan tidak molor ke bawah jika data sedikit
            listPanel.add(Box.createVerticalGlue());

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                    "Gagal memuat data laporan: " + e.getMessage(), 
                    "Error Database", 
                    JOptionPane.ERROR_MESSAGE);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createCardPanel(LaporanItem item) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#EBEBEB"), 1, true),
                new EmptyBorder(15, 18, 15, 18)
        ));
        // Tetapkan ukuran agar bentuk kartu selalu konsisten
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        card.setMinimumSize(new Dimension(0, 160));
        card.setPreferredSize(new Dimension(card.getPreferredSize().width, 160));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 1. Header (Nama Barang & Status Badge)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Title: Nama Barang
        JLabel lblNama = new JLabel(item.getNamaBarang());
        lblNama.setFont(new Font("Inter", Font.BOLD, 15));
        lblNama.setForeground(Color.decode("#1E293B")); // Slate 800

        // Status Badge
        String status;
        Color bgStatus, fgStatus;
        int akhir = item.getStokAkhir();
        if (akhir == 0) {
            status = "Habis";
            bgStatus = Color.decode("#FFEBEE");
            fgStatus = Color.decode("#C62828");
        } else if (akhir < 10) {
            status = "Terbatas";
            bgStatus = Color.decode("#FFF3E0");
            fgStatus = Color.decode("#E65100");
        } else {
            status = "Tersedia";
            bgStatus = Color.decode("#E8F5E9");
            fgStatus = Color.decode("#2E7D32");
        }

        JLabel badge = new JLabel(status, SwingConstants.CENTER);
        badge.setFont(new Font("Inter", Font.BOLD, 11));
        badge.setForeground(fgStatus);
        badge.setBackground(bgStatus);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        badge.putClientProperty("JComponent.roundRect", true);

        headerPanel.add(lblNama, BorderLayout.CENTER);
        headerPanel.add(badge, BorderLayout.EAST);

        // 2. Subtitle: Kategori
        JLabel lblKategori = new JLabel(item.getNamaKategori());
        lblKategori.setFont(new Font("Inter", Font.PLAIN, 12));
        lblKategori.setForeground(Color.GRAY);
        lblKategori.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 3. Separator (Garis pemisah tipis)
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.decode("#E2E8F0")); // Slate 200
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 4. Grid Metrics (Awal, Masuk, Keluar, Akhir)
        JPanel gridPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        gridPanel.add(createMetricColumn("Awal", String.valueOf(item.getStokAwal()), Color.DARK_GRAY));
        
        // Masuk (+X)
        String masukText = item.getMasuk() > 0 ? "+" + item.getMasuk() : "+0";
        Color masukColor = item.getMasuk() > 0 ? Color.decode("#1976D2") : Color.GRAY;
        gridPanel.add(createMetricColumn("Masuk", masukText, masukColor));

        // Keluar (-Y)
        String keluarText = item.getKeluar() > 0 ? "-" + item.getKeluar() : "-0";
        Color keluarColor = item.getKeluar() > 0 ? Color.decode("#C83214") : Color.GRAY;
        gridPanel.add(createMetricColumn("Keluar", keluarText, keluarColor));

        gridPanel.add(createMetricColumn("Akhir", String.valueOf(item.getStokAkhir()), Color.BLACK));

        // Tambahkan ke Container Card
        card.add(headerPanel);
        card.add(Box.createRigidArea(new Dimension(0, 3)));
        card.add(lblKategori);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(separator);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(gridPanel);

        return card;
    }

    private JPanel createMetricColumn(String label, String value, Color valueColor) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(new Font("Inter", Font.PLAIN, 12));
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Inter", Font.BOLD, 16));
        lblVal.setForeground(valueColor);
        lblVal.setAlignmentX(Component.LEFT_ALIGNMENT);

        col.add(lblTitle);
        col.add(Box.createRigidArea(new Dimension(0, 4)));
        col.add(lblVal);

        return col;
    }
}
