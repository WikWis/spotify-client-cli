package api;

import config.ConfigProvider;
import se.michaelthelin.spotify.SpotifyApi;

public class ApiBuilder {

    private final ConfigProvider provider;

    private ApiBuilder(ConfigProvider provider) {
        this.provider = provider;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SpotifyApi build() {
        return new SpotifyApi.Builder()
                .setClientId(provider.clientId())
                .setClientSecret(provider.clientSecret())
                .setRedirectUri(provider.redirectUri())
                .build();
    }

    public static class Builder {

        private ConfigProvider provider;

        public Builder configProvider(ConfigProvider provider) {
            this.provider = provider;
            return this;
        }

        public ApiBuilder build() {
            if (provider == null) {
                throw new IllegalStateException("ConfigProvider must be set");
            }

            return new ApiBuilder(provider);
        }
    }
}