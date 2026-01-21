package aicoach;

import aicoach.db.Db;
import aicoach.ui.AppFrame;
import aicoach.util.Dialogs;

import javax.swing.*;

public final class App {
    public static void main(String[] args) {
        try {
            Db.init();
        } catch (Exception e) {
            e.printStackTrace(); // Afiseaza eroarea completa in terminal pentru debugging
            Dialogs.error(null, "Eroare fatala la initializarea bazei de date: " + e.getMessage());
            System.exit(1);
        }
        SwingUtilities.invokeLater(() -> {
            AppFrame frame = new AppFrame();
            frame.setVisible(true);
        });
    }
}
