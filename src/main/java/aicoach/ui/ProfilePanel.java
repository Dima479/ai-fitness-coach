package aicoach.ui;

import aicoach.dao.ProfileDao;
import aicoach.model.User;
import aicoach.model.UserProfile;
import aicoach.util.Dialogs;

import javax.swing.*;
import java.awt.*;

public final class ProfilePanel extends JPanel {
    private final ProfileDao dao = new ProfileDao();
    private final long userId;

    private final JTextField age = new JTextField(6);
    private final JTextField height = new JTextField(6);
    private final JTextField weight = new JTextField(6);
    private final JComboBox<String> goal = new JComboBox<>(new String[]{"weight_loss", "bulking", "maintenance"});
    private final JComboBox<String> activity = new JComboBox<>(new String[]{"low", "moderate", "high"});
    private final JComboBox<String> gender = new JComboBox<>(new String[]{"male", "female", "other"});

    private final JButton save = new JButton("Save profile");
    private final JLabel updatedAt = new JLabel("-");

    public ProfilePanel(User user) {
        this.userId = user.id();
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Age:"), gc);
        gc.gridx = 1; form.add(age, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Height (cm):"), gc);
        gc.gridx = 1; form.add(height, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Weight (kg):"), gc);
        gc.gridx = 1; form.add(weight, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Goal:"), gc);
        gc.gridx = 1; form.add(goal, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Activity level:"), gc);
        gc.gridx = 1; form.add(activity, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Gender:"), gc);
        gc.gridx = 1; form.add(gender, gc); y++;

        gc.gridx = 0; gc.gridy = y; gc.gridwidth = 2;
        form.add(save, gc); y++;

        gc.gridy = y;
        form.add(updatedAt, gc);

        add(form, BorderLayout.NORTH);

        load(userId);

        save.addActionListener(e -> {
            try {
                UserProfile p = build(userId);
                dao.upsert(p);
                Dialogs.info(this, "Saved.");
                load(userId);
            } catch (Exception ex) {
                Dialogs.error(this, ex.getMessage());
            }
        });
    }

    public void reload() {
        load(userId);
    }

    private void load(long userId) {
        UserProfile p = dao.get(userId);
        if (p == null) {
            updatedAt.setText("Updated at: -");
            return;
        }

        age.setText(p.age() == null ? "" : String.valueOf(p.age()));
        height.setText(p.heightCm() == null ? "" : String.valueOf(p.heightCm()));
        weight.setText(p.weightKg() == null ? "" : String.valueOf(p.weightKg()));
        if (p.goal() != null) goal.setSelectedItem(p.goal());
        if (p.activityLevel() != null) activity.setSelectedItem(p.activityLevel());
        if (p.gender() != null) gender.setSelectedItem(p.gender());

        updatedAt.setText("Updated at: " + (p.updatedAt() == null ? "-" : p.updatedAt()));
    }

    private UserProfile build(long userId) {
        Integer a = parseIntOrNull(age.getText());
        Integer h = parseIntOrNull(height.getText());
        Double w = parseDoubleOrNull(weight.getText());
        return new UserProfile(
                userId,
                a,
                h,
                w,
                (String) goal.getSelectedItem(),
                (String) activity.getSelectedItem(),
                (String) gender.getSelectedItem(),
                null
        );
    }

    private static Integer parseIntOrNull(String s) {
        String t = s == null ? "" : s.trim();
        if (t.isEmpty()) return null;
        return Integer.parseInt(t);
    }

    private static Double parseDoubleOrNull(String s) {
        String t = s == null ? "" : s.trim();
        if (t.isEmpty()) return null;
        return Double.parseDouble(t);
    }
}
