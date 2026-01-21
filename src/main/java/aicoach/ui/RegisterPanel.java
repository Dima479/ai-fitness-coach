package aicoach.ui;

import aicoach.model.User;
import aicoach.service.AuthService;
import aicoach.util.Dialogs;

import javax.swing.*;
import java.awt.*;

public final class RegisterPanel extends JPanel {
    private final AuthService auth = new AuthService();
    private final JTextField email = new JTextField(24);
    private final JPasswordField pass = new JPasswordField(24);
    private final JPasswordField pass2 = new JPasswordField(24);

    public RegisterPanel(AppFrame frame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create account");
        title.setFont(title.getFont().deriveFont(22f));

        JButton btnCreate = new JButton("Create");
        JButton btnBack = new JButton("Back to login");

        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        add(title, gc);

        gc.gridwidth = 1; gc.gridy++;
        add(new JLabel("Email:"), gc);
        gc.gridx = 1;
        add(email, gc);

        gc.gridx = 0; gc.gridy++;
        add(new JLabel("Password:"), gc);
        gc.gridx = 1;
        add(pass, gc);

        gc.gridx = 0; gc.gridy++;
        add(new JLabel("Repeat password:"), gc);
        gc.gridx = 1;
        add(pass2, gc);

        gc.gridx = 0; gc.gridy++; gc.gridwidth = 2;
        add(btnCreate, gc);

        gc.gridy++;
        add(btnBack, gc);

        btnCreate.addActionListener(e -> {
            try {
                String pw1 = new String(pass.getPassword());
                String pw2 = new String(pass2.getPassword());
                if (!pw1.equals(pw2)) {
                    Dialogs.error(this, "Parolele nu coincid.");
                    return;
                }
                User u = auth.register(email.getText(), pw1);
                Dialogs.info(this, "Cont creat. Ești logat.");
                frame.onAuthenticated(u, true);
            } catch (Exception ex) {
                Dialogs.error(this, ex.getMessage());
            }
        });

        btnBack.addActionListener(e -> frame.showLogin());
    }

    public void reset() {
        email.setText("");
        pass.setText("");
        pass2.setText("");
    }
}
