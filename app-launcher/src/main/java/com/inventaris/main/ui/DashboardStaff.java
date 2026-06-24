package com.inventaris.main.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class DashboardStaff extends JFrame {

    public DashboardStaff() {
        setTitle("Sistem Inventaris - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        setContentPane(mainContainer);

        mainContainer.add(createTopBar(), BorderLayout.NORTH);
        mainContainer.add(createCenterContent(), BorderLayout.CENTER);
        mainContainer.add(createBottomNav(), BorderLayout.SOUTH);
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
        JLabel lblStaff = new JLabel("Staff");
        lblStaff.setFont(new Font("Inter", Font.PLAIN, 14));
        lblStaff.setForeground(Color.GRAY);

        // Right arrow + bracket as logout icon approximation
        JLabel lblLogout = new JLabel("<html><b style='color:#C83214;font-size:18px;'>&#x2192;]</b></html>");
        lblLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rightPanel.add(lblStaff);
        rightPanel.add(lblLogout);

        topBar.add(lblBrand, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    private JScrollPane createCenterContent() {
        JPanel centerPanel = new JPanel();
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

        // Transactions
        centerPanel.add(createTransactionItem("[NET-001] Cisco Catalyst Switch", "21 Jun 2026 \u2022 09:30 WIB", "+10", "MASUK", true));
        centerPanel.add(createTransactionItem("[NET-002] Kabel Fiber optik", "21 Jun 2026 \u2022 08:15 WIB", "-5", "KELUAR", false));
        centerPanel.add(createTransactionItem("[NET-003] Router SPF Edge", "20 Jun 2026 \u2022 16:45 WIB", "-1", "KELUAR", false));
        centerPanel.add(createTransactionItem("[NET-001] Cisco Catalyst Switch", "20 Jun 2026 \u2022 10:12 WIB", "+20", "MASUK", true));

        centerPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
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

        navBar.add(createNavItem("Dashboard", "\u25A6", true)); // squares
        navBar.add(createNavItem("Barang", "\u25A4", false)); // box
        navBar.add(createNavItem("Laporan", "\uD83D\uDCCA", false)); // chart

        return navBar;
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
            new DashboardStaff().setVisible(true);
        });
    }
}