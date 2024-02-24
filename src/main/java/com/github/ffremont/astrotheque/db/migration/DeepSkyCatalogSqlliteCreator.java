package com.github.ffremont.astrotheque.db.migration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ffremont.astrotheque.service.model.ConstellationData;
import com.github.ffremont.astrotheque.service.model.DsoEntry;
import lombok.extern.slf4j.Slf4j;
import org.sqlite.SQLiteDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
public class DeepSkyCatalogSqlliteCreator {

    final static String DSO_FILENAME = "deep-sky-objects.json";
    final static String CONSTS_FILENAME = "constellations.json";

    private final static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static void main(String[] args) {
        // Lecture du fichier JSON depuis le classpath
        InputStream inputStreamDso = DeepSkyCatalogSqlliteCreator.class.getResourceAsStream("/deep-sky-objects.json");
        InputStream inputStreamConsts = DeepSkyCatalogSqlliteCreator.class.getResourceAsStream("/constellations.json");

        // Connexion à la base de données SQLite
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:dbs/astrotheque.db");

        try (Connection connection = dataSource.getConnection()) {
            // Création de la table DsoEntries si elle n'existe pas
            connection.setAutoCommit(false);
            String createDsoTableSQL = "CREATE TABLE IF NOT EXISTS DsoEntries (type TEXT, constellation TEXT, magnitude REAL, id1, cat1 TEXT, id2 INTEGER, cat2 TEXT)";
            connection.createStatement().executeUpdate(createDsoTableSQL);

            String dsoIndex = "CREATE INDEX IF NOT EXISTS id1_index ON DsoEntries (id1);";
            connection.createStatement().executeUpdate(dsoIndex);

            String dsoIndex2 = "CREATE INDEX IF NOT EXISTS id2_index ON DsoEntries (id2);";
            connection.createStatement().executeUpdate(dsoIndex2);

            // Création de la table Constellations si elle n'existe pas
            String createConstellationTableSQL = "CREATE TABLE IF NOT EXISTS Constellations (label TEXT , name TEXT, abbreviation TEXT PRIMARY KEY)";
            connection.createStatement().executeUpdate(createConstellationTableSQL);

            // Insertion des données de constellations dans la table Constellations
            String insertConstellationsSQL = "INSERT INTO Constellations (label, name, abbreviation) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertConstellationsSQL)) {
                ConstellationData[] constellationData = mapper.readValue(inputStreamConsts, ConstellationData[].class);
                for (ConstellationData data : constellationData) {
                    preparedStatement.setString(1, data.label());
                    preparedStatement.setString(2, data.name());
                    preparedStatement.setString(3, data.abr());
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }

            // Insertion des données de DsoEntries dans la table DsoEntries
            String insertDsoEntriesSQL = "INSERT INTO DsoEntries (type, constellation, magnitude, id1, cat1, id2, cat2) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertDsoEntriesSQL)) {
                DsoEntry[] entries = mapper.readValue(inputStreamDso, DsoEntry[].class);
                for (DsoEntry entry : entries) {
                    if (Objects.isNull(entry.type())) {
                        continue;
                    }
                    preparedStatement.setString(1, entry.type().toString());
                    preparedStatement.setString(2, entry.constellation());
                    preparedStatement.setFloat(3, entry.magnitude() != null ? entry.magnitude() : 0);
                    preparedStatement.setInt(4, entry.id1() != null ? entry.id1() : 0);
                    preparedStatement.setString(5, entry.cat1());
                    preparedStatement.setInt(6, entry.id2() != null ? entry.id2() : 0);
                    preparedStatement.setString(7, entry.cat2());
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }

            connection.commit(); // Fin de la transaction
            System.out.println("Données insérées avec succès !");
        } catch (SQLException | IOException e) {
            log.error("Une erreur s'est produite lors de la migration.", e);
        }
    }
}
