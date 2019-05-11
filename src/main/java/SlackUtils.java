import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class SlackUtils {

    public static void sendMessage(String message) {
        SlackMessage slackMessage = SlackMessage.builder().text(message).build();

        CloseableHttpClient client = HttpClients.createDefault();
        String slackWebhookUrl = "https://hooks.slack.com/services/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";
        HttpPost httpPost = new HttpPost(slackWebhookUrl);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(slackMessage);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            client.execute(httpPost);
            client.close();

        } catch (IOException e) {
            throw new RuntimeException("Something went wrong!");
        }
    }
}