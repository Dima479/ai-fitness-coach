package aicoach.dao;

import aicoach.db.Db;
import aicoach.model.UserProfile;

import java.sql.*;

/**
 * @file ProfileDao.java
 * @brief Acces la tabela user_profiles (citire si upsert profil).
 *
 * Permite obtinerea profilului unui utilizator si salvarea lui (insert sau update) folosind UPSERT.
 */
public final class ProfileDao {

    /**
     * Returneaza profilul unui utilizator dupa userId.
     *
     * @param userId ID-ul utilizatorului.
     * @return UserProfile daca exista, altfel null.
     * @throws RuntimeException Daca apare o eroare SQL la interogare.
     */
    public UserProfile get(long userId) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "select user_id, age, height_cm, weight_kg, goal, activity_level, gender, updated_at from user_profiles where user_id = ?"
             )) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("get profile failed: " + e.getMessage(), e);
        }
    }

    /**
     * Insereaza sau actualizeaza profilul (UPSERT) pentru user_id:
     * daca nu exista rand, face INSERT; daca exista deja, face UPDATE.
     *
     * @param p Profilul de salvat.
     * @throws RuntimeException Daca apare o eroare SQL la inserare/actualizare.
     */
    public void upsert(UserProfile p) {
        String sql =
                "insert into user_profiles(user_id, age, height_cm, weight_kg, goal, activity_level, gender, updated_at) " +
                "values(?, ?, ?, ?, ?, ?, ?, datetime('now')) " +
                "on conflict(user_id) do update set " +
                "age=excluded.age, " +
                "height_cm=excluded.height_cm, " +
                "weight_kg=excluded.weight_kg, " +
                "goal=excluded.goal, " +
                "activity_level=excluded.activity_level, " +
                "gender=excluded.gender, " +
                "updated_at=datetime('now')";

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, p.userId());
            if (p.age() == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, p.age());
            if (p.heightCm() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, p.heightCm());
            if (p.weightKg() == null) ps.setNull(4, Types.REAL); else ps.setDouble(4, p.weightKg());
            ps.setString(5, p.goal());
            ps.setString(6, p.activityLevel());
            ps.setString(7, p.gender());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("upsert profile failed: " + e.getMessage(), e);
        }
    }

    /**
     * Mapeaza randul curent din ResultSet intr-un obiect UserProfile, pastrand valorile NULL din DB ca null in Java.
     *
     * @param rs ResultSet pozitionat pe un rand valid.
     * @return Obiect UserProfile construit din coloanele randului curent.
     * @throws SQLException Daca citirea coloanelor esueaza.
     */
    private UserProfile map(ResultSet rs) throws SQLException {
        Integer age = rs.getObject("age") == null ? null : rs.getInt("age");
        Integer height = rs.getObject("height_cm") == null ? null : rs.getInt("height_cm");
        Double weight = rs.getObject("weight_kg") == null ? null : rs.getDouble("weight_kg");
        return new UserProfile(
                rs.getLong("user_id"),
                age,
                height,
                weight,
                rs.getString("goal"),
                rs.getString("activity_level"),
                rs.getString("gender"),
                rs.getString("updated_at")
        );
    }
}
