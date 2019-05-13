import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SlackUtils {
    private static final String slackWebhookUrl = "https://hooks.slack.com/services/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";

    public static Integer sendMessage(String message) throws IOException, ExecutionException, InterruptedException {
        SlackMessage slackMessage = SlackMessage.builder().text(message).build();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(slackMessage);

        HttpRequest request = HttpRequest.newBuilder(URI.create(slackWebhookUrl)).headers("Accept", "application/json", "Content-type", "application/json").POST(HttpRequest.BodyPublishers.ofString(String.valueOf(json))).build();

        CompletableFuture<Integer> integerCompletableFuture = HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::statusCode);
        return integerCompletableFuture.get();


    }
}