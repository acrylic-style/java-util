package xyz.acrylicstyle.util.serialization.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import xyz.acrylicstyle.util.serialization.codec.Codec;
import xyz.acrylicstyle.util.serialization.decoder.ByteBufValueDecoder;
import xyz.acrylicstyle.util.serialization.decoder.ByteBufferValueDecoder;
import xyz.acrylicstyle.util.serialization.decoder.ListValueDecoder;
import xyz.acrylicstyle.util.serialization.decoder.MapValueDecoder;
import xyz.acrylicstyle.util.serialization.encoder.ByteBufValueEncoder;
import xyz.acrylicstyle.util.serialization.encoder.ByteBufferValueEncoder;
import xyz.acrylicstyle.util.serialization.encoder.ListValueEncoder;
import xyz.acrylicstyle.util.serialization.encoder.MapValueEncoder;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

public class CodecTest {
    @Test
    public void byteBufferTest() {
        ByteBuffer buf = ByteBuffer.allocate(128);
        UUID uuid = UUID.randomUUID();
        PlayerData.CODEC.encode(new PlayerData(uuid, "CodecTest"), new ByteBufferValueEncoder(buf));
        buf.flip();
        PlayerData data = PlayerData.CODEC.decode(new ByteBufferValueDecoder(buf));
        assert data.uuid.equals(uuid) : "Expected: " + uuid + ", Actual: " + data.uuid;
        assert data.name.equals("CodecTest") : data.name;
        buf.flip();
        uuid = UUID.randomUUID();
        PlayerData.CODEC.encode(new PlayerData(uuid, null), new ByteBufferValueEncoder(buf));
        buf.flip();
        data = PlayerData.CODEC.decode(new ByteBufferValueDecoder(buf));
        assert data.uuid.equals(uuid) : "Expected: " + uuid + ", Actual: " + data.uuid;
        assert data.name == null : data.name;
    }

    @Test
    public void byteBufTest() {
        ByteBuf buf = Unpooled.buffer();
        UUID uuid = UUID.randomUUID();
        PlayerData.CODEC.encode(new PlayerData(uuid, "CodecTest"), new ByteBufValueEncoder(buf));
        PlayerData data = PlayerData.CODEC.decode(new ByteBufValueDecoder(buf));
        assert data.uuid.equals(uuid) : "Expected: " + uuid + ", Actual: " + data.uuid;
        assert data.name.equals("CodecTest") : data.name;
        buf.resetReaderIndex();
        buf.resetWriterIndex();
        uuid = UUID.randomUUID();
        PlayerData.CODEC.encode(new PlayerData(uuid, null), new ByteBufValueEncoder(buf));
        data = PlayerData.CODEC.decode(new ByteBufValueDecoder(buf));
        assert data.uuid.equals(uuid) : "Expected: " + uuid + ", Actual: " + data.uuid;
        assert data.name == null : data.name;
    }

    @Test
    public void mapTest() {
        MapValueEncoder encoder = new MapValueEncoder();
        UUID uuid = UUID.randomUUID();
        PlayerData.CODEC.encode(new PlayerData(uuid, "CodecTest"), encoder);
        PlayerData data = PlayerData.CODEC.decode(new MapValueDecoder(encoder.getCurrentMap()));
        assert data.uuid.equals(uuid) : "Expected: " + uuid + ", Actual: " + data.uuid;
        assert data.name.equals("CodecTest") : data.name;
    }

    @Test
    public void listTest() {
        ListValueEncoder encoder = new ListValueEncoder();
        UUID uuid = UUID.randomUUID();
        PlayerData.CODEC.encode(new PlayerData(uuid, "CodecTest"), encoder);
        PlayerData data = PlayerData.CODEC.decode(new ListValueDecoder(encoder.getList(), true));
        assert data.uuid.equals(uuid) : "Expected: " + uuid + ", Actual: " + data.uuid;
        assert data.name.equals("CodecTest") : data.name;
    }

    private static final class PlayerData {
        public static final Codec<PlayerData> CODEC =
                Codec.<PlayerData>builder()
                        .group(
                                Codec.UUID.fieldOf("uuid").getter(d -> d.uuid),
                                Codec.STRING.optionalFieldOf("name").getter(d -> Optional.ofNullable(d.name)),
                                Codec.STRING.optionalFieldOf("name2").getter(d -> Optional.ofNullable(d.name)),
                                Codec.UUID.fieldOf("uuid2").getter(d -> d.uuid)
                        )
                        .build((uuid1, s, s2, u2) -> new PlayerData(uuid1, s));

        private final UUID uuid;
        private final String name;

        public PlayerData(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        @Override
        public String toString() {
            return "PlayerData{" +
                    "uuid=" + uuid +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
