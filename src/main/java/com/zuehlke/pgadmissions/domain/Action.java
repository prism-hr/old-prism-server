package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.PrismAction;

@Entity
@Table(name = "ACTION")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Action {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAction id;

    public PrismAction getId() {
        return id;
    }

    public void setId(PrismAction id) {
        this.id = id;
    }

    public Action withId(PrismAction id) {
        this.id = id;
        return this;
    }

}
