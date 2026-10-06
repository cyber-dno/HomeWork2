package gigabank.config;

import gigabank.service.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtFilter;

    public SecurityConfig(JwtAuthFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /*
        # curl -u user:user http://localhost:8080/auth/basic/hello
        GET http://localhost:8080/auth/basic/hello
        Authorization: Basic user user
    */
    @Bean
    public SecurityFilterChain basicFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/auth/basic/**")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    /*
        fetch('/auth/form/logout', { method: 'POST', redirect: 'follow' })
            .then(r => { console.log(r.status, r.url); window.location.href = r.url; });

        fetch('/auth/form/logout-all/user', { method: 'POST', redirect: 'follow' })
            .then(r => { console.log(r.status, r.url); window.location.href = r.url; });
    */
    @Bean
    public SecurityFilterChain formFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // чтобы работал logout запросами
                .securityMatcher("/auth/form/**", "/login")
                .sessionManagement(session -> session
                        .maximumSessions(2)
                        .expiredUrl("/login")
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(sessionRegistry())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/form/**").authenticated()
                        .requestMatchers("/login").permitAll())
                .formLogin(form -> form
                        .defaultSuccessUrl("/auth/form/hello")
                );
        return http.build();
    }

    /*
        # curl -X POST "http://localhost:8080/auth/jwt/token?username=user&roles=ROLE_USER"
        POST http://localhost:8080/auth/jwt/token?username=user&roles=ROLE_USER

        ###
        # curl -H "Authorization: Bearer <TOKEN>" http://localhost:8080/auth/jwt/hello
        GET http://localhost:8080/auth/jwt/hello
        Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTc4OTA3ODIzMiwiZXhwIjoxNzg5MDgxODMyfQ.7ApzfXNwsBfn1FGo-4S48mHoXEGlF-vAIeNGFus5KBo

        ###
        # curl -H "Authorization: Bearer <TOKEN>" http://localhost:8080/auth/jwt/sessions
        GET http://localhost:8080/auth/jwt/sessions
        Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTc4OTE1MTEwMywiZXhwIjoxNzg5MTU0NzAzfQ.RWZbe3QF6QhvpKoryO5pSqQHUlRnb6NfQYjutBWCuvM

        ###
        # curl -X POST -H "Authorization: Bearer <TOKEN>" http://localhost:8080/auth/jwt/logout
        POST http://localhost:8080/auth/jwt/logout
        Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTc4OTE1MDg3OSwiZXhwIjoxNzg5MTU0NDc5fQ.LaCP7gS7Pdc49JqdM3wFvmqTQGO5dxnKLxsYaDiAHQI

        ###
        # curl -X POST -H "Authorization: Bearer <TOKEN>" http://localhost:8080/auth/jwt/logout-all
        POST http://localhost:8080/auth/jwt/logout-all
        Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTc4OTE1MTEwMywiZXhwIjoxNzg5MTU0NzAzfQ.RWZbe3QF6QhvpKoryO5pSqQHUlRnb6NfQYjutBWCuvM

        ###
    */
    @Bean
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/auth/jwt/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/jwt/token").permitAll()
                        .anyRequest().authenticated())
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(Customizer.withDefaults()));
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/auth/oauth2/**"
                        , "/login"
                        , "/oauth2/authorization/**"
                        , "/login/oauth2/code/**"
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login"
                                , "/oauth2/authorization/**"
                                , "/login/oauth2/code/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/auth/oauth2/hello")
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin"))
                .roles("ADMIN")
                .build();
        UserDetails user = User.builder()
                .username("user")
                .password(encoder.encode("user"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
