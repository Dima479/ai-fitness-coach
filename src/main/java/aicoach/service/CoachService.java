package aicoach.service;

import aicoach.ai.OpenRouterClient;
import aicoach.model.UserProfile;
import aicoach.util.Validators;

/**
 * @file coachservice.java
 * @brief serviciu care genereaza planuri de workout si nutritie folosind ai (openrouter).
 *
 * construieste prompt-uri (system + user) pe baza profilului si apeleaza openrouterclient.
 */
public final class CoachService {

    /** model implicit: ia din openrouter_model iar daca lipseste foloseste fallback. */
    private static final String DEFAULT_MODEL =
            System.getenv().getOrDefault("OPENROUTER_MODEL", "openai/gpt-4o-mini");

    /** clientul care trimite request-urile http catre openrouter. */
    private final OpenRouterClient ai = new OpenRouterClient();

    /** modelul folosit de acest serviciu (mereu default_model in aceasta varianta). */
    private final String model = DEFAULT_MODEL;

    /**
     * genereaza un plan de antrenament (3 zile) folosind ai pe baza profilului.
     *
     * @param p profilul utilizatorului (poate fi null; atunci se trimit valori "unknown").
     * @return textul planului de antrenament generat de ai.
     * @throws runtimeexception daca apelul catre openrouter esueaza.
     */
    public String generateWorkoutPlan(UserProfile p) {
        String system = baseSystemPrompt();

        String user = String.join("\n",
                "Sarcina: creeaza un plan de antrenament.",
                "Constrangeri:",
                "- Ofera un plan de antrenament pe 3 zile",
                "- Fa-l cat mai scurt posibil, folosind putin text dar precis",
                "- Include incalzire, exercitii principale, regula de progresie si cardio/pasi.",
                "- Pastreaza-l realist si sigur pentru populatia generala. Daca exista probleme medicale/durere: recomanda specialist.",
                "",
                "Profil utilizator:",
                profileBlock(p)
        );

        return ai.chat(model, system, user, 0.1, 900);
    }

    /**
     * genereaza un plan de nutritie folosind ai pe baza profilului.
     *
     * @param p profilul utilizatorului (poate fi null; atunci se trimit valori "unknown").
     * @return textul planului de nutritie generat de ai.
     * @throws runtimeexception daca apelul catre openrouter esueaza.
     */
    public String generateNutritionPlan(UserProfile p) {
        String system = baseSystemPrompt();

        String user = String.join("\n",
                "Sarcina: creeaza un plan de nutritie.",
                "Constrangeri:",
                "- Ofera tinte zilnice (calorii, proteine, grasimi, carbohidrati) si un sablon simplu de mese.",
                "- Daca greutatea lipseste, presupune 75kg dar mentioneaza ca este o presupunere.",
                "- Pastreaza-l practic (mancaruri in stil Romania ok).",
                "- Adauga o regula simpla de ajustare bazata pe trendul saptamanal al greutatii.",
                "",
                "Profil utilizator:",
                profileBlock(p)
        );

        return ai.chat(model, system, user, 0.35, 900);
    }

    /**
     * construieste mesajul de sistem (reguli generale) pentru model.
     *
     * @return textul system prompt folosit la toate cererile.
     */
    private static String baseSystemPrompt() {
        return String.join("\n",
                "Esti un antrenor de fitness si nutritie bazat pe dovezi.",
                "Oferi sfaturi sigure si realiste si eviti diagnosticarea medicala.",
                "Daca utilizatorul mentioneaza durere acuta, boala, tulburari de alimentatie sau risc medical: recomanda consult de specialitate.",
                "Preferi planuri aplicabile: puncte, numere, pasi clari.",
                "Nu inventa date personale; daca lipsesc, mentioneaza presupunerile.",
                "Raspunde mereu in romana fara diacritice."
        );
    }

    /**
     * construieste un bloc text cu campurile profilului pentru a fi inclus in prompt.
     * normalizeaza valorile lipsa ca "unknown".
     *
     * @param p profilul utilizatorului.
     * @return bloc text cu campurile profilului pe linii separate.
     */
    private static String profileBlock(UserProfile p) {
        if (p == null) {
            return "- idUtilizator: necunoscut\n- varsta: necunoscut\n- inaltimeCm: necunoscut\n- greutateKg: necunoscut\n- obiectiv: necunoscut\n- nivelActivitate: necunoscut\n- gen: necunoscut";
        }

        String goal = toRoValue(Validators.nvl(p.goal()));
        String lvl = toRoValue(Validators.nvl(p.activityLevel()));
        String gender = toRoValue(Validators.nvl(p.gender()));

        return String.join("\n",
                "- idUtilizator: " + p.userId(),
                "- varsta: " + (p.age() == null ? "necunoscut" : p.age()),
                "- inaltimeCm: " + (p.heightCm() == null ? "necunoscut" : p.heightCm()),
                "- greutateKg: " + (p.weightKg() == null ? "necunoscut" : p.weightKg()),
                "- obiectiv: " + (goal.isBlank() ? "necunoscut" : goal),
                "- nivelActivitate: " + (lvl.isBlank() ? "necunoscut" : lvl),
                "- gen: " + (gender.isBlank() ? "necunoscut" : gender)
        );
    }

    private static String toRoValue(String value) {
        if (value == null) return "";
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
