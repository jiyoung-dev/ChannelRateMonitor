package com.hansol.channelmonitor;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
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

    public Map<String, Integer> getBusyRates() {
    	  Map<String, Integer> busyRates = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String currentDate = LocalDate.now().format(formatter);

        String query = "WITH CurrentBusyRate AS ( " +
            "    SELECT " +
            "        br.UniqueSystemID, " +
            "        br.RecordDate, " +
            "        br.RecordTime, " +
            "        br.RealTotalPorts, " +
            "        br.OccupiedCount, " +
            "        br.SavingBorder, " +
            "        br.BusyRate, " +
            "        br.ChannelState, " +
            "        ROW_NUMBER() OVER (PARTITION BY br.UniqueSystemID ORDER BY br.RecordTime DESC) AS rn, " +
            "        acd.Title AS CenterName " + 
            "    FROM BusyRate br " +
            "    JOIN AreaCodeDirectory acd " + 
            "    	ON br.UniqueSystemID LIKE CONCAT(acd.AreaCode, '%') " +
            "    WHERE br.RecordDate = ? " +
            ") " +
            "SELECT " + 
            "	CenterName, " + 
            "	AVG(BusyRate) AS BusyRateAvg, " + 
            "	RecordDate, " + 
            "	RecordTime " + 
            "FROM CurrentBusyRate " + 
            "WHERE rn = 2 " + 
            "GROUP BY CenterName, RecordDate, RecordTime " + 
            "ORDER BY CenterName "; 
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, currentDate);

            try (ResultSet rs = stmt.executeQuery()) {
              while (rs.next()) {
                  String centerName = rs.getString("CenterName");
                  int busyRateAvg = rs.getInt("BusyRateAvg");
                  busyRates.put(centerName, busyRateAvg);
              }
          }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return busyRates;
    }

    public String getCodeID(String codeName) {
        String codeID = "0";
        String query = "SELECT CodeName FROM CMCodeInfo WHERE CodeID = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codeName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    codeID = rs.getString("CodeName");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codeID;
    }

}
