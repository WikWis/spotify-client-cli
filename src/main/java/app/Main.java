package app;

import api.ApiBuilder;
import authorization.OAuthCallbackServer;
import config.ConfigProvider;
import config.ResourceJsonConfigProvider;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;

public class Main {

    static void main() {

        ConfigProvider configProvider;
        try {
            configProvider = new ResourceJsonConfigProvider();
        } catch (IOException _) {
            System.out.println("Błąd podczas wczytywania konfiguracji!");
            return;
        }

        // Build Api
        SpotifyApi spotifyApi = ApiBuilder.build(configProvider);

        // Generate URI
        URI uri = spotifyApi.authorizationCodeUri()
                .build()
                .execute();

        System.out.println("URI: " + uri.toString());

        // Get Code from Redirect Uri
        OAuthCallbackServer server = new OAuthCallbackServer();
        Optional<String> code = server.awaitCode(Duration.ofMinutes(2L));
        if (code.isEmpty()) {
            System.out.println("Problem while getting code!");
            return;
        }

        // Set Access Token, Set Refresh Token
        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = spotifyApi.authorizationCode(code.get())
                    .build()
                    .execute();

            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Get User Information, display Name
        try {
            GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile()
                    .build();
            User user = getCurrentUsersProfileRequest.execute();
            System.out.println("Display name: " + user.getDisplayName());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}
