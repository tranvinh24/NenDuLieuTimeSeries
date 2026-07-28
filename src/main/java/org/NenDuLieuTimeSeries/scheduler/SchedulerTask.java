package org.NenDuLieuTimeSeries.scheduler;

import org.NenDuLieuTimeSeries.client.ApiClient;
import org.NenDuLieuTimeSeries.model.UserResponse;
import org.NenDuLieuTimeSeries.service.CompressionService;
import org.NenDuLieuTimeSeries.service.SaveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class SchedulerTask {

    private static final Logger logger =
            LoggerFactory.getLogger(SchedulerTask.class);

    // Scheduler gọi mỗi 2 giây
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    // Executor dùng chung để gọi API với timeout
    private final ExecutorService apiExecutor =
            Executors.newSingleThreadExecutor();

    private final CompressionService compression =
            new CompressionService();

    private final ApiClient apiClient =
            new ApiClient();

    public void start() {

        scheduler.scheduleAtFixedRate(this::fetchAndLog, 0, 2, TimeUnit.SECONDS);

        // Graceful shutdown khi ứng dụng tắt
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.shutdown();
            apiExecutor.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) scheduler.shutdownNow();
                if (!apiExecutor.awaitTermination(5, TimeUnit.SECONDS)) apiExecutor.shutdownNow();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }

    private void fetchAndLog() {

        Future<UserResponse> future = apiExecutor.submit(apiClient::getUser);

        try {

            // Timeout 10ms như yêu cầu
            UserResponse response = future.get(10, TimeUnit.MILLISECONDS);

            int user = response.getUser();

            SaveType type = compression.check(user);

            switch (type) {
                case INFO  -> logger.info("User = {}", user);
                case DEBUG -> logger.debug("User = {}", user);
                case NONE  -> { /* Không ghi log */ }
            }

        } catch (TimeoutException e) {

            logger.error("Timeout: API did not respond within 10ms");
            future.cancel(true);

        } catch (ExecutionException e) {

            // Phân biệt content error vs các lỗi khác
            String cause = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            if (cause != null && cause.startsWith("Content Error")) {
                logger.error("Content Error: {}", cause);
            } else {
                logger.error("API Error: {}", cause);
            }

        } catch (Exception e) {

            logger.error("Unexpected error: {}", e.getMessage());

        }
    }
}
