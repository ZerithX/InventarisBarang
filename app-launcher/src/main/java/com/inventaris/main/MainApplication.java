package com.inventaris.main;

import com.formdev.flatlaf.FlatLightLaf;
import com.inventaris.main.ui.LoginFrame;

import javax.swing.SwingUtilities;

/**
 * Main entry point of the Kinetic Inventory Application.
 */
public class MainApplication {
    public static void main(String[] args) {
        // Load custom font so it can be referenced by its family name
        try {
            java.io.File fontFile = new java.io.File("app-launcher/src/main/java/com/inventaris/main/ui/components/Newsreader-VariableFont_opsz,wght.ttf");
            java.awt.Font customFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontFile);
            java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
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
