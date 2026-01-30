package aicoach.dao;

import aicoach.db.Db;
import aicoach.model.ProgressEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @file progressdao.java
 * @brief acces la tabela progress (listare inserare stergere).
 *
 * permite citirea istoricului de progres al unui utilizator adaugarea unei inregistrari noi
 * si stergerea unei inregistrari existente.
 */
public final class ProgressDao {

    /**
     * returneaza lista de inregistrari de progres pentru un utilizator ordonate descrescator dupa data
     * (cele mai noi primele).
     *
     * @param userid id-ul utilizatorului.
     * @return lista de inregistrari de progres (poate fi goala).
     * @throws runtimeexception daca apare o eroare sql la interogare.
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
            throw new RuntimeException("Listare progres esuata: " + e.getMessage(), e);
        }
    }

    /**
     * insereaza o inregistrare noua de progres si returneaza id-ul generat.
     *
     * @param e inregistrarea de progres care va fi salvata.
     * @return id-ul generat pentru inregistrarea inserata.
     * @throws runtimeexception daca apare o eroare sql la inserare.
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
            throw new RuntimeException("Inserare progres esuata: " + ex.getMessage(), ex);
        }
    }

    /**
     * sterge o inregistrare de progres dupa id dar doar daca apartine utilizatorului dat.
     *
     * @param id id-ul inregistrarii de sters.
     * @param userid id-ul utilizatorului (folosit ca protectie ca sa nu stergi inregistrarea altcuiva).
     * @throws runtimeexception daca apare o eroare sql la stergere.
     */
    public void delete(long id, long userId) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement("delete from progress where id = ? and user_id = ?")) {
            ps.setLong(1, id);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Stergere progres esuata: " + e.getMessage(), e);
        }
    }

    /**
     * map-eaza randul curent din resultset intr-un obiect progressentry pastrand valorile null din db ca null in java.
     *
     * @param rs resultset pozitionat pe un rand valid.
     * @return obiect progressentry construit din coloanele randului curent.
     * @throws sqlexception daca citirea coloanelor esueaza.
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
