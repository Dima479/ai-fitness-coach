package aicoach;

import aicoach.db.Db;
import aicoach.ui.AppFrame;
import aicoach.util.Dialogs;

import javax.swing.*;

public final class App {
    public static void main(String[] args) {
        
        Db.init();

        SwingUtilities.invokeLater(() -> {
            AppFrame frame = new AppFrame();
            frame.setVisible(true);
        });
    }
}
