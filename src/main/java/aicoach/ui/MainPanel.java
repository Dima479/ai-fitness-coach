package aicoach.ui;

import aicoach.model.User;

import javax.swing.*;
import java.awt.*;

public final class MainPanel extends JPanel {
    public MainPanel(AppFrame frame, User user) {
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Logged in as: " + user.email());
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton logout = new JButton("Logout");
        JPanel top = new JPanel(new BorderLayout());
        top.add(header, BorderLayout.WEST);
        top.add(logout, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Profile", new ProfilePanel(user));
        tabs.addTab("Plans", new PlansPanel(user));
        tabs.addTab("Progress", new ProgressPanel(user));
        tabs.addTab("Chat", new ChatPanel(user));

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        logout.addActionListener(e -> frame.logout());
    }
}
