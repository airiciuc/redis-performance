package pubsub.publisher;

import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsReporter {

    private final MetricRegistry metrics = new MetricRegistry();
    private final Meter messages = metrics.meter("messages");
    private final Histogram latencies = metrics.histogram("latencies");
    private final ScheduledReporter reporter;

    private final Map<Integer, Instant> timestamps = new ConcurrentHashMap<>();
    private final AtomicInteger errors = new AtomicInteger();

    public StatsReporter(String channel) throws IOException {
        File file = new File("metrics/" + channel);

        if (!file.exists() && !file.mkdirs()) {
            throw new IOException("Can not create reports location");
        }

        reporter = CsvReporter.forRegistry(metrics)
                .formatFor(Locale.US)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(file);
        reporter.start(1, TimeUnit.MINUTES);
    }

    public void onMessageSent(String message) {
        messages.mark();
        timestamps.put(message.hashCode(), Instant.now());
    }

    public void onMessageArrived(String message) {
        Instant sent = timestamps.remove(message.hashCode());
        if (sent != null) {
            Instant arrived = Instant.now();
            Duration duration = Duration.between(sent, arrived);
            latencies.update(duration.toMillis());
        }
    }

    public void onError() {
        errors.incrementAndGet();
    }

    public long getTotalMessagesSent() {
        return messages.getCount();
    }

    public int getTotalErrors() {
        return errors.get();
    }

    public void close() {
        reporter.report();
        reporter.stop();
    }
}
