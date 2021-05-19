package pubsub.subscriber;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pubsub.RedisClientBuilder;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Subscriber {

    private static final Logger LOGGER = LoggerFactory.getLogger(Subscriber.class);

    private static final int COUNTERS_PRINT_SEC = 5;

    private final String pattern;
    private final RedisClient client;

    private final String id = UUID.randomUUID().toString();

    private final ConcurrentHashMap<String, Integer> messages = new ConcurrentHashMap<>();

    public Subscriber(String pattern) {
        this.pattern = pattern;
        this.client = RedisClientBuilder.build();
    }

    public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 500; ++i) {
            int channel = i;
            executor.execute(() -> createConnection(channel));
        }

        logCounters();
    }

    private void createConnection(int channel) {
        StatefulRedisPubSubConnection<String, String> subscription = client.connectPubSub();
        subscription.sync().subscribe(pattern + channel);
        subscription.addListener(new RedisPubSubAdapter<String, String>() {
            @Override
            public void message(String channel, String message) {
                messages.compute(channel, (key, val) -> val == null ? 1 : val + 1);
            }
        });
    }

    private void logCounters() {
        while (true) {
            try {
                messages.forEach((channel, count) -> LOGGER.info("{} : [{}] : [{}]", id, channel, count));
                Thread.sleep(TimeUnit.SECONDS.toMillis(COUNTERS_PRINT_SEC));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}
