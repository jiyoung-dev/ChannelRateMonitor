package com.hansol.busyalert;

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
            // 임계치 DB 조회
            updateThresholds();

            // BusyRate 값 조회
            int currentBusyRate = dbService.getBusyRate();

            if (currentBusyRate >= dangerThreshold) {
                if (!dangerLogged) {
                    logger.error("(위험) 콜 점유율: {}% ,임계치({}%) 초과", currentBusyRate, dangerThreshold);
                    dangerLogged = true;

                    if (!warningLogged) {
                        logger.warn("(경고) 콜 점유율: {}% ,임계치({}%) 초과", currentBusyRate, warningThreshold);
                        warningLogged = true;
                    }
                }
            } else if (currentBusyRate >= warningThreshold) {
                if (!warningLogged) {
                    logger.warn("(경고) 콜 점유율: {}% ,임계치({}%) 초과", currentBusyRate, warningThreshold);
                    warningLogged = true;
                }
            } else {
                // 현재 상태가 dangerThreshold 이하인 경우
                if (dangerLogged && currentBusyRate < dangerThreshold) {
                    logger.info("{}% CLEARED: 위험 알림이 해지되었습니다. 현재콜 점유율: {}%", dangerThreshold, currentBusyRate);
                    dangerLogged = false;
                }
                if (warningLogged && currentBusyRate < warningThreshold) {
                    logger.info("{}% CLEARED: 경고 알림이 해지되었습니다. 현재콜 점유율: {}%", warningThreshold, currentBusyRate);
                    warningLogged = false;
                }
            }
        } catch (Exception e) {
            logger.error("모니터링 중 에러 발생: ", e);
        }
    }


    private void updateThresholds() {
        String warningThresholdStr = dbService.getCodeID("WARN");
        String dangerThresholdStr = dbService.getCodeID("ERROR");

        warningThreshold = Integer.parseInt(warningThresholdStr);
        dangerThreshold = Integer.parseInt(dangerThresholdStr);
    }
}
