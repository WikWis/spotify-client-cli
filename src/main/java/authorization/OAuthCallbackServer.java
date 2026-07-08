package authorization;

import io.fusionauth.http.log.Level;
import io.fusionauth.http.log.Logger;
import io.fusionauth.http.server.HTTPHandler;
import io.fusionauth.http.server.HTTPListenerConfiguration;
import io.fusionauth.http.server.HTTPResponse;
import io.fusionauth.http.server.HTTPServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.*;

public class OAuthCallbackServer {

    private static final int PORT = 8888;
    private static final String CALLBACK_PATH = "/callback";

    private final CompletableFuture<String> codeFuture = new CompletableFuture<>();
    private final HTTPServer server;

    public OAuthCallbackServer() {
        this.server = new HTTPServer()
                .withHandler(handler)
                .withListener(new HTTPListenerConfiguration(PORT))
                .withLoggerFactory((_) -> NO_OP_LOGGER);
    }

    public Optional<String> awaitCode(Duration timeout) {
        try {
            this.server.start();
            return Optional.of(codeFuture.get(timeout.toMillis(), TimeUnit.MILLISECONDS));
        } catch (InterruptedException | TimeoutException | ExecutionException | CancellationException e) {
            return Optional.empty();
        } finally {
            this.server.close();
        }
    }

    private final HTTPHandler handler = (req, res) -> {

        if (!CALLBACK_PATH.equals(req.getPath())) {
            this.respond(res, 400, "<h1>Bad path!</h1>");
            codeFuture.cancel(true);
            return;
        }

        Optional<String> code = Optional.ofNullable(req.getParameter("code"));
        if (code.isEmpty() || code.get().isBlank()) {
            this.respond(res, 400, "<h1>Code is empty, or does not exist!</h1>");
            codeFuture.cancel(true);
            return;
        }

        boolean completed = codeFuture.complete(code.get());

        this.respond(
                res,
                completed ? 200 : 409,
                completed
                        ? "<h1>Please return back to the terminal!</h1>"
                        : "<h1>Authorization callback was already handled.</h1>"
        );
    };

    private void respond(HTTPResponse response, int status, String body) throws IOException {
        response.setStatus(status);
        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        response.close();
    }

    private static final Logger NO_OP_LOGGER = new Logger() {
        public void debug(String message) {
        }

        public void debug(String message, Object... values) {
        }

        public void debug(String message, Throwable throwable) {
        }

        public void info(String message) {
        }

        public void info(String message, Object... values) {
        }

        public void error(String message) {
        }

        public void error(String message, Throwable throwable) {
        }

        public void trace(String message) {
        }

        public void trace(String message, Object... values) {
        }

        public boolean isTraceEnabled() {
            return false;
        }

        public boolean isDebugEnabled() {
            return false;
        }

        public boolean isInfoEnabled() {
            return false;
        }

        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public void setLevel(Level level) {
        }
    };

}
