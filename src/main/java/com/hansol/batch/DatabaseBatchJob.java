package com.hansol.batch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DatabaseBatchJob {

    private static final Logger logger = LogManager.getLogger(DatabaseBatchJob.class);
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    public static void main(String[] args) {
        loadProperties();

        Timer timer = new Timer();
        timer.schedule(new InsertTask(), 0, 15000); // 15초마다 실행
    }

    private static void loadProperties() {
        try (InputStream input = DatabaseBatchJob.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                logger.error("Error: unable to find application.properties");
                return;
            }

            prop.load(input);

            dbUrl = prop.getProperty("db.url");
            dbUser = prop.getProperty("db.username");
            dbPassword = prop.getProperty("db.password");
        } catch (Exception ex) {
            logger.error("Error loading properties file", ex);
        }
    }

    static class InsertTask extends TimerTask {

        @Override
        public void run() {
            insertData();
        }

        private void insertData() {
            String uniqueSystemID = "00000010";
            String recordDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String recordTime = new SimpleDateFormat("HHmmss").format(new Date());
            int realTotalPorts = 8;
            int occupiedCount = new Random().nextInt(realTotalPorts) + 1; // 1부터 8 사이 랜덤값
            int savingBorder = 10;
            int busyRate = (occupiedCount * 100) / realTotalPorts;
            String channelState = generateChannelState(realTotalPorts, occupiedCount);

            String insertSQL = "INSERT INTO BusyRate " +
                    "(UniqueSystemID, RecordDate, RecordTime, RealTotalPorts, OccupiedCount, SavingBorder, BusyRate, ChannelState) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

                preparedStatement.setString(1, uniqueSystemID);
                preparedStatement.setString(2, recordDate);
                preparedStatement.setString(3, recordTime);
                preparedStatement.setInt(4, realTotalPorts);
                preparedStatement.setInt(5, occupiedCount);
                preparedStatement.setInt(6, savingBorder);
                preparedStatement.setInt(7, busyRate);
                preparedStatement.setString(8, channelState);

                preparedStatement.executeUpdate();
            } catch (Exception e) {
                logger.error("Failed to insert data into BusyRate table", e);
            }
        }

        private String generateChannelState(int totalPorts, int occupiedCount) {
            StringBuilder state = new StringBuilder();
            for (int i = 0; i < totalPorts; i++) {
                if (i < occupiedCount) {
                    state.append("2");
                } else {
                    state.append("0");
                }
                if (i < totalPorts - 1) {
                    state.append("|");
                }
            }
            return state.toString();
        }
    }
}
