package uk.co.alumeni.prism.domain;

import uk.co.alumeni.prism.domain.definitions.PrismAgeRange;

import javax.persistence.*;

@Entity
@Table(name = "age_range")
public class AgeRange extends Definition<PrismAgeRange> {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAgeRange id;

    @Column(name = "lower_bound", unique = true)
    private Integer lowerBound;

    @Column(name = "upper_bound", unique = true)
    private Integer upperBound;

    @Column(name = "ordinal", nullable = false, unique = true)
    private Integer ordinal;

    public PrismAgeRange getId() {
        return id;
    }

    public void setId(PrismAgeRange id) {
        this.id = id;
    }

    public Integer getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(Integer lowerBound) {
        this.lowerBound = lowerBound;
    }

    public Integer getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(Integer upperBound) {
        this.upperBound = upperBound;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public AgeRange withId(PrismAgeRange id) {
        this.id = id;
        return this;
    }

    public AgeRange withLowerBound(Integer lowerBound) {
        this.lowerBound = lowerBound;
        return this;
    }

    public AgeRange withUpperBound(Integer upperBound) {
        this.upperBound = upperBound;
        return this;
    }

    public AgeRange withOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
        return this;
    }

}
