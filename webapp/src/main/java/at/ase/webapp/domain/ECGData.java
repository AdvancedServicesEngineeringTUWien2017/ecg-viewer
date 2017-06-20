package at.ase.webapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * An ECGDataDto envelop.
 */
@Document(collection = "sensordata")
public class ECGData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    private List<ECGValue> values;

    private Long date;

    private Double quality;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ECGValue> getValues() {
        return values;
    }

    public void setValues(List<ECGValue> values) {
        this.values = values;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Double getQuality() {
        return quality;
    }

    public void setQuality(Double quality) {
        this.quality = quality;
    }
}
