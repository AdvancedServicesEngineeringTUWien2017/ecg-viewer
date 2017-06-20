package at.ase.flink;

/**
 * Created by gena on 18/06/17.
 */
public class QualityEntity {
    private long previous;
    private long missed;
    private long all;
    private double percentage;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPrevious() {
        return previous;
    }

    public void setPrevious(long previous) {
        this.previous = previous;
    }

    public long getMissed() {
        return missed;
    }

    public void setMissed(long missed) {
        this.missed = missed;
    }

    public long getAll() {
        return all;
    }

    public void setAll(long all) {
        this.all = all;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "QualityEntity{" +
                "previous=" + previous +
                ", missed=" + missed +
                ", all=" + all +
                ", percentage=" + percentage +
                ", id='" + id + '\'' +
                '}';
    }
}
