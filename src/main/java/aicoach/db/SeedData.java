package aicoach.db;

import aicoach.util.Crypto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @file seeddata.java
 * @brief insereaza date initiale in baza de date (doar daca nu exista deja utilizatori).
 *
 * creeaza un utilizator de test si inregistrari asociate (profil progres plan mesaj in chat).
 * varianta foloseste statement (sql construit ca string) deoarece valorile sunt hardcodate/controlate.
 */
public final class SeedData {

    /** constructor privat: clasa utilitara nu se instantiaza. */
    private SeedData() {}

    /**
     * insereaza date initiale in db doar daca tabelul users este gol.
     *
     * @param c conexiunea activa catre baza de date sqlite.
     * @return nu returneaza nimic.
     * @throws sqlexception daca o comanda sql esueaza.
     */
    public static void apply(Connection c) throws SQLException {
        if (hasAnyUser(c)) return;

        String email = "test@example.com";
        String passHash = Crypto.sha256("test123");

        long userId;
        try (Statement st = c.createStatement()) {

            st.executeUpdate(
                    "insert into users(email, password_hash) values('" + email + "', '" + passHash + "')"
            );

            try (ResultSet rs = st.executeQuery("select last_insert_rowid()")) {
                rs.next();
                userId = rs.getLong(1);
            }

            st.executeUpdate(
                    "insert into user_profiles(user_id, age, height_cm, weight_kg, goal, activity_level, gender) " +
                            "values(" + userId + ", 25, 178, 75, 'weight_loss', 'moderate', 'male')"
            );

            st.executeUpdate(
                    "insert into progress(user_id, entry_date, weight_kg, calories_consumed, workout_min, notes) " +
                            "values(" + userId + ", date('now'), 75, 2200, 45, 'First workout logged')"
            );

            st.executeUpdate(
                    "insert into plans(user_id, plan_type, content) " +
                            "values(" + userId + ", 'WORKOUT', 'Push Day: Bench, OHP, Triceps dips, Incline Press')"
            );

            st.executeUpdate(
                    "insert into chat_history(user_id, role, message) " +
                            "values(" + userId + ", 'assistant', 'Hi! I am your ai coach')"
            );
        }
    }

    /**
     * verifica daca exista cel putin un utilizator in tabelul users.
     *
     * @param c conexiunea activa catre baza de date sqlite.
     * @return true daca exista cel putin un utilizator altfel false.
     * @throws sqlexception daca interogarea sql esueaza.
     */
    private static boolean hasAnyUser(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("select count(*) from users")) {
            rs.next();
            return rs.getInt(1) > 0;
        }
    }
}
