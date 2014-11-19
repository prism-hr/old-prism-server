package com.zuehlke.pgadmissions.domain.display;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;

@Entity
@Table(name = "DISPLAY_PROPERTY_DEFINITION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DisplayPropertyDefinition extends WorkflowDefinition {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismDisplayProperty id;

    @Column(name = "display_property_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismDisplayPropertyCategory displayPropertyCategory;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public PrismDisplayProperty getId() {
        return id;
    }

    public void setId(PrismDisplayProperty id) {
        this.id = id;
    }

    public final PrismDisplayPropertyCategory getDisplayPropertyCategory() {
        return displayPropertyCategory;
    }

    public final void setDisplayPropertyCategory(PrismDisplayPropertyCategory displayPropertyCategory) {
        this.displayPropertyCategory = displayPropertyCategory;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public DisplayPropertyDefinition withId(PrismDisplayProperty id) {
        this.id = id;
        return this;
    }

    public DisplayPropertyDefinition withDisplayPropertyCategory(PrismDisplayPropertyCategory displayPropertyCategory) {
        this.displayPropertyCategory = displayPropertyCategory;
        return this;
    }

    public DisplayPropertyDefinition withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
