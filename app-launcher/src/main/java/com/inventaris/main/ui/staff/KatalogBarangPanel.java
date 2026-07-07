package com.inventaris.main.ui.staff;

import com.formdev.flatlaf.FlatClientProperties;
import com.inventaris.inventory.domain.Barang;
import com.inventaris.inventory.domain.Kategori;
import com.inventaris.inventory.repository.BarangRepository;
import com.inventaris.inventory.repository.KategoriRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.List;

public class KatalogBarangPanel extends JPanel {
    private JScrollPane katalogScrollPane;
    private JTextField txtSearch;
    private JPanel pillsPanel;
    private JPanel catalogItemsPanel;
    private String selectedCategory = "Semua";

    public KatalogBarangPanel() {
        initComponents();
        refreshKatalog();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250));

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
        container.setBackground(new Color(250, 250, 250));
        container.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Header
        JLabel lblTitle = new JLabel("Katalog Barang");
        lblTitle.setFont(new Font("Newsreader 16pt", Font.BOLD, 24));
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

        add(katalogScrollPane, BorderLayout.CENTER);
    }

    public void refreshKatalog() {
        if (pillsPanel == null || catalogItemsPanel == null) return;
        loadCategoryPills();
        loadCatalogItems();
        
        // Reset scrollbar ke atas (0) setelah data diperbarui untuk mencegah pergeseran/scroll otomatis
        SwingUtilities.invokeLater(() -> {
            if (katalogScrollPane != null) {
                katalogScrollPane.getVerticalScrollBar().setValue(0);
            }
        });
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
            JOptionPane.showMessageDialog(this, "Gagal memuat kategori barang: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Gagal memuat katalog barang: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
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

    public void resetScroll() {
        SwingUtilities.invokeLater(() -> {
            if (katalogScrollPane != null) {
                katalogScrollPane.getVerticalScrollBar().setValue(0);
            }
        });
    }
}
