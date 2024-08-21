package com.hansol.channelmonitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class ChannelMonitorApplication {
    private static final Logger logger = LogManager.getLogger(ChannelMonitorApplication.class);

    public static void main(String[] args) {
        logger.info("Starting ChannelMonitorApplication...");

        MonitorService monitorService = new MonitorService();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                monitorService.monitorBusyRate();
            }
        }, 0, 15000);
    }
}