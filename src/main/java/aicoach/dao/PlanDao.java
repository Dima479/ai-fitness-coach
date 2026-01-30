package aicoach.dao;

import aicoach.db.Db;
import aicoach.model.Plan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @file plandao.java
 * @brief acces la tabela plans (listare inserare stergere).
 *
 * ofera operatii pentru planurile unui utilizator: citire lista adaugare plan nou si stergere plan.
 */
public final class PlanDao {

    /**
     * returneaza lista de planuri pentru un utilizator ordonate descrescator dupa data crearii.
     *
     * @param userid id-ul utilizatorului.
     * @return lista de planuri ale utilizatorului
     * @throws runtimeexception daca apare o eroare sql la interogare.
     */
    public List<Plan> list(long userId) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "select id, user_id, plan_type, content, created_at from plans where user_id = ? order by created_at desc, id desc")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Plan> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Listare planuri esuata: " + e.getMessage(), e);
        }
    }

    /**
     * insereaza un plan nou pentru un utilizator si returneaza id-ul generat.
     *
     * @param userid id-ul utilizatorului caruia ii apartine planul.
     * @param plantype tipul planului (ex. workout nutrition).
     * @param content continutul planului (text/descriere).
     * @return id-ul generat pentru planul inserat.
     * @throws runtimeexception daca apare o eroare sql la inserare.
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
            throw new RuntimeException("Inserare plan esuata: " + e.getMessage(), e);
        }
    }

    /**
     * sterge un plan dupa id dar doar daca apartine utilizatorului dat.
     *
     * @param planid id-ul planului de sters.
     * @param userid id-ul utilizatorului (folosit ca protectie ca sa nu stergi planul altcuiva).
     * @return nu returneaza nimic.
     * @throws runtimeexception daca apare o eroare sql la stergere.
     */
    public void delete(long planId, long userId) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement("delete from plans where id = ? and user_id = ?")) {
            ps.setLong(1, planId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Stergere plan esuata: " + e.getMessage(), e);
        }
    }

    /**
     * map-eaza randul curent din resultset intr-un obiect plan.
     *
     * @param rs resultset pozitionat pe un rand valid.
     * @return obiect plan construit din coloanele randului curent.
     * @throws sqlexception daca citirea coloanelor esueaza.
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
