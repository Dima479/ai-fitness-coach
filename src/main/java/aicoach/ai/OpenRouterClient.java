package aicoach.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * @file OpenRouterClient.java
 * @brief Client HTTP pentru apelarea OpenRouter Chat Completions
 *
 * Construieste un request JSON cu mesaje (system + user), trimite cererea cu API key din env
 * si returneaza textul raspunsului
 */
public final class OpenRouterClient {

    /** Endpoint-ul OpenRouter pentru chat completions. */
    private static final String URL = "https://openrouter.ai/api/v1/chat/completions";

    /** Client HTTP  folosit pentru trimiterea cererilor. */
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();

    /** Mapper JSON (Jackson) pentru construire JSON. */
    private final ObjectMapper om = new ObjectMapper();

    /** Cheia API citita din variabila de mediu OPENROUTER_API_KEY. */
    private final String key = System.getenv("OPENROUTER_API_KEY");

    /**
     * Constructor: verifica existenta cheii API in variabilele de mediu.
     *
     * @throws IllegalStateException Daca OPENROUTER_API_KEY lipseste sau este goala.
     */
    public OpenRouterClient() {
        if (key == null || key.isBlank()) throw new IllegalStateException("OPENROUTER_API_KEY lipsa");
    }

    /**
     * Trimite un mesaj catre model si intoarce raspunsul generat.
     *
     * @param model Numele modelului folosit
     * @param system Mesajul system instructiuni pentru model
     * @param user Mesajul userullui
     * @param temperature Temperatura pentru variatie (0 = determinist, mai mare = mai creativ).
     * @param maxTokens Numarul maxim de tokeni generati in raspuns.
     * @return Continutul raspunsului
     * @throws RuntimeException Daca request-ul esueaza
     */
    public String chat(String model, String system, String user, double temperature, int maxTokens) {
        try {
            var reqJson = om.createObjectNode();
            reqJson.put("model", model);

            var messages = om.createArrayNode();
            messages.add(om.createObjectNode().put("role", "system").put("content", system));
            messages.add(om.createObjectNode().put("role", "user").put("content", user));

            reqJson.set("messages", messages);
            reqJson.put("temperature", temperature);
            reqJson.put("max_tokens", maxTokens);

            String body = reqJson.toString();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .timeout(Duration.ofSeconds(60))
                    .header("Authorization", "Bearer " + key)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) {
                throw new RuntimeException("OpenRouter " + resp.statusCode() + ": " + resp.body());
            }

            JsonNode json = om.readTree(resp.body());
            return json.at("/choices/0/message/content").asText("");
        } catch (Exception e) {
            throw new RuntimeException("OpenRouter call failed: " + e.getMessage(), e);
        }
    }
}
