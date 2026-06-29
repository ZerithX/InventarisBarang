package com.inventaris.main.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.inventaris.inventory.repository.BarangRepository;
import com.inventaris.inventory.repository.KategoriRepository;
import com.inventaris.inventory.service.InventoryService;
import com.inventaris.transaction.repository.TransaksiRepository;
import com.inventaris.transaction.service.TransactionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DashboardAdmin extends JFrame {
    private final InventoryService inventoryService;
    private final TransactionService transactionService;
    private JLabel lblTotalKategori;
    private JLabel lblTotalBarang;
    private JLabel lblStokKritis;
    private JLabel lblTransaksiHariIni;
    private JPanel listPanel;
    private JTextField searchField;

    public DashboardAdmin() {
        this.inventoryService = new InventoryService(new BarangRepository(), new KategoriRepository());
        this.transactionService = new TransactionService(new TransaksiRepository());

        // Initialize dashboard labels
        this.lblTotalKategori = new JLabel("0");
        this.lblTotalBarang = new JLabel("0");
        this.lblStokKritis = new JLabel("0");
        this.lblTransaksiHariIni = new JLabel("0");

        initComponents();
        loadDashboardData();
    }

    private void initComponents() {
        setTitle("Sistem Inventaris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.decode("#F8F9FA"));
        setLayout(new BorderLayout());

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#EBEBEB")),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel menuIcon = new JLabel("☰");
        menuIcon.setFont(new Font("Inter", Font.BOLD, 22));
        menuIcon.setForeground(Color.DARK_GRAY);

        JLabel titleLabel = new JLabel("Sistem Inventaris");
        titleLabel.setFont(new Font("Newsreader", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.DARK_GRAY);

        topBar.add(menuIcon, BorderLayout.WEST);
        topBar.add(titleLabel, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);

        // Center Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.decode("#F8F9FB"));
        contentPanel.setBorder(new EmptyBorder(20, 15, 20, 15));

        // 1. Summary Cards Panel
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        cardsPanel.setBackground(Color.decode("#F8F9FB"));
        cardsPanel.setMaximumSize(new Dimension(800, 200));
        cardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- DB: total kategori, total barang, total barang dengan stok < 10, total transaksi hari ini
        cardsPanel.add(createSummaryCard("TOTAL KATEGORI", lblTotalKategori));
        cardsPanel.add(createSummaryCard("TOTAL BARANG", lblTotalBarang));
        cardsPanel.add(createSummaryCard("STOK KRITIS (<10)", lblStokKritis));
        cardsPanel.add(createSummaryCard("TRANSAKSI HARI INI", lblTransaksiHariIni));
        // --- DB

        contentPanel.add(cardsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // 2. Section Title
        JLabel sectionTitle = new JLabel("DAFTAR BARANG TERBARU");
        sectionTitle.setFont(new Font("Inter", Font.BOLD, 13));
        sectionTitle.setForeground(Color.DARK_GRAY);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(sectionTitle);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // --- DB: search data barang by nama dari tabel barang
        // 3. Search Bar
        this.searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Cari barang...");
        searchField.putClientProperty("JComponent.roundRect", true);
        searchField.setPreferredSize(new Dimension(350, 40));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        searchField.setFont(new Font("Inter", Font.PLAIN, 14));
        searchField.setMargin(new Insets(5, 10, 5, 10));
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            private void search() {
                loadBarangData(searchField.getText());
            }
        });
        // --- DB

        contentPanel.add(searchField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 4. Items List Panel
        this.listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#EBEBEB"), 1, true));
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(listPanel);

        // Wrapping contentPanel in JScrollPane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Navigation
        JPanel bottomNav = new JPanel(new GridLayout(1, 3));
        bottomNav.setBackground(Color.WHITE);
        bottomNav.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#EBEBEB")),
                new EmptyBorder(10, 0, 10, 0)
        ));
        bottomNav.setPreferredSize(new Dimension(400, 70));

        bottomNav.add(createNavButton("■", "Dashboard", true));
        bottomNav.add(createNavButton("□", "Kelola Data", false));
        bottomNav.add(createNavButton("📈", "Laporan", false));

        add(bottomNav, BorderLayout.SOUTH);
    }

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(Color.decode("#F0F0F0"));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, sep.getPreferredSize().height));
        return sep;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#EBEBEB"), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 10));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("Inter", Font.BOLD, 26));
        valueLabel.setForeground(Color.BLACK);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(valueLabel);

        return card;
    }

    private void loadDashboardData() {
        try {
            int totalKategori = inventoryService.getTotalKategori();
            int totalBarang = inventoryService.getTotalBarang();
            int stokKritis = inventoryService.getTotalLowStockBarang(10);
            int transaksiHariIni = transactionService.getTransactionsCountToday();

            lblTotalKategori.setText(String.valueOf(totalKategori));
            lblTotalBarang.setText(String.valueOf(totalBarang));
            lblStokKritis.setText(String.valueOf(stokKritis));
            lblTransaksiHariIni.setText(String.valueOf(transaksiHariIni));
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat ringkasan dashboard: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        }

        loadBarangData("");
    }

    private void loadBarangData(String query) {
        listPanel.removeAll();
        try {
            List<com.inventaris.inventory.domain.Barang> barangList = inventoryService.searchBarangByName(query);
            Map<String, String> kategoriMap = inventoryService.getAllKategoriMap();

            if (barangList.isEmpty()) {
                JPanel emptyWrapper = new JPanel(new GridBagLayout());
                emptyWrapper.setBackground(Color.WHITE);
                emptyWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
                emptyWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

                JLabel emptyLabel = new JLabel("Tidak ada barang ditemukan", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Inter", Font.ITALIC, 14));
                emptyLabel.setForeground(Color.GRAY);

                emptyWrapper.add(emptyLabel);
                listPanel.add(emptyWrapper);
            } else {
                for (int i = 0; i < barangList.size(); i++) {
                    com.inventaris.inventory.domain.Barang b = barangList.get(i);
                    String id = b.getId();
                    String shortId = id.length() > 8 ? id.substring(0, 8) : id;
                    String titleText = shortId + " / " + b.getNama();

                    String katName = b.getKategori() != null ? b.getKategori().getNama() : "Tidak Kategori";
                    String subtitleText = katName + " • Stok: " + b.getStok();

                    boolean isAvailable = b.getStok() >= 10;
                    String statusText = isAvailable ? "TERSEDIA" : "KRITIS";

                    listPanel.add(createItemRow(titleText, subtitleText, statusText, isAvailable));

                    if (i < barangList.size() - 1) {
                        listPanel.add(createSeparator());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data barang: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createItemRow(String title, String subtitle, String status, boolean isAvailable) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(12, 15, 12, 15));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        titleLabel.setForeground(Color.DARK_GRAY);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(subtitleLabel);

        // Status Badge
        JLabel statusBadge = new JLabel(status, SwingConstants.CENTER);
        statusBadge.setFont(new Font("Inter", Font.BOLD, 10));
        statusBadge.setOpaque(true);
        if (isAvailable) {
            statusBadge.setBackground(Color.decode("#E8F5E9"));
            statusBadge.setForeground(Color.decode("#2E7D32"));
        } else {
            statusBadge.setBackground(Color.decode("#FFEBEE"));
            statusBadge.setForeground(Color.decode("#C62828"));
        }
        statusBadge.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel badgeWrapper = new JPanel(new GridBagLayout());
        badgeWrapper.setBackground(Color.WHITE);
        badgeWrapper.add(statusBadge);

        row.add(textPanel, BorderLayout.CENTER);
        row.add(badgeWrapper, BorderLayout.EAST);

        // Prevent stretching in Y_AXIS BoxLayout
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));

        return row;
    }

    private JPanel createNavButton(String icon, String text, boolean isActive) {
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Inter", Font.PLAIN, 22));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (isActive) {
            iconLabel.setForeground(Color.decode("#1976D2"));
            textLabel.setForeground(Color.decode("#1976D2"));
        } else {
            iconLabel.setForeground(Color.GRAY);
            textLabel.setForeground(Color.GRAY);
        }

        btnPanel.add(iconLabel);
        btnPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        btnPanel.add(textLabel);

        return btnPanel;
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new DashboardAdmin().setVisible(true);
        });
    }
}
