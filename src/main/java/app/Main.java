package app;

import config.ConfigProvider;
import config.ResourceJsonConfigProvider;
import spotify.SpotifyApplication;

import java.io.IOException;

public class Main {

    static void main() {
        try {
            ConfigProvider configProvider = new ResourceJsonConfigProvider("settings.json");
            SpotifyApplication application = new SpotifyApplication(configProvider);

            application.run();
        } catch (IOException e) {
            System.err.println("Configuration wasn't loaded. " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Exception while running the app." + e.getMessage());
        }
    }
}
