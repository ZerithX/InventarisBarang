package com.inventaris.main.ui.staff;

import com.inventaris.auth.domain.User;
import com.inventaris.inventory.service.InventoryService;
import com.inventaris.main.ui.components.BottomSheetOverlay;
import com.inventaris.transaction.domain.TipeTransaksi;
import com.inventaris.transaction.domain.Transaksi;
import com.inventaris.transaction.service.TransactionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StaffOverviewPanel extends JPanel {
    private final User staffUser;
    private final TransactionService transactionService;
    private final InventoryService inventoryService;
    private final BottomSheetOverlay bottomSheetOverlay;

    private JPanel historyContainer;

    public StaffOverviewPanel(User staffUser, TransactionService transactionService,
                              InventoryService inventoryService, BottomSheetOverlay bottomSheetOverlay) {
        this.staffUser = staffUser;
        this.transactionService = transactionService;
        this.inventoryService = inventoryService;
        this.bottomSheetOverlay = bottomSheetOverlay;

        initComponents();
        loadHistoryData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

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

        // Action Cards (Input Barang Masuk & Keluar)
        JPanel cardMasuk = createActionCard("Input Barang Masuk", "Catat penerimaan stok baru", new Color(235, 245, 255), new Color(0, 102, 204), "\u21A1");
        cardMasuk.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                bottomSheetOverlay.openSheet(new FormTransaksiMasukPanel(staffUser, inventoryService, transactionService, bottomSheetOverlay, () -> loadHistoryData()), 550);
            }
        });

        JPanel cardKeluar = createActionCard("Input Barang Keluar", "Catat penggunaan atau pengeluaran stok", new Color(255, 240, 235), new Color(204, 51, 0), "\u219F");
        cardKeluar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                bottomSheetOverlay.openSheet(new FormTransaksiKeluarPanel(staffUser, inventoryService, transactionService, bottomSheetOverlay, () -> loadHistoryData()), 520);
            }
        });

        centerPanel.add(cardMasuk);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(cardKeluar);

        centerPanel.add(Box.createVerticalStrut(40));

        // History Title Label
        JLabel lblHistory = new JLabel("RIWAYAT TRANSAKSI");
        lblHistory.setFont(new Font("Inter", Font.BOLD, 12));
        lblHistory.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(lblHistory);
        centerPanel.add(Box.createVerticalStrut(15));

        // History Items Container
        historyContainer = new JPanel();
        historyContainer.setLayout(new BoxLayout(historyContainer, BoxLayout.Y_AXIS));
        historyContainer.setBackground(Color.WHITE);
        historyContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(historyContainer);

        centerPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadHistoryData() {
        if (historyContainer == null) return;
        historyContainer.removeAll();

        try {
            List<Transaksi> list = transactionService.getTransactionsByUser(staffUser.getId());
            if (list.isEmpty()) {
                JLabel lblEmpty = new JLabel("Belum ada riwayat transaksi.");
                lblEmpty.setFont(new Font("Inter", Font.ITALIC, 14));
                lblEmpty.setForeground(Color.GRAY);
                lblEmpty.setAlignmentX(Component.LEFT_ALIGNMENT);
                historyContainer.add(lblEmpty);
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm 'WIB'");
                for (Transaksi t : list) {
                    String barangNama = t.getBarang() != null ? t.getBarang().getNama() : "Barang Tidak Dikenal";
                    String formatTime = t.getCreatedAt() != null ? t.getCreatedAt().format(formatter) : "-";
                    String prefix = t.getTipeTransaksi() == TipeTransaksi.MASUK ? "+" : "-";
                    boolean isMasuk = t.getTipeTransaksi() == TipeTransaksi.MASUK;

                    historyContainer.add(createTransactionItem(
                            barangNama,
                            formatTime,
                            prefix + t.getJumlah(),
                            t.getTipeTransaksi().name(),
                            isMasuk
                    ));
                    historyContainer.add(Box.createVerticalStrut(10));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JLabel lblError = new JLabel("Gagal memuat riwayat transaksi.");
            lblError.setFont(new Font("Inter", Font.ITALIC, 14));
            lblError.setForeground(Color.RED);
            lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
            historyContainer.add(lblError);
        }

        historyContainer.revalidate();
        historyContainer.repaint();
    }

    private JPanel createActionCard(String title, String desc, Color bg, Color textCol, String unicodeIcon) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(800, 80));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 15));
        lblTitle.setForeground(textCol);

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Inter", Font.PLAIN, 12));
        lblDesc.setForeground(Color.GRAY);

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(lblDesc);

        JLabel lblIcon = new JLabel(unicodeIcon);
        lblIcon.setFont(new Font("Inter", Font.PLAIN, 26));
        lblIcon.setForeground(textCol);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }

    private JPanel createTransactionItem(String title, String date, String amount, String status, boolean isMasuk) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#F9FAFB"));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(800, 65));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 14));
        lblTitle.setForeground(Color.decode("#111827"));

        JLabel lblDate = new JLabel(date);
        lblDate.setFont(new Font("Inter", Font.PLAIN, 12));
        lblDate.setForeground(Color.decode("#6B7280"));

        leftPanel.add(lblTitle);
        leftPanel.add(Box.createVerticalStrut(4));
        leftPanel.add(lblDate);

        // Status Badge / Amount
        JLabel lblAmount = new JLabel(amount, SwingConstants.RIGHT);
        lblAmount.setFont(new Font("Inter", Font.BOLD, 15));
        lblAmount.setForeground(isMasuk ? Color.decode("#10B981") : Color.decode("#EF4444"));

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(lblAmount, BorderLayout.EAST);

        return panel;
    }
}
