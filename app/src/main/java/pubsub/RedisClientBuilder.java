package pubsub;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.resource.ClientResources;

public class RedisClientBuilder {

    public static RedisClient build(String host) {
        RedisClient redisClient = RedisClient.create(getClientResources(), "redis://" + host + "/0");
        redisClient.setOptions(ClientOptions.builder()
//                .timeoutOptions(TimeoutOptions.enabled(Duration.ofMillis(1000)))
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .build());
        return redisClient;
    }

    private static ClientResources getClientResources() {
        return ClientResources.builder()
                .build();
    }
}
