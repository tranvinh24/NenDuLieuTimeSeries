package org.NenDuLieuTimeSeries;

import org.NenDuLieuTimeSeries.scheduler.SchedulerTask;
import org.NenDuLieuTimeSeries.server.FakeApiServer;

public class Main {

    public static void main(String[] args) throws Exception {

        // Khởi động webservice
        new FakeApiServer().start();

        // Chạy scheduler
        new SchedulerTask().start();

        System.out.println("Application started...");

    }

}