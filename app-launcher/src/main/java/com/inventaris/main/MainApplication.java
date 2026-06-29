package com.inventaris.main;

import com.formdev.flatlaf.FlatLightLaf;
import com.inventaris.main.ui.LoginFrame;

import javax.swing.SwingUtilities;

/**
 * Main entry point of the Kinetic Inventory Application.
 */
public class MainApplication {
    public static void main(String[] args) {
        // Setup FlatLaf theme globally
        FlatLightLaf.setup();

        // Launch the Login Frame on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}