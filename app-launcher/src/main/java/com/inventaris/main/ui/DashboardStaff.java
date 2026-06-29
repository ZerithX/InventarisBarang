package com.inventaris.main.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.inventaris.auth.domain.User;
import com.inventaris.auth.domain.Staff;
import com.inventaris.inventory.domain.Barang;
import com.inventaris.inventory.domain.Kategori;
import com.inventaris.inventory.repository.BarangRepository;
import com.inventaris.inventory.repository.KategoriRepository;
import com.inventaris.transaction.domain.Transaksi;
import com.inventaris.transaction.domain.TipeTransaksi;
import com.inventaris.transaction.domain.BarangMasuk;
import com.inventaris.transaction.domain.BarangKeluar;
import com.inventaris.transaction.repository.TransaksiRepository;
import com.inventaris.transaction.service.TransactionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardStaff extends JFrame {
    private final User staffUser;
    private final TransactionService transactionService;

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel btnDashboard;
    private JPanel btnBarang;
    private JPanel btnLaporan;

    // Katalog View Components
    private JScrollPane katalogScrollPane;
    private JTextField txtSearch;
    private JPanel pillsPanel;
    private JPanel catalogItemsPanel;
    private String selectedCategory = "Semua";

    public DashboardStaff(User staffUser) {
        this.staffUser = staffUser;
        this.transactionService = new TransactionService(new TransaksiRepository(), new BarangRepository());

        setTitle("Sistem Inventaris - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        setContentPane(mainContainer);

        // CardLayout Setup
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);

        cardPanel.add(createCenterContent(), "DASHBOARD");
        cardPanel.add(createKatalogContent(), "KATALOG");

        mainContainer.add(createTopBar(), BorderLayout.NORTH);
        mainContainer.add(cardPanel, BorderLayout.CENTER);
        mainContainer.add(createBottomNav(), BorderLayout.SOUTH);
        
        // Initial load of pills
        refreshKatalog();
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblBrand = new JLabel("Kinetic Inventory");
        lblBrand.setFont(new Font("Georgia", Font.PLAIN, 24)); // using serif font for elegance

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);
        JLabel lblStaff = new JLabel(staffUser != null ? staffUser.getName() : "Staff");
        lblStaff.setFont(new Font("Inter", Font.PLAIN, 14));
        lblStaff.setForeground(Color.GRAY);

        // Right arrow + bracket as logout icon approximation
        JLabel lblLogout = new JLabel("<html><b style='color:#C83214;font-size:18px;'>&#x2192;]</b></html>");
        lblLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        rightPanel.add(lblStaff);
        rightPanel.add(lblLogout);

        topBar.add(lblBrand, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    private JScrollPane createCenterContent() {
        JPanel centerPanel = new JPanel() {
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
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Cards
        centerPanel.add(createActionCard("Input Barang Masuk", "Catat penerimaan stok baru", new Color(235, 245, 255), new Color(0, 102, 204), "\u21A1")); // Down arrow to bar
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(createActionCard("Input Barang Keluar", "Catat penggunaan atau pengeluaran stok", new Color(255, 240, 235), new Color(204, 51, 0), "\u219F")); // Up arrow from bar
        
        centerPanel.add(Box.createVerticalStrut(40));

        // History Label
        JLabel lblHistory = new JLabel("RIWAYAT TRANSAKSI");
        lblHistory.setFont(new Font("Inter", Font.BOLD, 12));
        lblHistory.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(lblHistory);
        centerPanel.add(Box.createVerticalStrut(15));

        // --- DB: history input barang masuk/keluar by staff yang sedang login
        try {
            List<Transaksi> list = transactionService.getTransactionsByUser(staffUser.getId());
            if (list.isEmpty()) {
                JLabel lblEmpty = new JLabel("Belum ada riwayat transaksi.");
                lblEmpty.setFont(new Font("Inter", Font.ITALIC, 14));
                lblEmpty.setForeground(Color.GRAY);
                lblEmpty.setAlignmentX(Component.LEFT_ALIGNMENT);
                centerPanel.add(lblEmpty);
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm 'WIB'");
                for (Transaksi t : list) {
                    String barangNama = t.getBarang() != null ? t.getBarang().getNama() : "Barang Tidak Dikenal";
                    String formatTime = t.getCreatedAt() != null ? t.getCreatedAt().format(formatter) : "-";
                    String prefix = t.getTipeTransaksi() == TipeTransaksi.MASUK ? "+" : "-";
                    boolean isMasuk = t.getTipeTransaksi() == TipeTransaksi.MASUK;

                    centerPanel.add(createTransactionItem(
                        barangNama,
                        formatTime,
                        prefix + t.getJumlah(),
                        t.getTipeTransaksi().name(),
                        isMasuk
                    ));
                    centerPanel.add(Box.createVerticalStrut(10));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JLabel lblError = new JLabel("Gagal memuat riwayat transaksi.");
            lblError.setFont(new Font("Inter", Font.ITALIC, 14));
            lblError.setForeground(Color.RED);
            lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
            centerPanel.add(lblError);
        }
        // --- DB

        centerPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private JScrollPane createKatalogContent() {
        JPanel container = new JPanel() {
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
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(250, 250, 250)); // light grey figma canvas background
        container.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Header
        JLabel lblTitle = new JLabel("Katalog Barang");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 28));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Referensi Data Inventaris");
        lblSubtitle.setFont(new Font("Inter", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.GRAY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblSubtitle);
        container.add(Box.createVerticalStrut(20));

        // 2. Search Bar
        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        searchWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        searchWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchWrapper.putClientProperty(FlatClientProperties.STYLE, "arc: 16;");

        JLabel lblSearchIcon = new JLabel("🔍 ");
        lblSearchIcon.setFont(new Font("Inter", Font.PLAIN, 16));
        lblSearchIcon.setForeground(Color.GRAY);
        searchWrapper.add(lblSearchIcon, BorderLayout.WEST);

        txtSearch = new JTextField();
        txtSearch.setBorder(null);
        txtSearch.setFont(new Font("Inter", Font.PLAIN, 14));
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari nama atau kategori...");
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshKatalog(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshKatalog(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshKatalog(); }
        });
        searchWrapper.add(txtSearch, BorderLayout.CENTER);

        container.add(searchWrapper);
        container.add(Box.createVerticalStrut(15));

        // 3. Category Pills Wrapper
        pillsPanel = new JPanel();
        pillsPanel.setLayout(new BoxLayout(pillsPanel, BoxLayout.X_AXIS));
        pillsPanel.setBackground(new Color(250, 250, 250));
        pillsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane pillsScroll = new JScrollPane(pillsPanel);
        pillsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pillsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        pillsScroll.setBorder(null);
        pillsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pillsScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        container.add(pillsScroll);
        container.add(Box.createVerticalStrut(15));

        // 4. Catalog Items List
        catalogItemsPanel = new JPanel();
        catalogItemsPanel.setLayout(new BoxLayout(catalogItemsPanel, BoxLayout.Y_AXIS));
        catalogItemsPanel.setBackground(new Color(250, 250, 250));
        catalogItemsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        container.add(catalogItemsPanel);
        container.add(Box.createVerticalGlue());

        katalogScrollPane = new JScrollPane(container);
        katalogScrollPane.setBorder(null);
        katalogScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        katalogScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        katalogScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return katalogScrollPane;
    }

    private void refreshKatalog() {
        if (pillsPanel == null || catalogItemsPanel == null) return;
        loadCategoryPills();
        loadCatalogItems();
    }

    private void loadCategoryPills() {
        pillsPanel.removeAll();
        pillsPanel.add(createPillButton("Semua", "Semua".equals(selectedCategory)));

        try {
            KategoriRepository katRepo = new KategoriRepository();
            List<Kategori> kats = katRepo.findAll();
            for (Kategori kat : kats) {
                pillsPanel.add(Box.createHorizontalStrut(10));
                pillsPanel.add(createPillButton(kat.getNama(), kat.getNama().equals(selectedCategory)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        pillsPanel.revalidate();
        pillsPanel.repaint();
    }

    private JPanel createPillButton(String text, boolean isActive) {
        JPanel pill = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2.setColor(isActive ? Color.decode("#0D52D6") : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

                // Border for inactive state
                if (!isActive) {
                    g2.setColor(Color.decode("#DCDCDC"));
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                }
                g2.dispose();
            }
        };
        pill.setOpaque(false);
        pill.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pill.setBorder(new EmptyBorder(6, 16, 6, 16));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.BOLD, 13));
        label.setForeground(isActive ? Color.WHITE : Color.decode("#333333"));
        pill.add(label, BorderLayout.CENTER);

        pill.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedCategory = text;
                refreshKatalog();
            }
        });

        return pill;
    }

    private void loadCatalogItems() {
        catalogItemsPanel.removeAll();
        String query = txtSearch.getText().trim().toLowerCase();

        try {
            BarangRepository barangRepo = new BarangRepository();
            List<Barang> barangList;
            if (!query.isEmpty()) {
                barangList = barangRepo.findByNameLike(query);
            } else {
                barangList = barangRepo.findAll();
            }

            int count = 0;
            for (Barang b : barangList) {
                // Filter by category
                if (!"Semua".equals(selectedCategory)) {
                    if (b.getKategori() == null || !selectedCategory.equals(b.getKategori().getNama())) {
                        continue;
                    }
                }

                catalogItemsPanel.add(createCatalogCard(b));
                catalogItemsPanel.add(Box.createVerticalStrut(15));
                count++;
            }

            if (count == 0) {
                JLabel lblEmpty = new JLabel("Tidak ada barang ditemukan.");
                lblEmpty.setFont(new Font("Inter", Font.ITALIC, 14));
                lblEmpty.setForeground(Color.GRAY);
                lblEmpty.setAlignmentX(Component.LEFT_ALIGNMENT);
                catalogItemsPanel.add(lblEmpty);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        catalogItemsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, catalogItemsPanel.getPreferredSize().height));
        catalogItemsPanel.revalidate();
        catalogItemsPanel.repaint();
    }

    private JPanel createCatalogCard(Barang b) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#EBEBEB"), 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 16;");

        // Top Row: Title & Status
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Judul: Nama Barang saja (tanpa UUID)
        JLabel lblTitle = new JLabel(b.getNama());
        lblTitle.setFont(new Font("Inter", Font.BOLD, 15));
        lblTitle.setForeground(Color.BLACK);
        topRow.add(lblTitle, BorderLayout.WEST);

        // Status Badge
        boolean tersedia = b.getStok() > 0;
        JLabel lblStatus = new JLabel(tersedia ? "TERSEDIA" : "HABIS", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Inter", Font.BOLD, 10));
        lblStatus.setOpaque(true);
        if (tersedia) {
            lblStatus.setBackground(Color.decode("#E8F5E9"));
            lblStatus.setForeground(Color.decode("#2E7D32"));
        } else {
            lblStatus.setBackground(Color.decode("#FFEBEE"));
            lblStatus.setForeground(Color.decode("#C62828"));
        }
        lblStatus.setBorder(new EmptyBorder(3, 8, 3, 8));
        lblStatus.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        topRow.add(lblStatus, BorderLayout.EAST);

        card.add(topRow);
        card.add(Box.createVerticalStrut(5));

        // Subtitle: Kategori
        String katName = b.getKategori() != null ? b.getKategori().getNama() : "-";
        JLabel lblKategori = new JLabel("Kategori: " + katName);
        lblKategori.setFont(new Font("Inter", Font.PLAIN, 12));
        lblKategori.setForeground(Color.GRAY);
        lblKategori.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblKategori);
        card.add(Box.createVerticalStrut(15));

        // Description Box
        String desc = b.getDeskripsi() != null ? b.getDeskripsi() : "Tidak ada deskripsi.";
        JTextArea txtDesc = new JTextArea(desc);
        txtDesc.setFont(new Font("Inter", Font.PLAIN, 12));
        txtDesc.setForeground(Color.decode("#333333"));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setFocusable(false);
        txtDesc.setBackground(Color.decode("#F5F6FF")); // Light violet background
        txtDesc.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel descWrapper = new JPanel(new BorderLayout());
        descWrapper.setBackground(Color.decode("#F5F6FF"));
        descWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#E6E8FF"), 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));
        descWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        descWrapper.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        descWrapper.add(txtDesc, BorderLayout.CENTER);

        card.add(descWrapper);
        card.add(Box.createVerticalStrut(15));

        // Bottom Row: Stok
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblStokLabel = new JLabel("Stok Saat Ini:");
        lblStokLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        lblStokLabel.setForeground(Color.GRAY);
        bottomRow.add(lblStokLabel, BorderLayout.WEST);

        JLabel lblStokValue = new JLabel(String.valueOf(b.getStok()));
        lblStokValue.setFont(new Font("Inter", Font.BOLD, 16));
        lblStokValue.setForeground(tersedia ? Color.decode("#0D52D6") : Color.decode("#C62828"));
        bottomRow.add(lblStokValue, BorderLayout.EAST);

        card.add(bottomRow);

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));

        return card;
    }

    private JPanel createActionCard(String title, String subtitle, Color iconBg, Color iconFg, String iconStr) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 16;");

        // Icon
        JPanel iconPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getWidth(), getHeight())); // circle
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(50, 50));
        JLabel lblIcon = new JLabel(iconStr, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Inter", Font.BOLD, 24));
        lblIcon.setForeground(iconFg);
        iconPanel.add(lblIcon, BorderLayout.CENTER);

        // Texts
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 18));
        
        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Inter", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);
        
        textPanel.add(lblTitle);
        textPanel.add(lblSub);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTransactionItem(String title, String time, String amount, String type, boolean isMasuk) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                new EmptyBorder(15, 0, 15, 0)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left texts
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        leftPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.PLAIN, 16));
        
        JLabel lblTime = new JLabel(time);
        lblTime.setFont(new Font("Inter", Font.PLAIN, 13));
        lblTime.setForeground(Color.GRAY);
        
        leftPanel.add(lblTitle);
        leftPanel.add(lblTime);

        // Right texts
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        
        JPanel container = new JPanel(new GridLayout(2, 1, 0, 4));
        container.setOpaque(false);
        
        JLabel lblAmount = new JLabel(amount, SwingConstants.RIGHT);
        lblAmount.setFont(new Font("Inter", Font.BOLD, 18));
        lblAmount.setForeground(isMasuk ? new Color(0, 102, 204) : new Color(204, 51, 0));

        JLabel lblType = new JLabel(type, SwingConstants.CENTER);
        lblType.setFont(new Font("Inter", Font.BOLD, 10));
        lblType.setForeground(isMasuk ? new Color(0, 102, 204) : new Color(204, 51, 0));
        lblType.setOpaque(true);
        lblType.setBackground(isMasuk ? new Color(240, 248, 255) : new Color(255, 245, 240));
        lblType.setBorder(new EmptyBorder(2, 8, 2, 8));
        lblType.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");

        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        typePanel.setOpaque(false);
        typePanel.add(lblType);

        container.add(lblAmount);
        container.add(typePanel);
        
        rightPanel.add(container, BorderLayout.CENTER);

        item.add(leftPanel, BorderLayout.CENTER);
        item.add(rightPanel, BorderLayout.EAST);

        return item;
    }

    private JPanel createBottomNav() {
        JPanel navBar = new JPanel(new GridLayout(1, 3));
        navBar.setBackground(Color.WHITE);
        navBar.setPreferredSize(new Dimension(getWidth(), 70));
        navBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        btnDashboard = createNavItem("Dashboard", "\u25A6", true); // squares active
        btnBarang = createNavItem("Barang", "\u25A4", false);      // box inactive
        btnLaporan = createNavItem("Laporan", "\uD83D\uDCCA", false);    // chart inactive

        btnDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                switchTab("DASHBOARD");
            }
        });

        btnBarang.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                switchTab("KATALOG");
            }
        });

        navBar.add(btnDashboard);
        navBar.add(btnBarang);
        navBar.add(btnLaporan);

        return navBar;
    }

    private void switchTab(String tabName) {
        cardLayout.show(cardPanel, tabName);
        updateNavItemState(btnDashboard, "Dashboard", "\u25A6", "DASHBOARD".equals(tabName));
        updateNavItemState(btnBarang, "Barang", "\u25A4", "KATALOG".equals(tabName));
        if ("KATALOG".equals(tabName)) {
            refreshKatalog();
            SwingUtilities.invokeLater(() -> {
                if (katalogScrollPane != null) {
                    katalogScrollPane.getVerticalScrollBar().setValue(0);
                }
            });
        }
    }

    private void updateNavItemState(JPanel item, String text, String iconStr, boolean isActive) {
        item.removeAll();
        item.add(Box.createVerticalGlue());

        JLabel lblIcon = new JLabel(iconStr, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Inter", Font.PLAIN, 22));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIcon.setForeground(isActive ? new Color(0, 102, 204) : Color.GRAY);

        JLabel lblText = new JLabel(text, SwingConstants.CENTER);
        lblText.setFont(new Font("Inter", isActive ? Font.BOLD : Font.PLAIN, 12));
        lblText.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblText.setForeground(isActive ? new Color(0, 102, 204) : Color.GRAY);

        item.add(lblIcon);
        item.add(Box.createVerticalStrut(4));
        item.add(lblText);
        item.add(Box.createVerticalGlue());

        item.revalidate();
        item.repaint();
    }

    private JPanel createNavItem(String text, String iconStr, boolean isActive) {
        JPanel item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBackground(Color.WHITE);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        item.add(Box.createVerticalGlue());

        JLabel lblIcon = new JLabel(iconStr, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Inter", Font.PLAIN, 22));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIcon.setForeground(isActive ? new Color(0, 102, 204) : Color.GRAY);

        JLabel lblText = new JLabel(text, SwingConstants.CENTER);
        lblText.setFont(new Font("Inter", isActive ? Font.BOLD : Font.PLAIN, 12));
        lblText.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblText.setForeground(isActive ? new Color(0, 102, 204) : Color.GRAY);

        item.add(lblIcon);
        item.add(Box.createVerticalStrut(4));
        item.add(lblText);
        
        item.add(Box.createVerticalGlue());

        return item;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Dummy staff user untuk pengujian langsung
            Staff dummyStaff = new Staff("staff_dummy", "password");
            dummyStaff.setId("9a6fca31-6fe6-11f1-aefe-2c1b3ae3ac30"); // Sesuai ID staff di SQL dump
            new DashboardStaff(dummyStaff).setVisible(true);
        });
    }
}