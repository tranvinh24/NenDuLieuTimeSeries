package org.NenDuLieuTimeSeries.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;

public class FakeApiServer {

    private final Random random = new Random();

    public void start() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/user", this::handleRequest);

        server.setExecutor(null);

        server.start();

        System.out.println("Fake API running at http://localhost:8080/user");
    }

    private void handleRequest(HttpExchange exchange) throws IOException {

        // Giả lập: 10% cơ hội delay lớn (> 10ms) để trigger timeout,
        // 90% còn lại trả về bình thường trong 0-5ms
        try {
            if (random.nextInt(10) == 0) {
                Thread.sleep(15 + random.nextInt(10)); // 15-24ms -> timeout
            } else {
                Thread.sleep(random.nextInt(5));        // 0-4ms  -> bình thường
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int value = 4000 + random.nextInt(501);

        String json = "{ \"user\" : " + value + " }";

        byte[] responseBytes = json.getBytes();

        exchange.getResponseHeaders().add("Content-Type", "application/json");

        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}