package com.inventaris.main.ui.components;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Custom GlassPane Overlay Panel Class for Bottom Sheet.
 * Provides a dims background overlay and sliding animation from the bottom.
 */
public class BottomSheetOverlay extends JPanel {
    private JPanel sheetPanel;
    private JPanel dialogPanel;
    private int sheetHeight = 480;
    private float alpha = 0f;
    private Timer timer;
    private int currentY;
    private boolean isDialogMode = false;

    public BottomSheetOverlay() {
        setLayout(null);
        setOpaque(false);

        // Close sheet when clicking outside the panel (only in bottom sheet mode)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isDialogMode && sheetPanel != null) {
                    Point p = e.getPoint();
                    if (!sheetPanel.getBounds().contains(p)) {
                        closeSheet();
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // Draw dark background overlay
        g2.setColor(new Color(0, 0, 0, (int) (alpha * 120)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    public void openSheet(JPanel contentForm, int height) {
        this.isDialogMode = false;
        this.dialogPanel = null;
        removeAll();

        // Bottom sheet panel container
        sheetPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                int arc = 28;
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + arc, arc, arc);
                g2.dispose();
            }
        };
        sheetPanel.setOpaque(false);
        sheetPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Top Drag Handle decoration
        JPanel topHandlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#DCDCDC"));
                g2.fillRoundRect((getWidth() - 40) / 2, 4, 40, 6, 6, 6);
                g2.dispose();
            }
        };
        topHandlePanel.setOpaque(false);
        topHandlePanel.setPreferredSize(new Dimension(getWidth(), 16));
        sheetPanel.add(topHandlePanel, BorderLayout.NORTH);
        sheetPanel.add(contentForm, BorderLayout.CENTER);

        add(sheetPanel);

        // Set animation starting conditions
        alpha = 0f;
        currentY = getHeight();
        sheetPanel.setBounds(0, currentY, getWidth(), sheetHeight);

        setVisible(true);

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(10, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int targetY = getHeight() - sheetHeight;
                if (currentY > targetY) {
                    currentY -= 25; // Speed of animation
                    if (currentY < targetY) currentY = targetY;
                    alpha = (float) (getHeight() - currentY) / sheetHeight;
                    sheetPanel.setBounds(0, currentY, getWidth(), sheetHeight);
                    repaint();
                } else {
                    timer.stop();
                }
            }
        });
        timer.start();
    }

    public void closeSheet() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(10, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int targetY = getHeight();
                if (currentY < targetY) {
                    currentY += 25;
                    if (currentY > targetY) currentY = targetY;
                    alpha = 1.0f - (float) (currentY - (getHeight() - sheetHeight)) / sheetHeight;
                    if (alpha < 0) alpha = 0;
                    sheetPanel.setBounds(0, currentY, getWidth(), sheetHeight);
                    repaint();
                } else {
                    timer.stop();
                    setVisible(false);
                }
            }
        });
        timer.start();
    }

    public void openDialog(JPanel dialogContent, int width, int height) {
        this.isDialogMode = true;
        this.sheetPanel = null;
        removeAll();

        // Custom centered panel container with rounded corners and drop shadow
        dialogPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                int arc = 20;
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.dispose();
            }
        };
        dialogPanel.setOpaque(false);
        dialogPanel.add(dialogContent, BorderLayout.CENTER);

        add(dialogPanel);

        // Center position calculation
        int x = (getWidth() - width) / 2;
        int y = (getHeight() - height) / 2;
        dialogPanel.setBounds(x, y, width, height);

        // Fade in alpha
        alpha = 0f;
        setVisible(true);

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(10, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (alpha < 1f) {
                    alpha += 0.1f;
                    if (alpha > 1f) alpha = 1f;
                    repaint();
                } else {
                    timer.stop();
                }
            }
        });
        timer.start();
    }

    public void closeDialog() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(10, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (alpha > 0f) {
                    alpha -= 0.1f;
                    if (alpha < 0f) alpha = 0f;
                    repaint();
                } else {
                    timer.stop();
                    setVisible(false);
                    removeAll();
                }
            }
        });
        timer.start();
    }
}
