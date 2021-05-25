package pubsub;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.resource.ClientResources;

import java.time.Duration;

public class RedisClientBuilder {

    public static RedisClusterClient build(String host) {
        RedisClusterClient redisClient = RedisClusterClient.create(getClientResources(), "redis://" + host + "/0");
        redisClient.setOptions(ClusterClientOptions.builder()
//                .timeoutOptions(TimeoutOptions.enabled(Duration.ofMillis(1000)))
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .topologyRefreshOptions(ClusterTopologyRefreshOptions.builder()
                        .enablePeriodicRefresh(Duration.ofMillis(500))
                        .enableAllAdaptiveRefreshTriggers()
                        .build())
                .build());
        return redisClient;
    }

    private static ClientResources getClientResources() {
        return ClientResources.builder()
                .build();
    }
}
