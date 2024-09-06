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

    public Map<String, Integer> getBusyRate() {
    	  Map<String, Integer> busyRates = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String currentDate = LocalDate.now().format(formatter);

        String query = "WITH LastedBusyRate AS ( " +
            "    SELECT " +
            "        UniqueSystemID, " +
            "        RecordDate, " +
            "        RecordTime, " +
            "        RealTotalPorts, " +
            "        OccupiedCount, " +
            "        SavingBorder, " +
            "        BusyRate, " +
            "        ChannelState, " +
            "        ROW_NUMBER() OVER (PARTITION BY UniqueSystemID ORDER BY RecordTime DESC) AS rn " +
            "    FROM BusyRate " +
            "    WHERE UniqueSystemID IN ('00101001', '00101002', '00201001', '00201002') " +
            "        AND RecordDate = ? " +
            ") " +
            "SELECT " +
            "    CASE " +
            "        WHEN UniqueSystemID IN ('00101001', '00101002') THEN '강남' " +
            "        WHEN UniqueSystemID IN ('00201001', '00201002') THEN '송도' " +
            "    END AS CenterName, " +
            "    AVG(BusyRate) AS BusyRateAvg, " +
            "    RecordDate, " +
            "    RecordTime " +
            "FROM LastedBusyRate " +
            "WHERE rn = 2 " +  
            "GROUP BY " +
            "    CASE " +
            "        WHEN UniqueSystemID IN ('00101001', '00101002') THEN '강남' " +
            "        WHEN UniqueSystemID IN ('00201001', '00201002') THEN '송도' " +
            "    END, " +
            "    RecordDate, " +
            "    RecordTime " +
            "ORDER BY CenterName";
        
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
