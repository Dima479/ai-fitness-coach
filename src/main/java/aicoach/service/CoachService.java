package aicoach.service;

import aicoach.ai.OpenRouterClient;
import aicoach.model.UserProfile;
import aicoach.util.Validators;

/**
 * @file CoachService.java
 * @brief Serviciu care genereaza planuri de workout si nutritie folosind AI (OpenRouter).
 *
 * Construieste prompt-uri (system + user) pe baza profilului si apeleaza OpenRouterClient.
 */
public final class CoachService {

    /** Model implicit: ia din OPENROUTER_MODEL, iar daca lipseste foloseste fallback. */
    private static final String DEFAULT_MODEL =
            System.getenv().getOrDefault("OPENROUTER_MODEL", "openai/gpt-4o-mini");

    /** Clientul care trimite request-urile HTTP catre OpenRouter. */
    private final OpenRouterClient ai = new OpenRouterClient();

    /** Modelul folosit de acest serviciu (mereu DEFAULT_MODEL in aceasta varianta). */
    private final String model = DEFAULT_MODEL;

    /**
     * Genereaza un plan de antrenament (3 zile) folosind AI, pe baza profilului.
     *
     * @param p Profilul utilizatorului (poate fi null; atunci se trimit valori "unknown").
     * @return Textul planului de antrenament generat de AI.
     * @throws RuntimeException Daca apelul catre OpenRouter esueaza.
     */
    public String generateWorkoutPlan(UserProfile p) {
        String system = baseSystemPrompt();

        String user = String.join("\n",
                "Task: Build a workout plan.",
                "Constraints:",
                "- Give a 3-day workout plan",
                "- Make as short as possible, using less text but be precise",
                "- Include warmup, main lifts, progression rule, and cardio/steps.",
                "- Keep it realistic and safe for general population. If medical/pain issues: advise specialist.",
                "",
                "User profile:",
                profileBlock(p)
        );

        return ai.chat(model, system, user, 0.1, 900);
    }

    /**
     * Genereaza un plan de nutritie folosind AI, pe baza profilului.
     *
     * @param p Profilul utilizatorului (poate fi null; atunci se trimit valori "unknown").
     * @return Textul planului de nutritie generat de AI.
     * @throws RuntimeException Daca apelul catre OpenRouter esueaza.
     */
    public String generateNutritionPlan(UserProfile p) {
        String system = baseSystemPrompt();

        String user = String.join("\n",
                "Task: Build a nutrition plan.",
                "Constraints:",
                "- Give daily targets (calories, protein, fats, carbs) and a simple meal template.",
                "- If weight is missing, assume 75kg but mention its an assumption.",
                "- Keep it practical (Romania style foods ok).",
                "- Add a simple adjustment rule based on weekly weight trend.",
                "",
                "User profile:",
                profileBlock(p)
        );

        return ai.chat(model, system, user, 0.35, 900);
    }

    /**
     * Construieste mesajul de sistem (reguli generale) pentru model.
     *
     * @return Textul system prompt folosit la toate cererile.
     */
    private static String baseSystemPrompt() {
        return String.join("\n",
                "You are an evidence-based fitness and nutrition coach.",
                "You give safe, realistic advice and avoid medical diagnosis.",
                "If the user mentions sharp pain, illness, eating disorders, or medical risk: recommend consulting a professional.",
                "Prefer actionable plans: bullets, numbers, clear steps.",
                "Do not invent personal data; if missing, state assumptions.",
                "Always respond in Romanian without diacritics."
        );
    }

    /**
     * Construieste un bloc text cu campurile profilului pentru a fi inclus in prompt.
     * Normalizeaza valorile lipsa ca "unknown".
     *
     * @param p Profilul utilizatorului.
     * @return Bloc text cu campurile profilului pe linii separate.
     */
    private static String profileBlock(UserProfile p) {
        if (p == null) {
            return "- userId: unknown\n- age: unknown\n- heightCm: unknown\n- weightKg: unknown\n- goal: unknown\n- activityLevel: unknown\n- gender: unknown";
        }

        String goal = Validators.nvl(p.goal());
        String lvl = Validators.nvl(p.activityLevel());
        String gender = Validators.nvl(p.gender());

        return String.join("\n",
                "- userId: " + p.userId(),
                "- age: " + (p.age() == null ? "unknown" : p.age()),
                "- heightCm: " + (p.heightCm() == null ? "unknown" : p.heightCm()),
                "- weightKg: " + (p.weightKg() == null ? "unknown" : p.weightKg()),
                "- goal: " + (goal.isBlank() ? "unknown" : goal),
                "- activityLevel: " + (lvl.isBlank() ? "unknown" : lvl),
                "- gender: " + (gender.isBlank() ? "unknown" : gender)
        );
    }
}
