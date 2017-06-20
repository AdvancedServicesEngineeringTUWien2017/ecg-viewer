package at.ase.webapp.service.dto;

import at.ase.webapp.domain.ECGData;
import at.ase.webapp.domain.ECGValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An ECGDataDto envelop.
 */
public class ECGDataDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private Map<Long, List<Long>> values;

    private Long date;

    private Double quality;

    public ECGDataDto(ECGData data) {
        this.id = data.getId();
        this.date = data.getDate();
        this.quality = data.getQuality();
        this.values = new HashMap<>();
        for (ECGValue value : data.getValues()) {
            this.values.put(Long.decode(value.getSerial()), value.getValues());
        }
    }

    public Double getQuality() {
        return quality;
    }

    public void setQuality(Double quality) {
        this.quality = quality;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<Long, List<Long>> getValues() {
        return values;
    }

    public void setValues(Map<Long, List<Long>> values) {
        this.values = values;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
