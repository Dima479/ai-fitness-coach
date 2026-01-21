package aicoach.ui;

import aicoach.dao.PlanDao;
import aicoach.dao.ProfileDao;
import aicoach.model.Plan;
import aicoach.model.User;
import aicoach.model.UserProfile;
import aicoach.service.CoachService;
import aicoach.util.Dialogs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public final class PlansPanel extends JPanel {
    private final PlanDao planDao = new PlanDao();
    private final ProfileDao profileDao = new ProfileDao();
    private final CoachService coach = new CoachService();

    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"type", "created_at"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextArea content = new JTextArea();
    private List<Plan> plans = List.of();

    public PlansPanel(User user) {
        setLayout(new BorderLayout());

        JPanel left = new JPanel(new BorderLayout());
        left.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton genWorkout = new JButton("Generate WORKOUT");
        JButton genNutrition = new JButton("Generate NUTRITION");
        JButton delete = new JButton("Delete selected");
        leftButtons.add(genWorkout);
        leftButtons.add(genNutrition);
        leftButtons.add(delete);
        left.add(leftButtons, BorderLayout.SOUTH);

        content.setLineWrap(true);
        content.setWrapStyleWord(true);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, new JScrollPane(content));
        split.setDividerLocation(500);

        add(split, BorderLayout.CENTER);

        reload(user.id());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            if (row >= plans.size()) return;
            content.setText(plans.get(row).content());
        });

        genWorkout.addActionListener(e -> {
            try {
                UserProfile p = profileDao.get(user.id());
                if (p == null) {
                    Dialogs.error(this, "First, colplete your profile.");
                    return;
                }
                String txt = coach.generateWorkoutPlan(p);
                planDao.insert(user.id(), "WORKOUT", txt);
                reload(user.id());
            } catch (Exception ex) {
                Dialogs.error(this, ex.getMessage());
            }
        });

        genNutrition.addActionListener(e -> {
            try {
                UserProfile p = profileDao.get(user.id());
                if (p == null) {
                    Dialogs.error(this, "First, colplete your profile.");
                    return;
                }
                String txt = coach.generateNutritionPlan(p);
                planDao.insert(user.id(), "NUTRITION", txt);
                reload(user.id());
            } catch (Exception ex) {
                Dialogs.error(this, ex.getMessage());
            }
        });

        delete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            if (row >= plans.size()) return;
            if (!Dialogs.confirm(this, "Delete selected plan?")) return;
            planDao.delete(plans.get(row).id(), user.id());
            reload(user.id());
            content.setText("");
        });
    }

    private void reload(long userId) {
        plans = planDao.list(userId);
        model.setRowCount(0);
        for (Plan p : plans) model.addRow(new Object[]{p.planType(), p.createdAt()});
        if (model.getRowCount() > 0) table.setRowSelectionInterval(0, 0);
    }
}
