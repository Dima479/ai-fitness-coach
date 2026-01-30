package aicoach.ui;

import aicoach.ai.OpenRouterClient;
import aicoach.dao.ChatDao;
import aicoach.dao.ProfileDao;
import aicoach.dao.ProgressDao;
import aicoach.model.ChatMessage;
import aicoach.model.ProgressEntry;
import aicoach.model.User;
import aicoach.model.UserProfile;
import aicoach.util.Dialogs;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class ChatPanel extends JPanel {
    private final ChatDao chatDao = new ChatDao();
    private final ProfileDao profileDao = new ProfileDao();
    private final ProgressDao progressDao = new ProgressDao();

    private static final String MODEL = "openai/gpt-4o-mini";
    private static final double TEMPERATURE = 0.4;
    private static final int MAX_TOKENS = 600;

    private final JTextArea chat = new JTextArea();
    private final JTextField input = new JTextField(40);

    public ChatPanel(User user) {
        setLayout(new BorderLayout());

        chat.setEditable(false);
        chat.setLineWrap(true);
        chat.setWrapStyleWord(true);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton send = new JButton("Trimite");
        JButton clear = new JButton("Sterge");

        bottom.add(new JLabel("Mesaj:"));
        bottom.add(input);
        bottom.add(send);
        bottom.add(clear);

        add(new JScrollPane(chat), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadHistory(user.id());

        send.addActionListener(e -> {
            String msg = input.getText().trim();
            if (msg.isEmpty()) return;

            input.setText("");
            send.setEnabled(false);

            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    UserProfile p = profileDao.get(user.id());
                    if (p == null) throw new RuntimeException("Completeaza profilul inainte de chat");

                    chatDao.insert(user.id(), "user", msg);

                    String system = """
                            Esti un antrenor AI de fitness. Raspunde in romana fara diacritice.
                            Fii practic si in siguranta: nu pune diagnostic si nu promite rezultate medicale.
                            Daca lipsesc date importante, pune cel mult 2 intrebari clare.
                            Pentru durere acuta/persistenta sau simptome serioase: recomanda consult medical.
                            Da raspunsuri structurate (liste cu puncte) cand e util.
                    """;

                    String context = buildContext(user.id(), p, msg);

                    OpenRouterClient ai = new OpenRouterClient();
                    return ai.chat(MODEL, system, context, TEMPERATURE, MAX_TOKENS);
                }

                @Override
                protected void done() {
                    try {
                        String reply = get();
                        if (reply == null || reply.isBlank()) reply = "(incearca din nou)";
                        chatDao.insert(user.id(), "assistant", reply);
                        loadHistory(user.id());
                    } catch (Exception ex) {
                        Dialogs.error(ChatPanel.this, ex.getMessage());
                        loadHistory(user.id());
                    } finally {
                        send.setEnabled(true);
                    }
                }
            }.execute();
        });

        clear.addActionListener(e -> {
            if (!Dialogs.confirm(this, "Stergi tot chatul?")) return;
            chatDao.clear(user.id());
            loadHistory(user.id());
        });
    }

    private void loadHistory(long userId) {
        List<ChatMessage> msgs = chatDao.list(userId, 500);
        StringBuilder sb = new StringBuilder();
        for (ChatMessage m : msgs) {
            sb.append("[").append(m.role()).append("] ").append(m.message()).append("\n\n");
        }
        chat.setText(sb.toString());
        chat.setCaretPosition(chat.getDocument().getLength());
    }

    private String buildContext(long userId, UserProfile p, String userMsg) {
        List<ProgressEntry> prog = progressDao.list(userId);
        int progTake = Math.min(5, prog.size());

        List<ChatMessage> allChat = chatDao.list(userId, 500);
        int chatTake = Math.min(20, allChat.size());
        List<ChatMessage> lastChat = allChat.subList(allChat.size() - chatTake, allChat.size());

        StringBuilder sb = new StringBuilder();

        sb.append("PROFIL:\n")
                .append("varsta=").append(p.age()).append(", ")
                .append("inaltime_cm=").append(p.heightCm()).append(", ")
                .append("greutate_kg=").append(p.weightKg()).append(", ")
                .append("obiectiv=").append(toRoValue(p.goal())).append(", ")
                .append("nivel_activitate=").append(toRoValue(p.activityLevel())).append(", ")
                .append("gen=").append(toRoValue(p.gender())).append("\n\n");

        sb.append("PROGRES (ultimele ").append(progTake).append("):\n");
        for (int i = 0; i < progTake; i++) {
            ProgressEntry x = prog.get(i);
            sb.append("- ").append(x.entryDate())
                    .append(": greutate=").append(x.weightKg())
                    .append(", calorii=").append(x.caloriesConsumed())
                    .append(", minute_antrenament=").append(x.workoutMin())
                    .append(", note=").append(x.notes())
                    .append("\n");
        }
        sb.append("\n");

        sb.append("CHAT (ultimele ").append(chatTake).append(" mesaje):\n");
        for (ChatMessage m : lastChat) {
            String text = m.message();
            if (text != null && text.length() > 600) text = text.substring(0, 600) + "…";
            sb.append("[").append(m.role()).append("] ").append(text).append("\n");
        }
        sb.append("\n");

        sb.append("Mesaj utilizator:\n").append(userMsg);

        return sb.toString();
    }

    private static String toRoValue(String value) {
        if (value == null) return null;
        return switch (value) {
            case "weight_loss" -> "slabire";
            case "bulking" -> "masa";
            case "maintenance" -> "mentinere";
            case "low" -> "scazut";
            case "moderate" -> "moderat";
            case "high" -> "ridicat";
            case "male" -> "masculin";
            case "female" -> "feminin";
            case "other" -> "altul";
            default -> value;
        };
    }
}
