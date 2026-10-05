package co.edu.corhuila.abbr.worker.application.port.in;

import java.time.Instant;

public interface Job {

    /** One run's summary. A job reports partial failure; it does not throw on it. */
    record Result(int processed, int failed) { }

    String name();

    /** Past the deadline the job stops and leaves the rest for the next run. */
    Result run(Instant deadline);
}
