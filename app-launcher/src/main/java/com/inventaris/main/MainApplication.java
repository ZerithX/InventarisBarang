package com.inventaris.main;

import com.formdev.flatlaf.FlatLightLaf;
import com.inventaris.main.ui.LoginFrame;

import javax.swing.SwingUtilities;

/**
 * Main entry point of the Kinetic Inventory Application.
 */
public class MainApplication {
    public static void main(String[] args) {
        // Load custom font via classpath (works after build and on any machine)
        try (java.io.InputStream fontStream = MainApplication.class.getResourceAsStream(
                "/com/inventaris/main/ui/components/Newsreader-VariableFont_opsz,wght.ttf")) {
            if (fontStream != null) {
                java.awt.Font customFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontStream);
                java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
            } else {
                System.err.println("Font Newsreader tidak ditemukan di resources.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setup FlatLaf theme globally
        FlatLightLaf.setup();

        // Launch the Login Frame on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
