package pubsub.subscriber;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import pubsub.RedisClientBuilder;

import java.util.concurrent.ConcurrentHashMap;

public class Subscriber {

    private final String pattern;
    private final int channels;
    private final RedisClient client;


    private final ConcurrentHashMap<String, Integer> messages = new ConcurrentHashMap<>();

    public Subscriber(String host, String pattern, int channels) {
        this.pattern = pattern;
        this.channels = channels;
        this.client = RedisClientBuilder.build(host);
    }

    public void run() {
        for (int i = 0; i < channels; ++i) {
            createConnection(i);
        }
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
}
