package org.NenDuLieuTimeSeries.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.NenDuLieuTimeSeries.model.UserResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Gọi API lấy dữ liệu user.
     * Ném IOException với message "Content Error: ..." nếu JSON không hợp lệ.
     */
    public UserResponse getUser() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/user"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        try {
            return mapper.readValue(response.body(), UserResponse.class);
        } catch (JsonProcessingException e) {
            throw new IOException("Content Error: Invalid JSON response - " + e.getOriginalMessage());
        }
    }
}