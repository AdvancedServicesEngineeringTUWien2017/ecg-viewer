package at.ase.flink;

import java.io.Serializable;

public class HeartRateEntry implements Serializable{

    private static final long serialVersionUID = 5435435332L;

    private Double value;

    private String sensorId;

    public HeartRateEntry(Double value, String sensorId){
        this.sensorId =sensorId;
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public String getSensorId() {
        return sensorId;
    }

    @Override
    public String toString() {
        return "HeartRateEntry{" +
                "value=" + value +
                ", sensorId='" + sensorId + '\'' +
                '}';
    }
}
