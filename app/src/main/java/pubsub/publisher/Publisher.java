package pubsub.publisher;

import com.google.common.util.concurrent.RateLimiter;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisException;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pubsub.RedisClientBuilder;
import pubsub.StringByteCodec;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Publisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);

    private static final byte[] MSG_BODY = new byte[500];

    private static final int RATE_INCREMENT_SECONDS = 300;
    private static final int CONNECTIONS = 161;

    private final Random random = new Random();
    private final String channel;
    private final int channels;
    private final int initialRounds;
    private final double initialRateIncrement;
    private final double rateIncrement;
    private final RedisClient client;

    private final RateLimiter rateLimiter;

    public Publisher(String host, String channel, int channels, int initialRounds,
            double initialRateIncrement, double rateIncrement) {
        this.channel = channel;
        this.channels = channels;
        this.initialRounds = initialRounds;
        this.initialRateIncrement = initialRateIncrement;
        this.rateIncrement = rateIncrement;
        this.client = RedisClientBuilder.build(host);
        this.rateLimiter = RateLimiter.create(initialRateIncrement);
    }

    public void run() {
        new Thread(this::startIncreasingRate).start();

        startPublishing();
    }

    private void startPublishing() {
        RedisPubSubAsyncCommands<String, byte[]>[] asyncs = new RedisPubSubAsyncCommands[CONNECTIONS];
        for (int i = 0; i < CONNECTIONS; ++i) {
            asyncs[i] = client.connectPubSub(new StringByteCodec()).async();
        }

        while (true) {
            sendMessage(asyncs);
            rateLimiter.acquire();
        }
    }

    private void sendMessage(RedisPubSubAsyncCommands<String, byte[]>[] asyncs) {
        try {
            int connectionIdx = random.nextInt(CONNECTIONS);
            int channelIdx = random.nextInt(322);

            asyncs[connectionIdx].publish(channel + channelIdx, MSG_BODY)
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
            sleep(TimeUnit.SECONDS.toMillis(RATE_INCREMENT_SECONDS));
            double increment = rounds < initialRounds ? initialRateIncrement : rateIncrement;
            rateLimiter.setRate(rateLimiter.getRate() + increment);
            ++rounds;
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
        }
    }
}
