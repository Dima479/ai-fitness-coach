package aicoach.ui;

import aicoach.model.User;
import aicoach.service.AuthService;
import aicoach.util.Dialogs;

import javax.swing.*;
import java.awt.*;

public final class LoginPanel extends JPanel {
    private final AuthService auth = new AuthService();

    public LoginPanel(AppFrame frame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Login");
        title.setFont(title.getFont().deriveFont(22f));

        JTextField email = new JTextField(24);
        JPasswordField pass = new JPasswordField(24);

        JButton btnLogin = new JButton("Login");
        JButton btnGoRegister = new JButton("Create account");

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

        gc.gridx = 0; gc.gridy++; gc.gridwidth = 2;
        add(btnLogin, gc);

        gc.gridy++;
        add(btnGoRegister, gc);

        btnLogin.addActionListener(e -> {
            try {
                String em = email.getText();
                String pw = new String(pass.getPassword());
                User u = auth.login(em, pw);
                if (u == null) {
                    Dialogs.error(this, "Email/parola greșite.");
                    return;
                }
                frame.onAuthenticated(u);
            } catch (Exception ex) {
                Dialogs.error(this, ex.getMessage());
            }
        });

        btnGoRegister.addActionListener(e -> frame.showRegister());
    }
}
