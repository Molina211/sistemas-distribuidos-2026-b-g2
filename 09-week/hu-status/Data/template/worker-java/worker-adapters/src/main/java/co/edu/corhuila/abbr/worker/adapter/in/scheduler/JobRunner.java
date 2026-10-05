package co.edu.corhuila.abbr.worker.adapter.in.scheduler;

import co.edu.corhuila.abbr.worker.adapter.Correlation;
import co.edu.corhuila.abbr.worker.application.port.in.Job;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Inbound adapter of the worker: runs the job with a fixed delay between runs,
 * so runs never overlap inside one process. A run longer than runTimeout stops
 * and leaves the rest for the next run.
 */
public class JobRunner {

    private static final Logger LOG = LoggerFactory.getLogger(JobRunner.class);
    private final Job job;
    private final Duration runTimeout;

    public JobRunner(Job job, Duration runTimeout) {
        this.job = job;
        this.runTimeout = runTimeout;
    }

    @Scheduled(fixedDelayString = "${worker.expire-every-ms}")
    public void run() {
        // One correlation id per run: every call to another service carries it (the
        // client reads it from the MDC), so the run can be followed across the system.
        MDC.put(Correlation.MDC_KEY, UUID.randomUUID().toString());
        long start = System.nanoTime();
        try {
            Job.Result r = job.run(Instant.now().plus(runTimeout));
            LOG.info("run finished job={} processed={} failed={} durationMs={}", job.name(), r.processed(),
                    r.failed(), (System.nanoTime() - start) / 1_000_000);
        } catch (RuntimeException e) {
            LOG.error("run failed job={}", job.name(), e);
        } finally {
            MDC.remove(Correlation.MDC_KEY);
        }
    }
}
