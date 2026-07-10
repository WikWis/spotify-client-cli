package spotify;

import config.ConfigProvider;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import spotify.api.SpotifyApiBuilder;
import spotify.auth.SpotifyApiAuthenticator;
import spotify.uri.SpotifyUriProvider;

import java.io.IOException;
import java.net.URI;

public class SpotifyApplication {

    private final ConfigProvider configProvider;

    public SpotifyApplication(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    public void run() {
        // Build object which handles requests to api
        SpotifyApi spotifyApi = SpotifyApiBuilder.build(configProvider);

        // Display URI on which user must click and then login to Spotify
        displayAuthorizationUri(spotifyApi);

        // Build Authenticator and authenticate api object
        SpotifyApiAuthenticator authenticator = new SpotifyApiAuthenticator(8888, "/callback");
        if (!authenticator.authenticate(spotifyApi)) {
            System.err.println("Nie udało się uwierzytelnić użytkownika.");
            return;
        }

        // Get User Information, display Name
        displayUserName(spotifyApi);
    }

    private void displayUserName(SpotifyApi spotifyApi) {
        try {
            GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile()
                    .build();
            User user = getCurrentUsersProfileRequest.execute();
            System.out.println("Display name: " + user.getDisplayName());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayAuthorizationUri(SpotifyApi spotifyApi) {
        SpotifyUriProvider uriProvider = new SpotifyUriProvider(spotifyApi);
        URI authorizationUri = uriProvider.provide();

        System.out.println("Otwórz ten adres w przeglądarce:");
        System.out.println(authorizationUri);
    }
}