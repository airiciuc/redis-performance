package pubsub.publisher;

import com.google.common.util.concurrent.RateLimiter;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisException;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pubsub.RedisClientBuilder;

import java.io.IOException;
import java.time.Instant;

public class Publisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);

    private static final String MSG_BODY = "0123456789";

    private final String channel;
    private final RedisClient client;
    private final StatsReporter statsReporter;

    private final RateLimiter rateLimiter;

    private volatile boolean running = false;

    public Publisher(String channel, double rate) throws IOException {
        this.channel = channel;
        this.client = RedisClientBuilder.build();
        this.statsReporter = new StatsReporter(channel);
        this.rateLimiter = RateLimiter.create(rate);
    }

    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown));

        subscribeToChannel();
        startPublishing();
    }

    private void subscribeToChannel() {
        StatefulRedisPubSubConnection<String, String> subscription = client.connectPubSub();
        subscription.sync().subscribe(channel);
        subscription.addListener(new RedisPubSubAdapter<String, String>() {
            @Override
            public void message(String channel, String message) {
                statsReporter.onMessageArrived(message);
            }
        });
    }

    private void startPublishing() {
        running = true;

        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        RedisPubSubAsyncCommands<String, String> async = connection.async();

        while (running) {
            sendMessage(async);
            rateLimiter.acquire();
        }
    }

    private void sendMessage(RedisPubSubAsyncCommands<String, String> async) {
        try {
            String message = createRandomMessage();
            async.publish(channel, message);
            statsReporter.onMessageSent(message);
        } catch (RedisException ex) {
            statsReporter.onError();
            LOGGER.error("failed to send message", ex);
        }
    }

    private String createRandomMessage() {
        return (Instant.now()) + " " + MSG_BODY;
    }

    private void shutDown() {
        running = false;
        LOGGER.info("total messages sent: {}", statsReporter.getTotalMessagesSent());
        LOGGER.info("total errors: {}", statsReporter.getTotalErrors());
        statsReporter.close();
    }
}
