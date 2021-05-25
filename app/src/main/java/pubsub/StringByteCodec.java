package pubsub;

import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;

import java.nio.ByteBuffer;

public class StringByteCodec implements RedisCodec<String, byte[]> {

    private final RedisCodec<String, String> keyCodec = new StringCodec();
    private final RedisCodec<byte[], byte[]> valueCodec = new ByteArrayCodec();

    @Override
    public String decodeKey(final ByteBuffer byteBuffer) {
        return keyCodec.decodeKey(byteBuffer);
    }

    @Override
    public byte[] decodeValue(final ByteBuffer byteBuffer) {
        return valueCodec.decodeValue(byteBuffer);
    }

    @Override
    public ByteBuffer encodeKey(final String value) {
        return keyCodec.encodeKey(value);
    }

    @Override
    public ByteBuffer encodeValue(final byte[] bytes) {
        return valueCodec.encodeValue(bytes);
    }
}
