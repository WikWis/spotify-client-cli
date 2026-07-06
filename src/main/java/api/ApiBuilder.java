package api;

import config.ConfigProvider;
import se.michaelthelin.spotify.SpotifyApi;

public class ApiBuilder {

    private ApiBuilder() {}

    public static SpotifyApi build(ConfigProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("ConfigProvider must be set");
        }

        return new SpotifyApi.Builder()
                .setClientId(provider.clientId())
                .setClientSecret(provider.clientSecret())
                .setRedirectUri(provider.redirectUri())
                .build();
    }

}