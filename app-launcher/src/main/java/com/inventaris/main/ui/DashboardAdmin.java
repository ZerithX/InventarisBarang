package com.inventaris.main.ui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardAdmin extends JFrame {
    public DashboardAdmin() {
        initComponents();
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
        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        cardsPanel.setBackground(Color.decode("#F8F9FB"));
        cardsPanel.setMaximumSize(new Dimension(800, 100));

        cardsPanel.add(createSummaryCard("TOTAL KATEGORI", "5"));
        cardsPanel.add(createSummaryCard("TOTAL BARANG", "150"));

        contentPanel.add(cardsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // 2. Section Title
        JLabel sectionTitle = new JLabel("DAFTAR BARANG TERBARU");
        sectionTitle.setFont(new Font("Inter", Font.BOLD, 13));
        sectionTitle.setForeground(Color.DARK_GRAY);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(sectionTitle);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 3. Search Bar
        JTextField searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Cari barang...");
        searchField.putClientProperty("JComponent.roundRect", true);
        searchField.setPreferredSize(new Dimension(350, 40));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        searchField.setFont(new Font("Inter", Font.PLAIN, 14));
        searchField.setMargin(new Insets(5, 10, 5, 10));

        contentPanel.add(searchField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 4. Items List Panel
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#EBEBEB"), 1, true));

        listPanel.add(createItemRow("BRG-001 / Laptop Pro X", "Elektronik • Stok: 45", "TERSEDIA", true));
        listPanel.add(createSeparator());
        listPanel.add(createItemRow("BRG-002 / Monitor 27\" 4K", "Elektronik • Stok: 2", "KRITIS", false));
        listPanel.add(createSeparator());
        listPanel.add(createItemRow("BRG-003 / Ergonomic Chair", "Perabot • Stok: 12", "TERSEDIA", true));
        listPanel.add(createSeparator());
        listPanel.add(createItemRow("BRG-004 / USB-C Hub", "Aksesoris • Stok: 3", "KRITIS", false));

        contentPanel.add(listPanel);

        // Wrapping contentPanel in JScrollPane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
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
        return sep;
    }

    private JPanel createSummaryCard(String title, String value) {
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

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 26));
        valueLabel.setForeground(Color.BLACK);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(valueLabel);

        return card;
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
