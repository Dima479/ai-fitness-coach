package aicoach.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @file Migrations.java
 * @brief Creeaza schema bazei de date (tabele + indexuri) daca nu exista deja.
 *
 * Foloseste comenzi CREATE TABLE IF NOT EXISTS si CREATE INDEX IF NOT EXISTS,
 * astfel incat metoda poate fi rulata de mai multe ori fara sa strice datele existente.
 */
public final class Migrations {

    /** Constructor privat: clasa utilitara, nu trebuie instantata. */
    private Migrations() {}

    /**
     * Aplica migrarile pentru schema bazei de date: creeaza tabelele si indexurile necesare.
     *
     * @param c Conexiunea activa catre baza de date SQLite.
     * @throws SQLException Daca o comanda SQL esueaza.
     */
    public static void apply(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {

            st.execute(
                    "create table if not exists users (" +
                            "  id integer primary key autoincrement," +
                            "  email text not null unique," +
                            "  password_hash text not null," +
                            "  created_at text default (datetime('now'))" +
                            ");"
            );
            st.execute(
                    "create table if not exists user_profiles (" +
                            "  user_id integer primary key," +
                            "  age integer," +
                            "  height_cm integer," +
                            "  weight_kg real," +
                            "  goal text," +
                            "  activity_level text," +
                            "  gender text," +
                            "  updated_at text default (datetime('now'))," +
                            "  foreign key (user_id) references users(id) on delete cascade" +
                            ");"
            );
            st.execute(
                    "create table if not exists plans (" +
                            "  id integer primary key autoincrement," +
                            "  user_id integer not null," +
                            "  plan_type text," +
                            "  content text," +
                            "  created_at text default (datetime('now'))," +
                            "  foreign key (user_id) references users(id) on delete cascade" +
                            ");"
            );
            st.execute("create index if not exists plans_user_ix on plans(user_id);");
            st.execute(
                    "create table if not exists progress (" +
                            "  id integer primary key autoincrement," +
                            "  user_id integer not null," +
                            "  entry_date text default (date('now'))," +
                            "  weight_kg real," +
                            "  calories_consumed integer," +
                            "  workout_min integer," +
                            "  notes text," +
                            "  foreign key (user_id) references users(id) on delete cascade" +
                            ");"
            );
            st.execute("create index if not exists progress_user_ix on progress(user_id);");
            st.execute(
                    "create table if not exists chat_history (" +
                            "  id integer primary key autoincrement," +
                            "  user_id integer not null," +
                            "  role text," +
                            "  message text," +
                            "  timestamp text default (datetime('now'))," +
                            "  foreign key (user_id) references users(id) on delete cascade" +
                            ");"
            );
            st.execute("create index if not exists chat_user_ix on chat_history(user_id);");
        }
    }
}
