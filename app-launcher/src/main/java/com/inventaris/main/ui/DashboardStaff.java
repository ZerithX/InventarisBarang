package com.inventaris.main.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.inventaris.auth.domain.User;
import com.inventaris.auth.domain.Staff;
import com.inventaris.inventory.repository.BarangRepository;
import com.inventaris.inventory.repository.KategoriRepository;
import com.inventaris.inventory.service.InventoryService;
import com.inventaris.main.ui.components.BottomSheetOverlay;
import com.inventaris.transaction.repository.TransaksiRepository;
import com.inventaris.transaction.service.TransactionService;
import com.inventaris.main.ui.staff.StaffOverviewPanel;
import com.inventaris.main.ui.staff.KatalogBarangPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardStaff extends JFrame {
    private final User staffUser;
    private final TransactionService transactionService;
    private final InventoryService inventoryService;

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel btnDashboard;
    private JPanel btnBarang;
    private JPanel btnLaporan;

    // Modular components
    private StaffOverviewPanel overviewPanel;
    private KatalogBarangPanel katalogPanel;
    private JPanel topBar;
    private BottomSheetOverlay bottomSheetOverlay;

    public DashboardStaff(User staffUser) {
        this.staffUser = staffUser;
        this.transactionService = new TransactionService(new TransaksiRepository(), new BarangRepository());
        this.inventoryService = new InventoryService(new BarangRepository(), new KategoriRepository());

        // Inisialisasi BottomSheetOverlay SEBELUM initComponents dipanggil
        this.bottomSheetOverlay = new BottomSheetOverlay();

        initComponents();

        // Register custom GlassPane
        setGlassPane(bottomSheetOverlay);
    }

    private void initComponents() {
        setTitle("Sistem Inventaris - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 750);
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

        // Instantiate modular panels
        this.overviewPanel = new StaffOverviewPanel(staffUser, transactionService, inventoryService, bottomSheetOverlay);
        this.katalogPanel = new KatalogBarangPanel();

        cardPanel.add(overviewPanel, "DASHBOARD");
        cardPanel.add(katalogPanel, "KATALOG");

        this.topBar = createTopBar();
        mainContainer.add(topBar, BorderLayout.NORTH);
        mainContainer.add(cardPanel, BorderLayout.CENTER);
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
        lblBrand.setFont(new Font("Newsreader 16pt", Font.PLAIN, 24));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);
        JLabel lblStaff = new JLabel(staffUser != null ? staffUser.getName() : "Staff");
        lblStaff.setFont(new Font("Inter", Font.PLAIN, 14));
        lblStaff.setForeground(Color.GRAY);

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

    private JPanel createBottomNav() {
        JPanel navBar = new JPanel(new GridLayout(1, 3));
        navBar.setBackground(Color.WHITE);
        navBar.setPreferredSize(new Dimension(getWidth(), 70));
        navBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        btnDashboard = createNavItem("Dashboard", "\u25A6", true);
        btnBarang = createNavItem("Barang", "\u25A4", false);
        btnLaporan = createNavItem("Laporan", "\uD83D\uDCCA", false);

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
        
        // Sembunyikan topBar ketika di tab barang (KATALOG), tampilkan di tab lainnya
        if (topBar != null) {
            topBar.setVisible(!"KATALOG".equals(tabName));
        }

        if ("KATALOG".equals(tabName)) {
            if (katalogPanel != null) {
                katalogPanel.refreshKatalog();
                katalogPanel.resetScroll();
            }
        }
        revalidate();
        repaint();
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
            Staff dummyStaff = new Staff("staff_dummy", "password");
            dummyStaff.setId("9a6fca31-6fe6-11f1-aefe-2c1b3ae3ac30");
            new DashboardStaff(dummyStaff).setVisible(true);
        });
    }
}
