package at.ase.webapp.domain;

import java.util.List;

/**
 * Created by gena on 18/06/17.
 */
public class ECGValue {

    private String serial;

    private List<Long> values;

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public List<Long> getValues() {
        return values;
    }

    public void setValues(List<Long> values) {
        this.values = values;
    }
}
