package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.definitions.PrismAgeRange;

@Entity
@Table(name = "age_range")
public class AgeRange extends Definition<PrismAgeRange> {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAgeRange id;

    @Column(name = "lower_bound", nullable = false, unique = true)
    private Integer lowerBound;

    @Column(name = "upper_bound", unique = true)
    private Integer upperBound;

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

}
