package aicoach.util;

import javax.swing.*;
import java.awt.*;

/**
 * @file dialogs.java
 * @brief utilitar pentru afisarea rapida a dialogurilor swing (error/info/confirm).
 *
 * centralizeaza mesajele in joptionpane ca sa se repete cod in toate ecranele.
 */
public final class Dialogs {

    /** constructor privat: clasa utilitara nu se instantiaza. */
    private Dialogs() {}

    /**
     * afiseaza un dialog de eroare (icon rosu) cu un mesaj.
     *
     * @param parent componenta parinte (pentru centrare si modalitate).
     * @param msg mesajul afisat in dialog.
     */
    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Eroare", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * afiseaza un dialog informativ (icon albastru) cu un mesaj.
     *
     * @param parent componenta parinte (pentru centrare si modalitate).
     * @param msg mesajul afisat in dialog.
     */
    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Informare", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * afiseaza un dialog de confirmare (yes/no) si intoarce alegerea utilizatorului.
     *
     * @param parent componenta parinte (pentru centrare si modalitate).
     * @param msg mesajul afisat in dialog.
     * @return true daca utilizatorul apasa yes altfel false.
     */
    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirmare", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
