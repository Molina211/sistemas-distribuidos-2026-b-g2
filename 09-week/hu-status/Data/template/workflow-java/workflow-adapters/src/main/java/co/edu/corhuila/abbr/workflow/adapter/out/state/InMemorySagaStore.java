package co.edu.corhuila.abbr.workflow.adapter.out.state;

import co.edu.corhuila.abbr.workflow.application.port.out.IdGenerator;
import co.edu.corhuila.abbr.workflow.application.port.out.SagaStore;
import co.edu.corhuila.abbr.workflow.domain.saga.SagaInstance;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Keeps the example runnable. It does NOT survive a restart — see the README. */
public class InMemorySagaStore implements SagaStore {

    private final Map<String, SagaInstance> items = new HashMap<>();
    private final Map<String, String> keys = new HashMap<>();

    @Override
    public synchronized CreateResult create(SagaInstance saga, String key) {
        String existing = keys.get(key);
        if (existing != null) {
            return new CreateResult(items.get(existing).copy(), false);
        }
        keys.put(key, saga.id());
        items.put(saga.id(), saga.copy());
        return new CreateResult(saga, true);
    }

    @Override
    public synchronized void save(SagaInstance saga) {
        items.put(saga.id(), saga.copy());
    }

    @Override
    public synchronized Optional<SagaInstance> get(String id) {
        return Optional.ofNullable(items.get(id)).map(SagaInstance::copy);
    }

    public static class UuidGenerator implements IdGenerator {
        @Override
        public String newId() {
            return UUID.randomUUID().toString();
        }
    }
}
