package pubsub.publisher;

import com.google.common.util.concurrent.RateLimiter;
import io.lettuce.core.RedisChannelHandler;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisConnectionStateListener;
import io.lettuce.core.RedisException;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pubsub.RedisClientBuilder;

import java.net.SocketAddress;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Publisher implements RedisConnectionStateListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);

    private static final String MSG_BODY = "CachePubSubEvent{nodeId='84af0b96-2eb6-4153-bc99-fc2733c56712', " +
            "cacheName='Query_f06ce81a-9b74-4c0a-9af8-ce08e7128453', cacheKey=null}";

    private static final int RATE_INCREMENT_SECONDS = 300;

    private final Random random = new Random();
    private final String channel;
    private final int channels;
    private final int initialRounds;
    private final double initialRateIncrement;
    private final double rateIncrement;
    private final RedisClient client;

    private final RateLimiter rateLimiter;

    public Publisher(String channel, int channels, int initialRounds, double initialRateIncrement,
            double rateIncrement) {
        this.channel = channel;
        this.channels = channels;
        this.initialRounds = initialRounds;
        this.initialRateIncrement = initialRateIncrement;
        this.rateIncrement = rateIncrement;
        this.client = RedisClientBuilder.build();
        this.rateLimiter = RateLimiter.create(initialRateIncrement);
    }

    public void run() {
        new Thread(this::startIncreasingRate).start();
        startPublishing();
    }

    private void startPublishing() {
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        RedisPubSubAsyncCommands<String, String> async = connection.async();
        client.addListener(this);

        while (true) {
            sendMessage(async);
            rateLimiter.acquire();
        }
    }

    private void sendMessage(RedisPubSubAsyncCommands<String, String> async) {
        try {
            async.publish(channel + random.nextInt(channels), MSG_BODY)
                    .exceptionally(err -> {
                        LOGGER.error("Failed to send message", err);
                        return 1L;
                    });
        } catch (RedisException ex) {
            LOGGER.error("failed to send message", ex);
        }
    }

    private void startIncreasingRate() {
        int rounds = 1;
        while (true) {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(RATE_INCREMENT_SECONDS));
                double increment = rounds < initialRounds ? initialRateIncrement : rateIncrement;
                rateLimiter.setRate(rateLimiter.getRate() + increment);
                ++rounds;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onRedisConnected(RedisChannelHandler<?, ?> connection, SocketAddress socketAddress) {
        LOGGER.info("Connected to {}", socketAddress);
    }

    @Override
    public void onRedisDisconnected(RedisChannelHandler<?, ?> connection) {
        LOGGER.info("Disconnected from redis");
    }

    @Override
    public void onRedisExceptionCaught(RedisChannelHandler<?, ?> connection, Throwable cause) {
        LOGGER.error("Exception occurred", cause);
    }
}
