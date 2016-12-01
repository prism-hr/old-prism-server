package uk.co.alumeni.prism.domain;

import uk.co.alumeni.prism.domain.definitions.PrismDomicile;

import javax.persistence.*;

import static org.apache.commons.lang3.ObjectUtils.compare;

@Entity
@Table(name = "domicile")
public class Domicile extends Definition<PrismDomicile> implements Comparable<Domicile> {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismDomicile id;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "ordinal", nullable = false, unique = true)
    private Integer ordinal;

    @Override
    public PrismDomicile getId() {
        return id;
    }

    @Override
    public void setId(PrismDomicile id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public Domicile withId(PrismDomicile id) {
        this.id = id;
        return this;
    }

    public Domicile withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public Domicile withOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
        return this;
    }

    @Override
    public int compareTo(Domicile other) {
        return compare(id, other.getId());
    }

}
