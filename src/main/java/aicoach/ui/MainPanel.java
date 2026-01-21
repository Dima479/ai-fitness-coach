package aicoach.ui;

import aicoach.model.User;

import javax.swing.*;
import java.awt.*;

public final class MainPanel extends JPanel {
    public MainPanel(AppFrame frame, User user, boolean showProfile) {
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Logged in as: " + user.email());
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton profile = new JButton("Profile");
        JButton logout = new JButton("Logout");
        JPanel top = new JPanel(new BorderLayout());
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topRight.add(profile);
        topRight.add(logout);
        top.add(header, BorderLayout.WEST);
        top.add(topRight, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Chat", new ChatPanel(user));
        tabs.addTab("Plans", new PlansPanel(user));
        tabs.addTab("Progress", new ProgressPanel(user));

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        logout.addActionListener(e -> frame.logout());

        ProfilePanel profilePanel = new ProfilePanel(user);
        JDialog profileDialog = new JDialog(frame, "Profile", Dialog.ModalityType.APPLICATION_MODAL);
        profileDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        profileDialog.setContentPane(profilePanel);
        profileDialog.pack();

        profile.addActionListener(e -> {
            profilePanel.reload();
            profileDialog.pack();
            profileDialog.setLocationRelativeTo(frame);
            profileDialog.setVisible(true);
        });

        if (showProfile) {
            SwingUtilities.invokeLater(() -> {
                profilePanel.reload();
                profileDialog.pack();
                profileDialog.setLocationRelativeTo(frame);
                profileDialog.setVisible(true);
            });
        }
    }
}
