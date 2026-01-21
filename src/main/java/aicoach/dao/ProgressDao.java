package aicoach.dao;

import aicoach.db.Db;
import aicoach.model.ProgressEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @file ProgressDao.java
 * @brief Acces la tabela progress (listare, inserare, stergere).
 *
 * Permite citirea istoricului de progres al unui utilizator, adaugarea unei inregistrari noi
 * si stergerea unei inregistrari existente.
 */
public final class ProgressDao {

    /**
     * Returneaza lista de inregistrari de progres pentru un utilizator, ordonate descrescator dupa data
     * (cele mai noi primele).
     *
     * @param userId ID-ul utilizatorului.
     * @return Lista de inregistrari de progres (poate fi goala).
     * @throws RuntimeException Daca apare o eroare SQL la interogare.
     */
    public List<ProgressEntry> list(long userId) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "select id, user_id, entry_date, weight_kg, calories_consumed, workout_min, notes from progress where user_id = ? order by entry_date desc, id desc"
             )) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<ProgressEntry> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("list progress failed: " + e.getMessage(), e);
        }
    }

    /**
     * Insereaza o inregistrare noua de progres si returneaza ID-ul generat.
     *
     * @param e Inregistrarea de progres care va fi salvata.
     * @return ID-ul generat pentru inregistrarea inserata.
     * @throws RuntimeException Daca apare o eroare SQL la inserare.
     */
    public long insert(ProgressEntry e) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "insert into progress(user_id, entry_date, weight_kg, calories_consumed, workout_min, notes) values(?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS
             )) {
            ps.setLong(1, e.userId());
            ps.setString(2, e.entryDate());
            if (e.weightKg() == null) ps.setNull(3, Types.REAL); else ps.setDouble(3, e.weightKg());
            if (e.caloriesConsumed() == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, e.caloriesConsumed());
            if (e.workoutMin() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, e.workoutMin());
            ps.setString(6, e.notes());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("insert progress failed: " + ex.getMessage(), ex);
        }
    }

    /**
     * Sterge o inregistrare de progres dupa id, dar doar daca apartine utilizatorului dat.
     *
     * @param id ID-ul inregistrarii de sters.
     * @param userId ID-ul utilizatorului (folosit ca protectie ca sa nu stergi inregistrarea altcuiva).
     * @throws RuntimeException Daca apare o eroare SQL la stergere.
     */
    public void delete(long id, long userId) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement("delete from progress where id = ? and user_id = ?")) {
            ps.setLong(1, id);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete progress failed: " + e.getMessage(), e);
        }
    }

    /**
     * Map-eaza randul curent din ResultSet intr-un obiect ProgressEntry, pastrand valorile NULL din DB ca null in Java.
     *
     * @param rs ResultSet pozitionat pe un rand valid.
     * @return Obiect ProgressEntry construit din coloanele randului curent.
     * @throws SQLException Daca citirea coloanelor esueaza.
     */
    private ProgressEntry map(ResultSet rs) throws SQLException {
        Double w = rs.getObject("weight_kg") == null ? null : rs.getDouble("weight_kg");
        Integer cal = rs.getObject("calories_consumed") == null ? null : rs.getInt("calories_consumed");
        Integer min = rs.getObject("workout_min") == null ? null : rs.getInt("workout_min");
        return new ProgressEntry(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("entry_date"),
                w,
                cal,
                min,
                rs.getString("notes")
        );
    }
}
