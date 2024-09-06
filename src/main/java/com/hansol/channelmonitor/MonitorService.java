package com.hansol.channelmonitor;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonitorService {
	  private static final Logger logger = LogManager.getLogger(MonitorService.class);
	  private DatabaseService dbService = new DatabaseService();
	  private int warningThreshold = 70;
	  private int dangerThreshold = 80;
	
	  // 센터별 상태를 관리
	  private Map<String, Boolean> dangerLoggedMap = new HashMap<>();
	  private Map<String, Boolean> warningLoggedMap = new HashMap<>();
	
	  public void monitorBusyRate() {
	      try {
	          // 임계치 조회
	          getThresholds();
	
	          // BusyRate 조회
	          Map<String, Integer> busyRates = dbService.getBusyRate();
	
	          System.out.println("busyRates : " + busyRates);
	
	          // 초기화는 처음 센터가 추가될 때만 수행
	          for (String center : busyRates.keySet()) {
	              dangerLoggedMap.putIfAbsent(center, false); // 최초 설정 시에만 false로 설정
	              warningLoggedMap.putIfAbsent(center, false);
	          }
	
	          for (Map.Entry<String, Integer> entry : busyRates.entrySet()) {
	              String centerName = entry.getKey();
	              int currentBusyRate = entry.getValue();
	
	              boolean dangerLogged = dangerLoggedMap.get(centerName);
	              boolean warningLogged = warningLoggedMap.get(centerName);
	
	              // 위험 임계치 이상 
	              if (currentBusyRate >= dangerThreshold) {
	              	  if (!warningLogged) {
	              		  // 경고 로그남김 (경고도 위험 포함임) 
	              		  logger.info("[IVRA] {}_채널점유율: {}% 경고, 현재 채널점유율: {}%", centerName, warningThreshold, currentBusyRate);
	              		  warningLoggedMap.put(centerName, true);
	              	  }
	                  if (!dangerLogged) {
	                      logger.info("[IVRA] {}_채널점유율: {}% 위험, 현재 채널점유율: {}%", centerName, dangerThreshold, currentBusyRate);
	                      dangerLoggedMap.put(centerName, true);  // 위험 로깅 상태 갱신
	                  }
	              } 
	              // 경고 임계치 이상 & 위험 임계치 미만
	              else if (currentBusyRate >= warningThreshold) {
	                  if (dangerLogged) {
	                      // 위험 해제 로그
	                      logger.info("[IVRO] {}_채널점유율: {}% 위험 해제, 현재 채널점유율: {}%", centerName, dangerThreshold, currentBusyRate);
	                      dangerLoggedMap.put(centerName, false);  // 위험 상태 해제
	                  }
	
	                  if (!warningLogged) {
	                      logger.info("[IVRA] {}_채널점유율: {}% 경고, 현재 채널점유율: {}%", centerName, warningThreshold, currentBusyRate);
	                      warningLoggedMap.put(centerName, true);  // 경고 로깅 상태 갱신
	                  }
	              } 
	              // 경고 임계치 미만(해제 상태)
	              else {
	                  // 위험 상태 해제 처리
	                  if (dangerLogged) {
	                      logger.info("[IVRO] {}_채널점유율: {}% 위험 해제, 현재 채널점유율: {}%", centerName, dangerThreshold, currentBusyRate);
	                      dangerLoggedMap.put(centerName, false);  // 위험 상태 해제
	                  }
	                  // 경고 상태 해제 처리
	                  if (warningLogged) {
	                      logger.info("[IVRO] {}_채널점유율: {}% 경고 해제, 현재 채널점유율: {}%", centerName, warningThreshold, currentBusyRate);
	                      warningLoggedMap.put(centerName, false);  // 경고 상태 해제
	                  }
	              }
	          }
	      } catch (Exception e) {
	          logger.error("모니터링 중 에러 발생: ", e);
	      }
	  }
	
	  private void getThresholds() {
	      String warningThresholdStr = dbService.getCodeID("위험");
	      String dangerThresholdStr = dbService.getCodeID("심각");
	
	      warningThreshold = Integer.parseInt(warningThresholdStr); // 예: 70
	      dangerThreshold = Integer.parseInt(dangerThresholdStr);  // 예: 80 
	  }
}

