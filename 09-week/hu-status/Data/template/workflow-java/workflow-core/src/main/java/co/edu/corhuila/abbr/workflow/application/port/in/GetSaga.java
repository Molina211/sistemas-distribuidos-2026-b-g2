package co.edu.corhuila.abbr.workflow.application.port.in;

import co.edu.corhuila.abbr.workflow.domain.saga.SagaInstance;

public interface GetSaga {
    /** @throws co.edu.corhuila.abbr.workflow.domain.saga.UnknownSagaException if it does not exist */
    SagaInstance get(String id);
}
