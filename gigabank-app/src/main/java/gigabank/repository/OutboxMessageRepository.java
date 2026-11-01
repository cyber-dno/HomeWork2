package gigabank.repository;

import gigabank.model.OutboxMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OutboxMessageRepository  extends CrudRepository<OutboxMessage, Long> {
    List<OutboxMessage> findByProcessedFalseOrderByCreatedAtAsc();

    int countOutboxMessagesByProcessed(boolean processed);
}
