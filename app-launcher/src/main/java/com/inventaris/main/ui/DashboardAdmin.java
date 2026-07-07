package com.inventaris.main.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.inventaris.inventory.repository.BarangRepository;
import com.inventaris.inventory.repository.KategoriRepository;
import com.inventaris.inventory.service.InventoryService;
import com.inventaris.main.ui.admin.DashboardOverviewPanel;
import com.inventaris.main.ui.admin.KelolaMasterDataPanel;
import com.inventaris.main.ui.admin.LaporanPanel;
import com.inventaris.main.ui.components.BottomSheetOverlay;
import com.inventaris.transaction.repository.TransaksiRepository;
import com.inventaris.transaction.service.TransactionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardAdmin extends JFrame {
    private final InventoryService inventoryService;
    private final TransactionService transactionService;

    // Navigation and CardLayout Components
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel btnDashboard;
    private JPanel btnKelola;
    private JPanel btnLaporan;

    // Modular Views
    private DashboardOverviewPanel overviewPanel;
    private KelolaMasterDataPanel kelolaPanel;
    private LaporanPanel laporanPanel;

    // Shared Bottom Sheet Overlay
    private BottomSheetOverlay bottomSheetOverlay;

    public DashboardAdmin() {
        this.inventoryService = new InventoryService(new BarangRepository(), new KategoriRepository());
        this.transactionService = new TransactionService(new TransaksiRepository(), new BarangRepository());

        // Inisialisasi BottomSheetOverlay SEBELUM initComponents dipanggil
        // agar objek panel (seperti KelolaMasterDataPanel) tidak menerima referensi null
        this.bottomSheetOverlay = new BottomSheetOverlay();

        initComponents();

        // Daftarkan sebagai GlassPane JFrame
        setGlassPane(bottomSheetOverlay);
    }

    private void initComponents() {
        setTitle("Sistem Inventaris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.decode("#F8F9FA"));
        setLayout(new BorderLayout());

        // CardLayout Setup
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.decode("#F8F9FA"));

        // Instantiate modular panels
        this.overviewPanel = new DashboardOverviewPanel(inventoryService, transactionService);
        this.laporanPanel = new LaporanPanel(inventoryService);
        
        // Define callback to refresh dashboard data when kelola panel updates database
        Runnable globalRefreshCallback = () -> {
            if (overviewPanel != null) {
                overviewPanel.loadDashboardData();
            }
            if (laporanPanel != null) {
                laporanPanel.loadLaporanData();
            }
        };
        
        // Pass bottomSheetOverlay, service, and refresh callback to modular kelolaPanel
        this.kelolaPanel = new KelolaMasterDataPanel(inventoryService, bottomSheetOverlay, globalRefreshCallback);

        // Add modular panels to card layout
        cardPanel.add(wrapInMainLayout(overviewPanel, "Sistem Inventaris"), "DASHBOARD");
        cardPanel.add(kelolaPanel, "KELOLA_DATA");
        cardPanel.add(laporanPanel, "LAPORAN");

        add(cardPanel, BorderLayout.CENTER);

        // Bottom Navigation
        JPanel bottomNav = new JPanel(new GridLayout(1, 3));
        bottomNav.setBackground(Color.WHITE);
        bottomNav.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#EBEBEB")),
                new EmptyBorder(10, 0, 10, 0)
        ));
        bottomNav.setPreferredSize(new Dimension(400, 70));

        btnDashboard = createNavButton("■", "Dashboard", true);
        btnKelola = createNavButton("□", "Kelola Data", false);
        btnLaporan = createNavButton("📈", "Laporan", false);

        btnDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                switchTab("DASHBOARD");
            }
        });

        btnKelola.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                switchTab("KELOLA_DATA");
            }
        });

        btnLaporan.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                switchTab("LAPORAN");
            }
        });

        bottomNav.add(btnDashboard);
        bottomNav.add(btnKelola);
        bottomNav.add(btnLaporan);

        add(bottomNav, BorderLayout.SOUTH);
    }

    /**
     * Wraps a panel with a consistent TopBar layout
     */
    private JPanel wrapInMainLayout(JPanel contentPanel, String title) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.decode("#F8F9FA"));

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#EBEBEB")),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Newsreader 16pt", Font.BOLD, 22));
        titleLabel.setForeground(Color.DARK_GRAY);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);
        JLabel lblRole = new JLabel("Admin");
        lblRole.setFont(new Font("Inter", Font.PLAIN, 14));
        lblRole.setForeground(Color.GRAY);

        JLabel lblLogout = new JLabel("<html><b style='color:#C83214;font-size:18px;'>&#x2192;]</b></html>");
        lblLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                com.inventaris.auth.domain.User currentUser = com.inventaris.auth.domain.Session.getLoggedInUser();
                if (currentUser != null) {
                    com.inventaris.core.util.ActivityLogger.log(
                        currentUser.getId(),
                        currentUser.getName(),
                        currentUser.getRole().toString(),
                        "LOGOUT",
                        "User " + currentUser.getName() + " berhasil logout"
                    );
                }
                com.inventaris.auth.domain.Session.clear();
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        rightPanel.add(lblRole);
        rightPanel.add(lblLogout);

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);

        wrapper.add(topBar, BorderLayout.NORTH);
        wrapper.add(contentPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createNavButton(String icon, String text, boolean isActive) {
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

    private void switchTab(String tabName) {
        cardLayout.show(cardPanel, tabName);
        updateNavButtonState(btnDashboard, "■", "Dashboard", "DASHBOARD".equals(tabName));
        updateNavButtonState(btnKelola, "□", "Kelola Data", "KELOLA_DATA".equals(tabName));
        updateNavButtonState(btnLaporan, "📈", "Laporan", "LAPORAN".equals(tabName));
        if ("KELOLA_DATA".equals(tabName)) {
            if (kelolaPanel != null) {
                kelolaPanel.switchSubTab(true);
            }
        } else if ("LAPORAN".equals(tabName)) {
            if (laporanPanel != null) {
                laporanPanel.loadLaporanData();
            }
        }
    }

    private void updateNavButtonState(JPanel btnPanel, String icon, String text, boolean isActive) {
        btnPanel.removeAll();
        
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
        
        btnPanel.revalidate();
        btnPanel.repaint();
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new DashboardAdmin().setVisible(true);
        });
    }
}
