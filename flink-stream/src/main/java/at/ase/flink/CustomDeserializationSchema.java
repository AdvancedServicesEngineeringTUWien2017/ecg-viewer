package at.ase.flink;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CustomDeserializationSchema implements DeserializationSchema<DataEntriesWrapper> {

    private ObjectMapper objectMapper;

    public CustomDeserializationSchema() {
        this.objectMapper = new ObjectMapper();
    }

    public DataEntriesWrapper deserialize(byte[] bytes) throws IOException {
        String json = new String(bytes);
        DataEntriesWrapper wrapper = new DataEntriesWrapper();
        RawData data = parseJson(json);
        wrapper.setEntryList(createEntries(data));
        wrapper.setId(data.getId());
        wrapper.setSerial(data.getSerial());
        return wrapper;
    }

    public boolean isEndOfStream(DataEntriesWrapper dataEntries) {
        return false;
    }

    public TypeInformation<DataEntriesWrapper> getProducedType() {
        return TypeInformation.of(DataEntriesWrapper.class);
    }

    private RawData parseJson(String json) {
        RawData dataObject;
        try {
            dataObject = objectMapper.readValue(json, RawData.class);
            log.info(dataObject.toString());
        } catch (IOException e) {
            log.error("An error occurred while parsing the payload: ", e);
            dataObject = null;
        }
        return dataObject;
    }

    private List<DataEntry> createEntries(RawData rawData) {
        List<DataEntry> entries = new ArrayList<>();
        long i = 0;
        if (rawData.getSerial() == -1L) {
            //poison pill received
            return new ArrayList<>();
        }
        for (Long value : rawData.getLeadI()) {
            entries.add(new DataEntry(value, rawData.getSerial() * 1000 + i, rawData.getId()));
            i += 1000 / rawData.getLeadI().size();
        }
        return entries;
    }
}
