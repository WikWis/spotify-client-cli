package config;

import java.net.URI;

public interface ConfigProvider {
    String clientId();
    String clientSecret();
    URI redirectUri();
}
