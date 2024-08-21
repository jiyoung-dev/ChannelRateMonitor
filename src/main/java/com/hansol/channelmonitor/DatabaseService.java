package com.hansol.channelmonitor;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatabaseService {
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public DatabaseService() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            dbUrl = prop.getProperty("db.url");
            dbUser = prop.getProperty("db.username");
            dbPassword = prop.getProperty("db.password");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getBusyRate() {
        int busyRate = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String currentDate = LocalDate.now().format(formatter);

        String query = "SELECT TOP 1 busyrate FROM BusyRate WHERE RecordDate = ? ORDER BY RecordTime DESC";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, currentDate);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    busyRate = rs.getInt("busyrate");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return busyRate;
    }

    public String getCodeID(String codeName) {
        String codeID = "0";
        String query = "SELECT CodeID FROM CMCodeInfo WHERE CodeName = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codeName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    codeID = rs.getString("CodeID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codeID;
    }

}
