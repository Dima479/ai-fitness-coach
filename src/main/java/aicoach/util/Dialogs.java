package aicoach.util;

import javax.swing.*;
import java.awt.*;

/**
 * @file Dialogs.java
 * @brief Utilitar pentru afisarea rapida a dialogurilor Swing (error/info/confirm).
 *
 * Centralizeaza mesajele in JOptionPane ca sa se repete cod in toate ecranele.
 */
public final class Dialogs {

    /** Constructor privat: clasa utilitara, nu se instantiaza. */
    private Dialogs() {}

    /**
     * Afiseaza un dialog de eroare (icon rosu) cu un mesaj.
     *
     * @param parent Componenta parinte (pentru centrare si modalitate).
     * @param msg Mesajul afisat in dialog.
     */
    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Afiseaza un dialog informativ (icon albastru) cu un mesaj.
     *
     * @param parent Componenta parinte (pentru centrare si modalitate).
     * @param msg Mesajul afisat in dialog.
     */
    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Afiseaza un dialog de confirmare (Yes/No) si intoarce alegerea utilizatorului.
     *
     * @param parent Componenta parinte (pentru centrare si modalitate).
     * @param msg Mesajul afisat in dialog.
     * @return true daca utilizatorul apasa Yes, altfel false.
     */
    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
