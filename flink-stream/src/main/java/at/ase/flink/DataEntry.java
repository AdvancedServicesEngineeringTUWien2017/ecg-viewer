package at.ase.flink;

import java.io.Serializable;

public class DataEntry implements Serializable{

    private static final long serialVersionUID = 5435435332L;

    private long value;

    private long serialNumber;

    private String sensorId;

    public DataEntry(long value, long serialNumber, String sensorId){
        this.sensorId =sensorId;
        this.serialNumber = serialNumber;
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public String getSensorId() {
        return sensorId;
    }

    @Override
    public String toString() {
        return "DataEntry{" +
                "value=" + value +
                ", serialNumber=" + serialNumber +
                ", sensorId='" + sensorId + '\'' +
                '}';
    }
}
