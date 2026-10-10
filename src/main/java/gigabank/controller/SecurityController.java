package gigabank.controller;

import gigabank.model.Token;
import gigabank.model.User;
import gigabank.repository.UserRepository;
import gigabank.service.security.JwtService;
import gigabank.service.security.TokenService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class SecurityController {
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry;

    @GetMapping("/basic/hello")
    public String basicHello() {
        return "Basic Auth";
    }

    @GetMapping("/basic/user/hello")
    public String basicUserHello()  {
        return "Basic User Auth";
    }

    @GetMapping("/basic/admin/hello")
    public String basicAdminHello() {
        return "Basic Admin Auth";
    }

    @GetMapping("/form/hello")
    public String formHello() {
        return "Form Login";
    }

    @PostMapping("/form/logout")
    public ResponseEntity<Void> logout(ServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = ((HttpServletRequest) request).getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/login?logout"))
                .build();
    }

    @PostMapping("/form/logout-all/{username}")
    public String logoutAll(@PathVariable String username) {
        sessionRegistry.getAllPrincipals().stream()
                .filter(u -> ((org.springframework.security.core.userdetails.User) u).getUsername().equals(username))
                .findFirst()
                .ifPresent(user -> {
                    sessionRegistry.getAllSessions(user, false)
                            .forEach(SessionInformation::expireNow);
                });
        return "redirect:/login?logout";
    }

    // Получить активные сессии
    @GetMapping("/form/active")
    public List<String> getActiveSessions() {
        return sessionRegistry.getAllPrincipals().stream()
                .map(u -> ((org.springframework.security.core.userdetails.User) u).getUsername())
                .toList();
    }

    @PostMapping("/jwt/token")
    public String getToken(@RequestParam String username,
                           @RequestParam List<String> roles) {
        User user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        String jwt = jwtService.generateToken(username, roles);
        tokenService.saveUserToken(user, jwt);
        return jwt;
    }

    @PostMapping("/jwt/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authHeader
    ) {
        String jwt = authHeader.substring(7);
        tokenService.revokeToken(jwt); // Помечаем токен как отозванный
        return ResponseEntity.ok().build();
    }

    @PostMapping("/jwt/logout-all")
    public ResponseEntity<?> logoutAll(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        tokenService.revokeAllUserTokens(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/jwt/sessions")
    public List<Token> getActiveSessions(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return tokenService.getAllUserTokens(user.getId());
    }

    @GetMapping("/jwt/hello")
    public String jwtHello(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = (header != null && header.startsWith("Bearer "))
                ? header.substring(7)
                : null;

        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getName();

        return "Hello, JWT! User: " + username + ", token: " + token;
    }

    @GetMapping("/oauth2/hello")
    public String oauth2Hello(Authentication authentication) {
        return "OAuth2 Login. User: " + authentication.getName()
                + ", type: " + authentication.getClass().getSimpleName();
    }
}
