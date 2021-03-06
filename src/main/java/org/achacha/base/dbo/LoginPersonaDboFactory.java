package org.achacha.base.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.global.Global;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LoginPersonaDboFactory extends BaseDboFactory<LoginPersonaDbo> {
    public LoginPersonaDboFactory() {
        super(LoginPersonaDbo.class);
    }

    /**
     * Find login with persona by id
     * @param connection Connection
     * @param idToFind long
     * @return LoginPersonaDbo or null if not found
     */
    public LoginPersonaDbo findById(Connection connection, long idToFind) {
        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/Login/SelectPersonaById.sql",
                        p -> p.setLong(1, idToFind));
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                LoginPersonaDbo dbo = new LoginPersonaDbo();
                dbo.fromResultSet(connection, rs);
                return dbo;
            } else {
                LoginUserDbo.LOGGER.warn("Failed to find login id={}", idToFind);
            }
        } catch (Exception sqle) {
            LoginUserDbo.LOGGER.error("Failed to find login", sqle);
        }
        return null;
    }

    /**
     * Search for personas by search term and limit
     * @param connection Connection
     * @param searchTerm String
     * @param limit      int
     * @return List of LoginPersonaDbo
     */
    public Collection<LoginPersonaDbo> findSearch(Connection connection, String searchTerm, int limit) {
        List<LoginPersonaDbo> personas = new ArrayList<>();
        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/Login/SearchPersona.sql",
                        p -> {
                            String queryTerm = "%" + searchTerm + "%";
                            p.setString(1, queryTerm);
                            p.setString(2, queryTerm);
                            p.setString(3, queryTerm);
                            p.setString(4, queryTerm);
                            p.setInt(5, limit);
                        });
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                LoginPersonaDbo dbo = new LoginPersonaDbo();
                dbo.fromResultSet(connection, rs);
                personas.add(dbo);
            }
        } catch (Exception sqle) {
            LoginUserDbo.LOGGER.error("Failed to find personas", sqle);
        }
        return personas;
    }
}
