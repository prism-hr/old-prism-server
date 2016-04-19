package uk.co.alumeni.prism.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import uk.co.alumeni.prism.domain.definitions.PrismDomicile;

@Entity
@Table(name = "domicile")
public class Domicile extends Definition<PrismDomicile> {

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

}
