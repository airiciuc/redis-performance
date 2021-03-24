package pubsub;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.metrics.MicrometerCommandLatencyRecorder;
import io.lettuce.core.metrics.MicrometerOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.Delay;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.time.Duration;
import java.util.Map;

public class RedisClientBuilder {

    public static final String CONFIG_YML = "redis.yml";
    public static final String HOST_PROP = "host";

    public static RedisClient build() {
        RedisClient redisClient = RedisClient.create(getClientResources(), "redis://" + getHost() + "/0");
        redisClient.setOptions(ClientOptions.builder()
                .suspendReconnectOnProtocolFailure(false)
                .pingBeforeActivateConnection(false)
                .build());
        return redisClient;
    }

    private static String getHost() {
        InputStream inputStream = RedisClientBuilder.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_YML);
        Map<String, Object> properties = new Yaml().load(inputStream);
        return (String) properties.get(HOST_PROP);
    }

    private static ClientResources getClientResources() {
        MeterRegistry registry = new JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM);
        MicrometerOptions options = MicrometerOptions.builder()
                .histogram(true)
                .localDistinction(true)
                .build();
        return ClientResources.builder()
                .reconnectDelay(Delay.constant(Duration.ofSeconds(1)))
                .commandLatencyRecorder(new MicrometerCommandLatencyRecorder(registry, options))
                .build();
    }
}
