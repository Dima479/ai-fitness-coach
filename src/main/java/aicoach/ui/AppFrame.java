package aicoach.ui;

import aicoach.model.User;

import javax.swing.*;
import java.awt.*;

public final class AppFrame extends JFrame {
    private final CardLayout layout = new CardLayout();
    private final JPanel root = new JPanel(layout);

    private final LoginPanel loginPanel;
    private final RegisterPanel registerPanel;
    private MainPanel mainPanel;

    public AppFrame() {
        super("Antrenor fitness AI");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 650);
        setLocationRelativeTo(null);

        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);

        root.add(loginPanel, "login");
        root.add(registerPanel, "register");

        setContentPane(root);
        showLogin();
    }

    public void showLogin() {
        loginPanel.reset();
        layout.show(root, "login");
    }

    public void showRegister() {
        registerPanel.reset();
        layout.show(root, "register");
    }

    public void onAuthenticated(User user) {
        onAuthenticated(user, false);
    }

    public void onAuthenticated(User user, boolean showProfile) {
        if (mainPanel != null) root.remove(mainPanel);
        mainPanel = new MainPanel(this, user, showProfile);
        root.add(mainPanel, "main");
        layout.show(root, "main");
        revalidate();
        repaint();
    }

    public void logout() {
        showLogin();
    }
}
