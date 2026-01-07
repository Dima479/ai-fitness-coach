package aicoach.ui;

import aicoach.dao.ProgressDao;
import aicoach.model.ProgressEntry;
import aicoach.model.User;
import aicoach.util.Dialogs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public final class ProgressPanel extends JPanel {
    private final ProgressDao dao = new ProgressDao();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"id", "date", "weight", "calories", "workout_min", "notes"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);

    private final JTextField date = new JTextField(10);
    private final JTextField weight = new JTextField(8);
    private final JTextField calories = new JTextField(8);
    private final JTextField minutes = new JTextField(8);
    private final JTextField notes = new JTextField(30);

    public ProgressPanel(User user) {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        date.setText(LocalDate.now().toString());
        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(date);
        form.add(new JLabel("Weight:"));
        form.add(weight);
        form.add(new JLabel("Calories:"));
        form.add(calories);
        form.add(new JLabel("Minutes:"));
        form.add(minutes);
        form.add(new JLabel("Notes:"));
        form.add(notes);

        JButton add = new JButton("Add");
        JButton del = new JButton("Delete selected");
        form.add(add);
        form.add(del);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        reload(user.id());

        add.addActionListener(e -> {
            try {
                ProgressEntry entry = new ProgressEntry(
                        0,
                        user.id(),
                        date.getText().trim(),
                        parseDoubleOrNull(weight.getText()),
                        parseIntOrNull(calories.getText()),
                        parseIntOrNull(minutes.getText()),
                        notes.getText()
                );
                dao.insert(entry);
                reload(user.id());
            } catch (Exception ex) {
                Dialogs.error(this, ex.getMessage());
            }
        });

        del.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            long id = Long.parseLong(model.getValueAt(row, 0).toString());
            if (!Dialogs.confirm(this, "Delete entry " + id + "?")) return;
            dao.delete(id, user.id());
            reload(user.id());
        });
    }

    private void reload(long userId) {
        List<ProgressEntry> list = dao.list(userId);
        model.setRowCount(0);
        for (ProgressEntry e : list) {
            model.addRow(new Object[]{
                    e.id(), e.entryDate(), e.weightKg(), e.caloriesConsumed(), e.workoutMin(), e.notes()
            });
        }
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
