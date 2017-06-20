package at.ase.flink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.util.serialization.SerializationSchema;

@Slf4j
public class CustomHeartRateSerializationSchema implements SerializationSchema<HeartRateEntry> {

    private ObjectMapper objectMapper;

    public CustomHeartRateSerializationSchema() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public byte[] serialize(HeartRateEntry heartRateEntry) {
        try {
            return objectMapper.writeValueAsString(heartRateEntry).getBytes();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
