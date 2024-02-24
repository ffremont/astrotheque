package com.github.ffremont.astrotheque.dao;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.db.ConnectionPoolManager;
import com.github.ffremont.astrotheque.service.model.ConstellationData;
import com.github.ffremont.astrotheque.service.model.DsoEntry;
import com.github.ffremont.astrotheque.service.model.Type;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import static java.util.function.Predicate.not;

@Slf4j
public class DeepSkyCatalogDAO {
    
    private final ConnectionPoolManager connectionPoolManager;

    public DeepSkyCatalogDAO(IoC ioC) {
        this.connectionPoolManager = ioC.get(ConnectionPoolManager.class);
    }

    public Optional<ConstellationData> getConstellationByAbr(String abr) {
        String query = "SELECT * FROM Constellations WHERE LOWER(abbreviation) = LOWER(?)";
        Connection connection = null;
        try {
            connection = connectionPoolManager.getConnection();

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, abr);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(new ConstellationData(
                                resultSet.getString("label"),
                                resultSet.getString("name"),
                                resultSet.getString("abbreviation")
                        ));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(STR."Recherche Constellations impossible \{abr}", e);
            }
        } finally {
            if (Objects.nonNull(connection)) {
                connectionPoolManager.releaseConnection(connection);
            }
        }

        return Optional.empty();
    }

    public Optional<DsoEntry> getDsoByName(String name) {
        var id = Integer.parseInt(Optional.of(name.replaceAll("[\\D.]", "")).filter(not(String::isBlank)).orElse("0").trim());
        var category = Optional.of(name.replaceAll("[\\d.]", "")).filter(not(String::isBlank)).orElse("").trim();

        String query = "SELECT * FROM DsoEntries WHERE (id1 = ? AND LOWER(cat1) = LOWER(?)) OR (id2 = ? AND LOWER(cat2) = LOWER(?))";

        Connection connection = null;
        try {
            connection = connectionPoolManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                statement.setString(2, category);
                statement.setInt(3, id);
                statement.setString(4, category);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(new DsoEntry(
                                Type.valueOf(resultSet.getString("type")),
                                resultSet.getString("constellation"),
                                resultSet.getFloat("magnitude"),
                                resultSet.getInt("id1"),
                                resultSet.getString("cat1"),
                                resultSet.getInt("id2"),
                                resultSet.getString("cat2")
                        ));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(STR."Recherche DsoEntries impossible \{name}", e);
            }
        } finally {
            if (Objects.nonNull(connection)) {
                connectionPoolManager.releaseConnection(connection);
            }
        }
        return Optional.empty();
    }
}
