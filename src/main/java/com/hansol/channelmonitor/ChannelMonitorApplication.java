package com.hansol.channelmonitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChannelMonitorApplication {
    private static final Logger logger = LogManager.getLogger(ChannelMonitorApplication.class);

    public static void main(String[] args) {
        logger.info("Starting ChannelMonitorApplication...");

        MonitorService monitorService = new MonitorService();
        
        Runnable runnable = new Runnable() {
        	  @Override 
        	  public void run() {
        		    monitorService.monitorBusyRate();
        	  }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 15000, TimeUnit.MILLISECONDS);
    }
}