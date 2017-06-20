package at.ase.webapp.service.dto;

import at.ase.webapp.domain.ECGValue;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gena on 18/06/17.
 */
public class ECGValueDto implements Serializable {

    private static final long serialVersionUID = 2424321241442L;

    private Long serial;

    private List<Long> values;

    public ECGValueDto(ECGValue value) {
        this.serial = Long.decode(value.getSerial());
        this.values = value.getValues();
    }

    public Long getSerial() {
        return serial;
    }

    public void setSerial(Long serial) {
        this.serial = serial;
    }

    public List<Long> getValues() {
        return values;
    }

    public void setValues(List<Long> values) {
        this.values = values;
    }
}
