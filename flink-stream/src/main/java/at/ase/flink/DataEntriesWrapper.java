package at.ase.flink;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gena on 03/06/17.
 */
public class DataEntriesWrapper implements Serializable {

    private static final long serialVersionUID = 44365432422L;

    private List<DataEntry> entryList;

    private String id;

    private Long serial;

    public List<DataEntry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<DataEntry> entryList) {
        this.entryList = entryList;
    }

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
}
