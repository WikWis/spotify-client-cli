package app;

import config.ConfigProvider;
import config.ResourceJsonConfigProvider;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

public class Main {

    static void main() {

        ConfigProvider configProvider;
        try {
            configProvider = new ResourceJsonConfigProvider();
        } catch (IOException _) {
            System.out.println("Błąd podczas wczytywania konfiguracji!");
            return;
        }

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(configProvider.clientId())
                .setClientSecret(configProvider.clientSecret())
                .setRedirectUri(configProvider.redirectUri())
                .build();

        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .build();

        URI uri = authorizationCodeUriRequest.execute();
        System.out.println("URI: " + uri.toString());

        System.out.print("Code:");
        String code = new Scanner(System.in).nextLine();

        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();

        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

}
