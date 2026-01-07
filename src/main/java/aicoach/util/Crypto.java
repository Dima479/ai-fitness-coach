package aicoach.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @file Crypto.java
 * @brief Functii utilitare pentru hash-uirea textului (SHA-256).
 *
 * Ofera o metoda simpla care transforma un string intr-un hash SHA-256 in format hex
 */
public final class Crypto {

    /** Constructor privat: clasa utilitara, nu se instantiaza. */
    private Crypto() {}

    /**
     * Calculeaza hash-ul SHA-256 pentru un text si il returneaza ca string hex (lowercase).
     *
     * @param s Textul de intrare (ex. parola) care va fi hash-uit
     * @return Hash-ul SHA-256 in format hex (64 caractere).
     * @throws RuntimeException Daca algoritmul SHA-256 nu este disponibil sau apare o eroare la calcul.
     */
    public static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("sha256 failed: " + e.getMessage(), e);
        }
    }
}
