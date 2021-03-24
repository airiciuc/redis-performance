package pubsub.subscriber;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.reactive.PatternMessage;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pubsub.RedisClientBuilder;
import reactor.core.publisher.FluxSink;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Subscriber {

    private static final Logger LOGGER = LoggerFactory.getLogger(Subscriber.class);

    private static final int COUNTERS_PRINT_SEC = 60;

    private final String pattern;
    private final RedisClient client;

    private final String id = UUID.randomUUID().toString();

    private final ConcurrentHashMap<String, Integer> messages = new ConcurrentHashMap<>();

    public Subscriber(String pattern) {
        this.pattern = pattern;
        this.client = RedisClientBuilder.build();
    }

    public void run() {
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        RedisPubSubReactiveCommands<String, String> reactive = connection.reactive();

        reactive.psubscribe(pattern).block();

        reactive.observePatterns(FluxSink.OverflowStrategy.DROP)
                .doOnNext(this::countMessage)
                .subscribe();

        logCounters();
    }

    private void countMessage(PatternMessage<String, String> message) {
        String channel = message.getChannel();
        messages.compute(channel, (key, val) -> val == null ? 1 : val + 1);
    }

    private void logCounters() {
        while (true) {
            try {
                messages.forEach((channel, count) -> {
                    LOGGER.info("{} : [{}] : [{}]", id, channel, count);
                });
                Thread.sleep(TimeUnit.SECONDS.toMillis(COUNTERS_PRINT_SEC));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}
