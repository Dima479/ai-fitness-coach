package aicoach.dao;

import aicoach.db.Db;
import aicoach.model.User;

import java.sql.*;

/**
 * @file UserDao.java
 * @brief Acces la tabela users (cautare si inserare)
 *
 * Contine operatii de baza pentru utilizatori: cautare dupa email, cautare dupa id si inserare user nou.
 * Foloseste JDBC cu PreparedStatement pentru parametri
 */
public final class UserDao {

    /**
     * Cauta un utilizator dupa email.
     *
     * @param email Emailul cautat.
     * @return User daca exista, altfel null.
     * @throws RuntimeException Daca apare o eroare SQL la interogare.
     */
    public User findByEmail(String email) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "select id, email, password_hash, created_at from users where email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new User(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("created_at")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByEmail failed: " + e.getMessage(), e);
        }
    }

    /**
     * Cauta un utilizator dupa id.
     *
     * @param id ID-ul utilizatorului.
     * @return User daca exista, altfel null.
     * @throws RuntimeException Daca apare o eroare SQL la interogare.
     */
    public User findById(long id) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "select id, email, password_hash, created_at from users where id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new User(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("created_at")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById failed: " + e.getMessage(), e);
        }
    }

    /**
     * Insereaza un utilizator nou in tabela users si returneaza ID-ul generat.
     *
     * @param email Emailul utilizatorului.
     * @param passwordHash Hash-ul parolei (nu parola in clar).
     * @return ID-ul generat pentru utilizatorul inserat.
     * @throws RuntimeException Daca apare o eroare SQL la inserare.
     */
    public long insert(String email, String passwordHash) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "insert into users(email, password_hash) values(?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("insert user failed: " + e.getMessage(), e);
        }
    }
}
