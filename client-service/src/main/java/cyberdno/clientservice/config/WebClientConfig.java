package cyberdno.clientservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Configuration
public class WebClientConfig {

    private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${account-service.base-url}")
    private String accountServiceUrl;

    @Value("${account-service.username}")
    private String username;

    @Value("${account-service.password}")
    private String password;

    @Bean
    public WebClient accountServiceWebClient() {
        return WebClient.builder()
                .baseUrl(accountServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(basicAuthentication(username, password))
                .filter(loggingFilter())
                .build();
    }

    private ExchangeFilterFunction basicAuthentication(String user, String pass) {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            String auth = "Basic " + Base64.getEncoder()
                    .encodeToString((user + ":" + pass).getBytes());
            ClientRequest request = ClientRequest.from(clientRequest)
                    .header(HttpHeaders.AUTHORIZATION, auth)
                    .build();
            return Mono.just(request);
        });
    }

    private ExchangeFilterFunction loggingFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            log.info("Request: {} {}", req.method(), req.url());
            return Mono.just(req);
        }).andThen(ExchangeFilterFunction.ofResponseProcessor(resp -> {
            log.info("Response: {}", resp.statusCode());
            return Mono.just(resp);
        }));
    }
}
