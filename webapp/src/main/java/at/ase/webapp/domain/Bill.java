package at.ase.webapp.domain;

/**
 * Created by gena on 19/06/17.
 */
public class Bill {

    public Bill(Long date, String id, Double quality) {
        this.date = date;
        this.id = id;
        this.quality = quality;
    }

    private Long date;

    private String id;

    private Double quality;

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getQuality() {
        return quality;
    }

    public void setQuality(Double quality) {
        this.quality = quality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bill bill = (Bill) o;

        return id != null ? id.equals(bill.id) : bill.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
