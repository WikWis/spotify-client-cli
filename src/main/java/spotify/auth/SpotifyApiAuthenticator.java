package spotify.auth;

import se.michaelthelin.spotify.SpotifyApi;
import spotify.auth.callback.SpotifyCallbackServer;
import spotify.tokens.SpotifyTokensProvider;
import tokens.Tokens;
import tokens.TokensProvidingException;

import java.time.Duration;
import java.util.Optional;

public class SpotifyApiAuthenticator {

    private final int callbackPort;
    private final String callbackPath;

    public SpotifyApiAuthenticator(int callbackPort, String callbackPath) {
        this.callbackPort = callbackPort;
        this.callbackPath = callbackPath;
    }

    public boolean authenticate(SpotifyApi api) {
        SpotifyCallbackServer server = new SpotifyCallbackServer(callbackPort, callbackPath);

        Optional<String> code = server.awaitCode(Duration.ofMinutes(2));
        if (code.isEmpty()) return false;

        SpotifyTokensProvider provider = new SpotifyTokensProvider(api);
        Tokens tokens;
        try {
            tokens = provider.provide(code.get());
        } catch (TokensProvidingException e) {
            return false;
        }

        api.setAccessToken(tokens.accessToken());
        api.setRefreshToken(tokens.refreshToken());
        return true;
    }

}
