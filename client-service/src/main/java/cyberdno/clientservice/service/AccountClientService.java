package cyberdno.clientservice.service;

import cyberdno.clientservice.dto.Account;
import cyberdno.clientservice.exception.AccountNotFoundException;
import cyberdno.clientservice.exception.ServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AccountClientService {

    private final WebClient webClient;

    public AccountClientService(WebClient accountServiceWebClient) {
        this.webClient = accountServiceWebClient;
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "getAccountFallback")
    public Mono<Account> getAccount(Long id) {
        return webClient.get()
                .uri("/api/accounts/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(),
                        resp -> Mono.error(new AccountNotFoundException(id)))
                .onStatus(HttpStatusCode::isError,
                        resp -> Mono.error(new ServiceException(
                                "Error while calling Account Service: " + resp.statusCode())))
                .bodyToMono(Account.class);
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "createAccountFallback")
    public Mono<Account> createAccount(Account account) {
        return webClient.post()
                .uri("/api/accounts")
                .bodyValue(account)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        resp -> Mono.error(new ServiceException(
                                "Error while creating account: " + resp.statusCode())))
                .bodyToMono(Account.class);
    }

    private Mono<Account> getAccountFallback(Long id, Throwable e) {
        if (e instanceof AccountNotFoundException) {
            return Mono.error(e);
        }
        return Mono.just(new Account(id, 0.0));
    }

    private Mono<Account> createAccountFallback(Account account, Throwable e) {
        return Mono.error(new ServiceException("Account Service unavailable: " + e.getMessage()));
    }
}
