package spotify.uri;

import se.michaelthelin.spotify.SpotifyApi;
import uri.UriProvider;

import java.net.URI;

public class SpotifyUriProvider implements UriProvider {

    private final SpotifyApi api;

    public SpotifyUriProvider(SpotifyApi api) {
        this.api = api;
    }

    @Override
    public URI provide() {
        return api.authorizationCodeUri()
                .build()
                .execute();
    }
}
