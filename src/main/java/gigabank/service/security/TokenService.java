package gigabank.service.security;

import gigabank.classifier.TokenType;
import gigabank.model.Token;
import gigabank.model.User;
import gigabank.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public boolean isTokenValid(String jwtToken) {
        return tokenRepository.findByToken(jwtToken)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
    }

    public void revokeToken(String jwt) {
        Optional<Token> token = tokenRepository.findByToken(jwt);
        if (token.isPresent() && isTokenValid(jwt)) {
            Token t = token.get();
            t.setExpired(true);
            t.setRevoked(true);
            tokenRepository.save(t);
        }
    }

    public List<Token> getAllUserTokens(Long id) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(id);
        if (validUserTokens.isEmpty()) {
            return List.of();
        }
        return validUserTokens;
    }

    @Scheduled(fixedRate = 86400000)
    public void cleanExpiredTokens() {
        tokenRepository.deleteAllByExpiredTrueOrRevokedTrue();
    }
}
