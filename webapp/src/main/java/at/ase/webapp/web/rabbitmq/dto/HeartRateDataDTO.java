package at.ase.webapp.web.rabbitmq.dto;

import java.io.Serializable;

public class HeartRateDataDTO implements Serializable {

    private static final long serialVersionUID = 42L;

    private Double value;

    private String sensorId;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public String toString() {
        return "HeartRateDataDTO{" +
            "value=" + value +
            ", sensorId='" + sensorId + '\'' +
            '}';
    }
}
