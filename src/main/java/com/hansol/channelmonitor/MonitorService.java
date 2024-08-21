package com.hansol.channelmonitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonitorService {
    private static final Logger logger = LogManager.getLogger(MonitorService.class);
    private DatabaseService dbService = new DatabaseService();
    private int warningThreshold = 70;
    private int dangerThreshold = 80;
    private boolean warningLogged = false;
    private boolean dangerLogged = false;

    public void monitorBusyRate() {
        try {
            // 임계치 조회
            getThresholds();

            // BusyRate 조회
            int currentBusyRate = dbService.getBusyRate();

            if (currentBusyRate >= dangerThreshold) {
                if (!dangerLogged) {
                    logger.info("(위험) [THRESHOLD_EXCEEDED] Call Occupancy Rate: {}%, Threshold: {}%", currentBusyRate, dangerThreshold);
                    dangerLogged = true;

                    if (!warningLogged) {
                        logger.info("(경고) [THRESHOLD_EXCEEDED] Call Occupancy Rate: {}%, Threshold: {}%", currentBusyRate, warningThreshold);
                        warningLogged = true;
                    }
                }
            } else if (currentBusyRate >= warningThreshold) {
                if (!warningLogged) {
                	logger.info("(경고) [THRESHOLD_EXCEEDED] Call Occupancy Rate: {}%, Threshold: {}%", currentBusyRate, warningThreshold);
                    warningLogged = true;
                }
            } else {
                // 현재 상태가 dangerThreshold 이하인 경우
                if (dangerLogged && currentBusyRate < dangerThreshold) {
                    logger.info("[CLEARED] 위험 알림이 해지되었습니다. Call Occupancy Rate: {}%, Threshold: {}%", currentBusyRate, dangerThreshold);
                    dangerLogged = false;
                }
                if (warningLogged && currentBusyRate < warningThreshold) {
                    logger.info("[CLEARED] 경고 알림이 해지되었습니다. Call Occupancy Rate: {}%, Threshold: {}%", currentBusyRate, warningThreshold);
                    warningLogged = false;
                }
            }
        } catch (Exception e) {
            logger.error("모니터링 중 에러 발생: ", e);
        }
    }


    private void getThresholds() {
        String warningThresholdStr = dbService.getCodeID("위험");
        String dangerThresholdStr = dbService.getCodeID("심각");

        warningThreshold = Integer.parseInt(warningThresholdStr); // 70
        dangerThreshold = Integer.parseInt(dangerThresholdStr);  // 80 
    }
}
