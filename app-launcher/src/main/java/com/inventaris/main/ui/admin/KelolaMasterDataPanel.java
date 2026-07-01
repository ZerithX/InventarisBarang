package com.inventaris.main.ui.admin;

import com.formdev.flatlaf.FlatClientProperties;
import com.inventaris.inventory.domain.Barang;
import com.inventaris.inventory.domain.Kategori;
import com.inventaris.inventory.service.InventoryService;
import com.inventaris.main.ui.components.BottomSheetOverlay;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KelolaMasterDataPanel extends JPanel {
    private final InventoryService inventoryService;
    private final BottomSheetOverlay bottomSheetOverlay;
    private final Runnable globalRefreshCallback; // Callback to refresh summary dashboard

    private JScrollPane kelolaScroll;
    private JTextField txtKelolaSearch;
    private JPanel kelolaItemsPanel;
    private JPanel tabBarangBtn;
    private JPanel tabKategoriBtn;
    private boolean isBarangTabActive = true;

    public KelolaMasterDataPanel(InventoryService inventoryService, BottomSheetOverlay bottomSheetOverlay,
                                  Runnable globalRefreshCallback) {
        this.inventoryService = inventoryService;
        this.bottomSheetOverlay = bottomSheetOverlay;
        this.globalRefreshCallback = globalRefreshCallback;

        initComponents();
        loadKelolaData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8F9FA"));

        // Content Container holds Sub-Tabs, Search, and Scroll Panel
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BorderLayout());
        contentContainer.setBackground(Color.decode("#F8F9FB"));

        // Header Bar (Title)
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(Color.WHITE);
        headerBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#EBEBEB")),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel titleLabel = new JLabel("Kelola Master Data", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Newsreader", Font.BOLD, 22));
        titleLabel.setForeground(Color.DARK_GRAY);
        headerBar.add(titleLabel, BorderLayout.CENTER);

        // Sub-Tab Header
        JPanel subTabHeader = new JPanel(new GridLayout(1, 2));
        subTabHeader.setBackground(Color.WHITE);
        subTabHeader.setPreferredSize(new Dimension(getWidth(), 48));

        tabBarangBtn = createSubTabButton("Daftar Barang", true);
        tabKategoriBtn = createSubTabButton("Daftar Kategori", false);

        tabBarangBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                switchSubTab(true);
            }
        });

        tabKategoriBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                switchSubTab(false);
            }
        });

        subTabHeader.add(tabBarangBtn);
        subTabHeader.add(tabKategoriBtn);

        // Top Wrapper (Header Bar + Sub-Tab Header)
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setBackground(Color.WHITE);
        topWrapper.add(headerBar);
        topWrapper.add(subTabHeader);

        contentContainer.add(topWrapper, BorderLayout.NORTH);

        // Inner Scroll Container
        JPanel innerContainer = new JPanel() {
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
        innerContainer.setLayout(new BoxLayout(innerContainer, BoxLayout.Y_AXIS));
        innerContainer.setBackground(Color.decode("#F8F9FB"));
        innerContainer.setBorder(new EmptyBorder(20, 15, 20, 15));

        // Search Bar
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

        txtKelolaSearch = new JTextField();
        txtKelolaSearch.setBorder(null);
        txtKelolaSearch.setFont(new Font("Inter", Font.PLAIN, 14));
        txtKelolaSearch.putClientProperty("JTextField.placeholderText", "Cari perangkat atau kategori...");
        txtKelolaSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loadKelolaData(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loadKelolaData(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadKelolaData(); }
        });
        searchWrapper.add(txtKelolaSearch, BorderLayout.CENTER);

        innerContainer.add(searchWrapper);
        innerContainer.add(Box.createVerticalStrut(20));

        // Kelola Items List Panel
        kelolaItemsPanel = new JPanel();
        kelolaItemsPanel.setLayout(new BoxLayout(kelolaItemsPanel, BoxLayout.Y_AXIS));
        kelolaItemsPanel.setBackground(Color.decode("#F8F9FB"));
        kelolaItemsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        innerContainer.add(kelolaItemsPanel);
        innerContainer.add(Box.createVerticalGlue());

        kelolaScroll = new JScrollPane(innerContainer);
        kelolaScroll.setBorder(null);
        kelolaScroll.getVerticalScrollBar().setUnitIncrement(16);
        kelolaScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        kelolaScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // LayeredPane to float the FAB
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(kelolaScroll, JLayeredPane.DEFAULT_LAYER);

        // FAB Button Setup
        JPanel btnFab = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#0D52D6"));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        btnFab.setOpaque(false);
        btnFab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFab.setSize(56, 56);

        JLabel lblPlus = new JLabel("+", SwingConstants.CENTER);
        lblPlus.setFont(new Font("Inter", Font.PLAIN, 28));
        lblPlus.setForeground(Color.WHITE);
        btnFab.add(lblPlus, BorderLayout.CENTER);

        // Action when FAB is clicked
        btnFab.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Runnable formRefreshCallback = () -> {
                    loadKelolaData();
                    if (globalRefreshCallback != null) {
                        globalRefreshCallback.run();
                    }
                };

                if (isBarangTabActive) {
                    bottomSheetOverlay.openSheet(new FormBarangPanel(null, inventoryService, bottomSheetOverlay, formRefreshCallback), 520);
                } else {
                    bottomSheetOverlay.openSheet(new FormKategoriPanel(null, inventoryService, bottomSheetOverlay, formRefreshCallback), 280);
                }
            }
        });

        layeredPane.add(btnFab, JLayeredPane.PALETTE_LAYER);

        layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = layeredPane.getWidth();
                int h = layeredPane.getHeight();
                kelolaScroll.setBounds(0, 0, w, h);
                btnFab.setBounds(w - 76, h - 76, 56, 56);
            }
        });

        contentContainer.add(layeredPane, BorderLayout.CENTER);
        add(contentContainer, BorderLayout.CENTER);
    }

    private JPanel createSubTabButton(String text, boolean isActive) {
        JPanel tab = new JPanel(new BorderLayout());
        tab.setBackground(Color.WHITE);
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Inter", isActive ? Font.BOLD : Font.PLAIN, 14));
        label.setForeground(isActive ? Color.decode("#0D52D6") : Color.GRAY);
        tab.add(label, BorderLayout.CENTER);

        if (isActive) {
            tab.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.decode("#0D52D6")));
        } else {
            tab.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#EBEBEB")));
        }

        return tab;
    }

    public void switchSubTab(boolean isBarang) {
        this.isBarangTabActive = isBarang;
        updateSubTabState(tabBarangBtn, "Daftar Barang", isBarang);
        updateSubTabState(tabKategoriBtn, "Daftar Kategori", !isBarang);
        txtKelolaSearch.setText("");
        loadKelolaData();

        SwingUtilities.invokeLater(() -> {
            if (kelolaScroll != null) {
                kelolaScroll.getVerticalScrollBar().setValue(0);
            }
        });
    }

    private void updateSubTabState(JPanel tab, String text, boolean isActive) {
        tab.removeAll();
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Inter", isActive ? Font.BOLD : Font.PLAIN, 14));
        label.setForeground(isActive ? Color.decode("#0D52D6") : Color.GRAY);
        tab.add(label, BorderLayout.CENTER);

        if (isActive) {
            tab.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.decode("#0D52D6")));
        } else {
            tab.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#EBEBEB")));
        }
        tab.revalidate();
        tab.repaint();
    }

    public void loadKelolaData() {
        if (kelolaItemsPanel == null) return;
        kelolaItemsPanel.removeAll();
        String query = txtKelolaSearch.getText().trim().toLowerCase();

        try {
            Runnable formRefreshCallback = () -> {
                loadKelolaData();
                if (globalRefreshCallback != null) {
                    globalRefreshCallback.run();
                }
            };

            if (isBarangTabActive) {
                List<Barang> barangList;
                if (!query.isEmpty()) {
                    barangList = inventoryService.searchBarangByName(query);
                } else {
                    barangList = inventoryService.getAllBarang();
                }

                barangList.sort((a, b) -> a.getId().compareTo(b.getId()));

                for (int i = 0; i < barangList.size(); i++) {
                    kelolaItemsPanel.add(createBarangKelolaCard(barangList.get(i), formRefreshCallback));
                    kelolaItemsPanel.add(Box.createVerticalStrut(15));
                }

                if (barangList.isEmpty()) {
                    JLabel lblEmpty = new JLabel("Tidak ada barang ditemukan.");
                    lblEmpty.setFont(new Font("Inter", Font.ITALIC, 14));
                    lblEmpty.setForeground(Color.GRAY);
                    lblEmpty.setAlignmentX(Component.LEFT_ALIGNMENT);
                    kelolaItemsPanel.add(lblEmpty);
                }
            } else {
                List<Kategori> kategoriList = inventoryService.getAllKategori();
                List<Barang> allBarangList = inventoryService.getAllBarang();

                // Count barang per category
                Map<String, Long> countMap = allBarangList.stream()
                        .filter(b -> b.getKategori() != null)
                        .collect(Collectors.groupingBy(b -> b.getKategori().getId(), Collectors.counting()));

                kategoriList.sort((a, b) -> a.getId().compareTo(b.getId()));

                int renderedCount = 0;
                for (int i = 0; i < kategoriList.size(); i++) {
                    Kategori kat = kategoriList.get(i);
                    if (!query.isEmpty() && !kat.getNama().toLowerCase().contains(query)) {
                        continue;
                    }
                    long count = countMap.getOrDefault(kat.getId(), 0L);
                    kelolaItemsPanel.add(createKategoriKelolaCard(kat, count, formRefreshCallback));
                    kelolaItemsPanel.add(Box.createVerticalStrut(15));
                    renderedCount++;
                }

                if (renderedCount == 0) {
                    JLabel lblEmpty = new JLabel("Tidak ada kategori ditemukan.");
                    lblEmpty.setFont(new Font("Inter", Font.ITALIC, 14));
                    lblEmpty.setForeground(Color.GRAY);
                    lblEmpty.setAlignmentX(Component.LEFT_ALIGNMENT);
                    kelolaItemsPanel.add(lblEmpty);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        kelolaItemsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, kelolaItemsPanel.getPreferredSize().height));
        kelolaItemsPanel.revalidate();
        kelolaItemsPanel.repaint();
    }

    private JPanel createBarangKelolaCard(Barang b, Runnable formRefreshCallback) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#EBEBEB"), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 16;");

        // Top Row: Title and Actions
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel(b.getNama());
        lblTitle.setFont(new Font("Inter", Font.BOLD, 15));
        lblTitle.setForeground(Color.BLACK);
        topRow.add(lblTitle, BorderLayout.WEST);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsPanel.setOpaque(false);

        JLabel lblEdit = new JLabel("✏");
        lblEdit.setFont(new Font("Inter", Font.PLAIN, 16));
        lblEdit.setForeground(Color.decode("#0D52D6"));
        lblEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                bottomSheetOverlay.openSheet(new FormBarangPanel(b, inventoryService, bottomSheetOverlay, formRefreshCallback), 520);
            }
        });

        JLabel lblDelete = new JLabel("🗑");
        lblDelete.setFont(new Font("Inter", Font.PLAIN, 16));
        lblDelete.setForeground(Color.decode("#C83214"));
        lblDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JPanel confirmPanel = com.inventaris.main.ui.components.ConfirmDialogs.createDeleteConfirmationDialog(
                    "Hapus Data",
                    "Apakah Anda yakin ingin menghapus data ini secara permanen? Tindakan ini tidak dapat dibatalkan.",
                    () -> {
                        bottomSheetOverlay.closeDialog();
                        try {
                            inventoryService.deleteBarang(b.getId());
                            JOptionPane.showMessageDialog(KelolaMasterDataPanel.this, "Data barang berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                            formRefreshCallback.run();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(KelolaMasterDataPanel.this, "Gagal menghapus barang: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
                        }
                    },
                    () -> {
                        bottomSheetOverlay.closeDialog();
                    }
                );
                bottomSheetOverlay.openDialog(confirmPanel, 320, 320);
            }
        });

        actionsPanel.add(lblEdit);
        actionsPanel.add(lblDelete);
        topRow.add(actionsPanel, BorderLayout.EAST);

        card.add(topRow);
        card.add(Box.createVerticalStrut(8));

        // Subtitle: Kategori
        String katName = b.getKategori() != null ? b.getKategori().getNama() : "-";
        JLabel lblKategori = new JLabel("Kategori: " + katName);
        lblKategori.setFont(new Font("Inter", Font.PLAIN, 13));
        lblKategori.setForeground(Color.GRAY);
        lblKategori.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblKategori);
        card.add(Box.createVerticalStrut(4));

        // Stok
        JLabel lblStok = new JLabel("Stok: " + b.getStok());
        lblStok.setFont(new Font("Inter", Font.BOLD, 13));
        lblStok.setForeground(Color.DARK_GRAY);
        lblStok.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblStok);

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
        return card;
    }

    private JPanel createKategoriKelolaCard(Kategori kat, long count, Runnable formRefreshCallback) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#EBEBEB"), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 16;");

        // Top Row: Title and Actions
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel(kat.getNama());
        lblTitle.setFont(new Font("Inter", Font.BOLD, 15));
        lblTitle.setForeground(Color.BLACK);
        topRow.add(lblTitle, BorderLayout.WEST);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsPanel.setOpaque(false);

        JLabel lblEdit = new JLabel("✏");
        lblEdit.setFont(new Font("Inter", Font.PLAIN, 16));
        lblEdit.setForeground(Color.decode("#0D52D6"));
        lblEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                bottomSheetOverlay.openSheet(new FormKategoriPanel(kat, inventoryService, bottomSheetOverlay, formRefreshCallback), 280);
            }
        });

        JLabel lblDelete = new JLabel("🗑");
        lblDelete.setFont(new Font("Inter", Font.PLAIN, 16));
        lblDelete.setForeground(Color.decode("#C83214"));
        lblDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JPanel confirmPanel = com.inventaris.main.ui.components.ConfirmDialogs.createDeleteConfirmationDialog(
                    "Hapus Data",
                    "Apakah Anda yakin ingin menghapus data ini secara permanen? Tindakan ini tidak dapat dibatalkan.",
                    () -> {
                        bottomSheetOverlay.closeDialog();
                        try {
                            inventoryService.deleteKategori(kat.getId());
                            JOptionPane.showMessageDialog(KelolaMasterDataPanel.this, "Data kategori berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                            formRefreshCallback.run();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(KelolaMasterDataPanel.this, "Gagal menghapus kategori. Pastikan tidak ada barang yang menggunakan kategori ini!", "Error Database", JOptionPane.ERROR_MESSAGE);
                        }
                    },
                    () -> {
                        bottomSheetOverlay.closeDialog();
                    }
                );
                bottomSheetOverlay.openDialog(confirmPanel, 320, 320);
            }
        });

        actionsPanel.add(lblEdit);
        actionsPanel.add(lblDelete);
        topRow.add(actionsPanel, BorderLayout.EAST);

        card.add(topRow);
        card.add(Box.createVerticalStrut(8));

        // Subtitle: Jumlah Barang
        JLabel lblJumlah = new JLabel("Jumlah Barang: " + count);
        lblJumlah.setFont(new Font("Inter", Font.PLAIN, 13));
        lblJumlah.setForeground(Color.GRAY);
        lblJumlah.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblJumlah);

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
        return card;
    }
}
