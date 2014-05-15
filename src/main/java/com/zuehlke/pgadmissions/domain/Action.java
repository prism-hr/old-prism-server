package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;

@Entity
@Table(name = "ACTION")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Action implements Serializable {

    private static final long serialVersionUID = 52046298022482941L;

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private ApplicationFormAction id;

    public ApplicationFormAction getId() {
        return id;
    }

    public void setId(ApplicationFormAction id) {
        this.id = id;
    }

    public Action withId(ApplicationFormAction id) {
        this.id = id;
        return this;
    }

}
