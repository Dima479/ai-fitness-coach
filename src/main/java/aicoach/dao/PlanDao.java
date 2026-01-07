package aicoach.dao;

import aicoach.db.Db;
import aicoach.model.Plan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @file PlanDao.java
 * @brief Acces la tabela plans (listare, inserare, stergere).
 *
 * Ofera operatii pentru planurile unui utilizator: citire lista, adaugare plan nou si stergere plan.
 */
public final class PlanDao {

    /**
     * Returneaza lista de planuri pentru un utilizator, ordonate descrescator dupa id
     *
     * @param userId ID-ul utilizatorului.
     * @return Lista de planuri ale utilizatorului
     * @throws RuntimeException Daca apare o eroare SQL la interogare.
     */
    public List<Plan> list(long userId) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "select id, user_id, plan_type, content, created_at from plans where user_id = ? order by id desc")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Plan> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("list plans failed: " + e.getMessage(), e);
        }
    }

    /**
     * Insereaza un plan nou pentru un utilizator si returneaza ID-ul generat.
     *
     * @param userId ID-ul utilizatorului caruia ii apartine planul.
     * @param planType Tipul planului (ex. WORKOUT, NUTRITION).
     * @param content Continutul planului (text/descriere).
     * @return ID-ul generat pentru planul inserat.
     * @throws RuntimeException Daca apare o eroare SQL la inserare.
     */
    public long insert(long userId, String planType, String content) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "insert into plans(user_id, plan_type, content) values(?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS
             )) {
            ps.setLong(1, userId);
            ps.setString(2, planType);
            ps.setString(3, content);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("insert plan failed: " + e.getMessage(), e);
        }
    }

    /**
     * Sterge un plan dupa id, dar doar daca apartine utilizatorului dat.
     *
     * @param planId ID-ul planului de sters.
     * @param userId ID-ul utilizatorului (folosit ca protectie ca sa nu stergi planul altcuiva).
     * @return Nu returneaza nimic.
     * @throws RuntimeException Daca apare o eroare SQL la stergere.
     */
    public void delete(long planId, long userId) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement("delete from plans where id = ? and user_id = ?")) {
            ps.setLong(1, planId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete plan failed: " + e.getMessage(), e);
        }
    }

    /**
     * Map-eaza randul curent din ResultSet intr-un obiect Plan.
     *
     * @param rs ResultSet pozitionat pe un rand valid.
     * @return Obiect Plan construit din coloanele randului curent.
     * @throws SQLException Daca citirea coloanelor esueaza.
     */
    private Plan map(ResultSet rs) throws SQLException {
        return new Plan(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("plan_type"),
                rs.getString("content"),
                rs.getString("created_at")
        );
    }
}
