package at.ac.tuwien.dsg.sensor;

import java.util.List;

public class GenericDataInstance {

    private String sensorId;
    private long serial;
    private List<? extends IRecord> records;

    public GenericDataInstance(String id, List<? extends IRecord> records, long serial) {
        this.sensorId = id;
        this.records = records;
        this.serial = serial;

    }

    public void setID(String id) {
        this.sensorId = id;
    }

    public String getJSON() {

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":\"" + this.sensorId + "\",");
        json.append("\"serial\":" + this.serial + ",");

        for (IRecord r : this.records) {
            json.append("\"" + r.getKeyAsJson() + "\":" + r.getValueAsJson() + ",");

        }
        json.deleteCharAt(json.length() - 1);
        json.append("}");

        return json.toString();
    }

    public interface IRecord {
        String getKeyAsJson();

        String getValueAsJson();
    }

    public static class Record implements IRecord {

        private String key;
        private String value;

        public Record(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public String getKeyAsJson() {
            return this.getKey();
        }

        public String getValueAsJson() {
            return this.getValue();
        }
    }

    public static class ListRecord implements IRecord {

        private String key;
        private List<String> value;

        public ListRecord(String key, List<String> value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public List<String> getValue() {
            return this.value;
        }

        public String getKeyAsJson() {
            return this.getKey();
        }

        public String getValueAsJson() {
            StringBuilder builder = new StringBuilder("[");
            for (String x : value) {
                builder.append(x);
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("]");
            return builder.toString();
        }
    }

    public static void main(String[] args) {
        System.out.println("test");
    }

}
