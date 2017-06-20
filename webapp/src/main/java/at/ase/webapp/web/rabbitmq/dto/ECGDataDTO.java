package at.ase.webapp.web.rabbitmq.dto;

import java.io.Serializable;
import java.util.List;

public class ECGDataDTO implements Serializable{

    private static final long serialVersionUID = 42L;

    private String id;

    private Long serial;

    private List<Long> leadI;

    private Double heartRate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSerial() {
        return serial;
    }

    public void setSerial(Long serial) {
        this.serial = serial;
    }

    public List<Long> getLeadI() {
        return leadI;
    }

    public void setLeadI(List<Long> leadI) {
        this.leadI = leadI;
    }

    public Double getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Double heartRate) {
        this.heartRate = heartRate;
    }

    @Override
    public String toString() {
        return "ECGDataDTO{" +
            "id='" + id + '\'' +
            ", serial=" + serial +
            ", leadI=" + leadI +
            '}';
    }
}
