package tokens;

public interface TokensProvider {
    Tokens provide(String authorizationCode) throws TokensProvidingException;
}
