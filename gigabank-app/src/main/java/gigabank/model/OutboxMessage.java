package gigabank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_messages", schema = "banking")
@Data
public class OutboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_type")
    @NotNull
    private String aggregateType;

    @Column(name = "aggregate_id")
    @NotNull
    private String aggregateId;

    @Column(name = "event_type")
    @NotNull
    private String eventType;

    @Column(columnDefinition = "jsonb")
    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @NotNull
    private boolean processed = false;
}
