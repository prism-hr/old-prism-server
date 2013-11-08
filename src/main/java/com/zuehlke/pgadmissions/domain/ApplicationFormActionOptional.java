package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "APPLICATION_FORM_ACTION_OPTIONAL")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplicationFormActionOptional implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ApplicationFormActionOptionalId id;

    public ApplicationFormActionOptionalId getId() {
        return id;
    }

    public void setId(ApplicationFormActionOptionalId id) {
        this.id = id;
    }

}
