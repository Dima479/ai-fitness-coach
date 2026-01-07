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
        JButton send = new JButton("Send");
        JButton clear = new JButton("Clear");

        bottom.add(new JLabel("Message:"));
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
                    if (p == null) throw new RuntimeException("Complete your profile before chat");

                    chatDao.insert(user.id(), "user", msg);

                    String system = """
                            You are an AI fitness coach. Respond in English.
                            Be practical and safe: don’t diagnose and don’t promise medical results.
                            If important data is missing, ask at most 2 clear questions.
                            For sharp/persistent pain or serious symptoms: recommend medical consultation.
                            Give structured answers (bullet points) when useful.
                    """;

                    String context = buildContext(user.id(), p, msg);

                    OpenRouterClient ai = new OpenRouterClient();
                    return ai.chat(MODEL, system, context, TEMPERATURE, MAX_TOKENS);
                }

                @Override
                protected void done() {
                    try {
                        String reply = get();
                        if (reply == null || reply.isBlank()) reply = "(try again)";
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
            if (!Dialogs.confirm(this, "delete all chat?")) return;
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
                .append("age=").append(p.age()).append(", ")
                .append("height_cm=").append(p.heightCm()).append(", ")
                .append("weight_kg=").append(p.weightKg()).append(", ")
                .append("goal=").append(p.goal()).append(", ")
                .append("activity_level=").append(p.activityLevel()).append(", ")
                .append("gender=").append(p.gender()).append("\n\n");

        sb.append("PROGRES (ultimele ").append(progTake).append("):\n");
        for (int i = 0; i < progTake; i++) {
            ProgressEntry x = prog.get(i);
            sb.append("- ").append(x.entryDate())
                    .append(": weight=").append(x.weightKg())
                    .append(", calories=").append(x.caloriesConsumed())
                    .append(", workout_min=").append(x.workoutMin())
                    .append(", notes=").append(x.notes())
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

        sb.append("User messsage:\n").append(userMsg);

        return sb.toString();
    }
}
