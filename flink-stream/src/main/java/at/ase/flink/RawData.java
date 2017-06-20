package at.ase.flink;

import java.util.List;

public class RawData {

    private String id;

    private Long serial;

    private List<Long> leadI;

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

    @Override
    public String toString() {
        return "RawData{" +
                "id='" + id + '\'' +
                ", serial=" + serial +
                ", leadI=" + leadI +
                '}';
    }
}
