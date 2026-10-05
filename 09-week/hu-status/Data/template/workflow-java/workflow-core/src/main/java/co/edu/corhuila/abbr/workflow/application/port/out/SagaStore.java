package co.edu.corhuila.abbr.workflow.application.port.out;

import co.edu.corhuila.abbr.workflow.domain.saga.SagaInstance;
import java.util.Optional;

public interface SagaStore {

    record CreateResult(SagaInstance saga, boolean created) { }

    /**
     * Stores a new saga under its idempotency key, atomically. If the key already
     * has a saga, stores nothing and returns that one with created=false.
     */
    CreateResult create(SagaInstance saga, String key);

    void save(SagaInstance saga);

    Optional<SagaInstance> get(String id);
}
