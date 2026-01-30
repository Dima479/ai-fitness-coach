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
    private final JComboBox<String> goal = new JComboBox<>(new String[]{"slabire", "masa", "mentinere"});
    private final JComboBox<String> activity = new JComboBox<>(new String[]{"scazut", "moderat", "ridicat"});
    private final JComboBox<String> gender = new JComboBox<>(new String[]{"masculin", "feminin", "altul"});

    private final JButton save = new JButton("Salveaza profil");
    private final JLabel updatedAt = new JLabel("-");

    public ProfilePanel(User user) {
        this.userId = user.id();
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Varsta:"), gc);
        gc.gridx = 1; form.add(age, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Inaltime (cm):"), gc);
        gc.gridx = 1; form.add(height, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Greutate (kg):"), gc);
        gc.gridx = 1; form.add(weight, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Obiectiv:"), gc);
        gc.gridx = 1; form.add(goal, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Nivel activitate:"), gc);
        gc.gridx = 1; form.add(activity, gc); y++;

        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Gen:"), gc);
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
                Dialogs.info(this, "Salvat.");
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
            updatedAt.setText("Actualizat la: -");
            return;
        }

        age.setText(p.age() == null ? "" : String.valueOf(p.age()));
        height.setText(p.heightCm() == null ? "" : String.valueOf(p.heightCm()));
        weight.setText(p.weightKg() == null ? "" : String.valueOf(p.weightKg()));
        if (p.goal() != null) goal.setSelectedItem(mapGoalToUi(p.goal()));
        if (p.activityLevel() != null) activity.setSelectedItem(mapActivityToUi(p.activityLevel()));
        if (p.gender() != null) gender.setSelectedItem(mapGenderToUi(p.gender()));

        updatedAt.setText("Actualizat la: " + (p.updatedAt() == null ? "-" : p.updatedAt()));
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

    private static String mapGoalToUi(String value) {
        if (value == null) return null;
        return switch (value) {
            case "weight_loss" -> "slabire";
            case "bulking" -> "masa";
            case "maintenance" -> "mentinere";
            default -> value;
        };
    }

    private static String mapActivityToUi(String value) {
        if (value == null) return null;
        return switch (value) {
            case "low" -> "scazut";
            case "moderate" -> "moderat";
            case "high" -> "ridicat";
            default -> value;
        };
    }

    private static String mapGenderToUi(String value) {
        if (value == null) return null;
        return switch (value) {
            case "male" -> "masculin";
            case "female" -> "feminin";
            case "other" -> "altul";
            default -> value;
        };
    }
}
