package xyz.acrylicstyle.util.serialization.test;

import org.junit.jupiter.api.Test;
import xyz.acrylicstyle.util.serialization.codec.Codec;
import xyz.acrylicstyle.util.serialization.decoder.ByteBufferValueDecoder;
import xyz.acrylicstyle.util.serialization.decoder.ListValueDecoder;
import xyz.acrylicstyle.util.serialization.encoder.ByteBufferValueEncoder;
import xyz.acrylicstyle.util.serialization.encoder.ListValueEncoder;
import xyz.acrylicstyle.util.serialization.encoder.MapValueEncoder;

import java.nio.ByteBuffer;
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
    }

    @Test
    public void mapTest() {
        MapValueEncoder encoder = new MapValueEncoder();
        encoder.push("l");
        Codec.LONG.encode(100L, encoder);
        encoder.pop();
//        System.out.println(new Yaml().dumpAsMap(encoder.getCurrentMap()));
        encoder.getCurrentMap().clear();
        UUID uuid = UUID.randomUUID();
        PlayerData.CODEC.encode(new PlayerData(uuid, "CodecTest"), encoder);
//        System.out.println(new Yaml().dumpAsMap(encoder.getCurrentMap()));
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
                        .group(Codec.UUID.fieldOf("uuid").getter(d -> d.uuid), Codec.STRING.fieldOf("name").getter(d -> d.name))
                        .build(PlayerData::new);

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
