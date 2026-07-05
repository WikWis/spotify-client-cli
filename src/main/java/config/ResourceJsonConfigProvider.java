package config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

public class ResourceJsonConfigProvider implements ConfigProvider {

    private static final String CONFIG_FILE = "settings.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final JsonNode config;

    public ResourceJsonConfigProvider() throws IOException {
        try (InputStream inputStream = openConfigFile()) {
            this.config = OBJECT_MAPPER.readTree(inputStream);
        }
    }

    @Override
    public String clientId() {
        return getValue("ClientID");
    }

    @Override
    public String clientSecret() {
        return getValue("ClientSecret");
    }

    @Override
    public URI redirectUri() {
        return URI.create(getValue("RedirectUri"));
    }

    private String getValue(String key) {
        JsonNode value = config.get(key);

        if (value == null || !value.isTextual() || value.asText().isBlank()) {
            throw new IllegalStateException("Missing or invalid configuration field: " + key);
        }

        return value.asText();
    }

    private InputStream openConfigFile() {
        InputStream inputStream = ResourceJsonConfigProvider.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE);

        return Objects.requireNonNull(inputStream, "Missing configuration file: " + CONFIG_FILE);
    }
}
