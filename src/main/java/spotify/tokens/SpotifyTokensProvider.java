package spotify.tokens;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import tokens.Tokens;
import tokens.TokensProvider;
import tokens.TokensProvidingException;

import java.io.IOException;

public class SpotifyTokensProvider implements TokensProvider {

    private final SpotifyApi api;

    public SpotifyTokensProvider(SpotifyApi api) {
        this.api = api;
    }

    @Override
    public Tokens provide(String authorizationCode) throws TokensProvidingException {
        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = api.authorizationCode(authorizationCode)
                    .build()
                    .execute();
            String accessToken = authorizationCodeCredentials.getAccessToken();
            String refreshToken = authorizationCodeCredentials.getRefreshToken();
            return new Tokens(accessToken, refreshToken);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new TokensProvidingException(
                    "Failed to exchange Spotify authorization code for tokens",
                    e
            );
        }
    }
}
