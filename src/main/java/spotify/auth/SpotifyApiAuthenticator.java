package spotify.auth;

import se.michaelthelin.spotify.SpotifyApi;
import spotify.auth.callback.SpotifyCallbackServer;
import spotify.tokens.SpotifyTokensProvider;
import tokens.Tokens;
import tokens.TokensProvidingException;

import java.time.Duration;
import java.util.Optional;

public class SpotifyApiAuthenticator {

    public boolean authenticate(SpotifyApi api) {
        SpotifyCallbackServer server = new SpotifyCallbackServer
                (
                        api.getRedirectURI().getPort(),
                        api.getRedirectURI().getRawPath()
                );

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
