package aicoach.util;

/**
 * @file Validators.java
 * @brief Utilitar pentru validari simple si normalizarea valorilor null.
 *
 * Include o metoda de verificare (require), o validare simpla de email si un helper nvl pentru stringuri.
 */
public final class Validators {

    /** Constructor privat: clasa utilitara, nu se instantiaza. */
    private Validators() {}

    /**
     * Arunca exceptie daca o conditie nu este indeplinita.
     *
     * @param cond Conditia care trebuie sa fie true.
     * @param message Mesajul exceptiei daca conditia este false.
     * @return Nu returneaza nimic.
     * @throws IllegalArgumentException Daca cond este false.
     */
    public static void require(boolean cond, String message) {
        if (!cond) throw new IllegalArgumentException(message);
    }

    /**
     * Verifica daca un string seamana cu un email (validare simpla, nu completa).
     *
     * @param email Textul de verificat.
     * @return true daca pare email, altfel false.
     */
    public static boolean isEmail(String email) {
        if (email == null) return false;
        String e = email.trim();
        return e.contains("@") && e.contains(".") && e.length() >= 6;
    }

    /**
     * Intoarce string gol daca valoarea este null, altfel intoarce stringul original.
     *
     * @param s String posibil null.
     * @return string non null ("" daca s este null).
     */
    public static String nvl(String s) {
        return s == null ? "" : s;
    }
}
