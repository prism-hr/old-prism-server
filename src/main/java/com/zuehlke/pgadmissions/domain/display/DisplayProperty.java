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

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;

@Entity
@Table(name = "DISPLAY_PROPERTY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DisplayProperty extends WorkflowDefinition {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismDisplayProperty id;

    @Column(name = "display_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismDisplayCategory displayCategory;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public Enum<?> getId() {
        return id;
    }

    @Override
    public void setId(Enum<?> id) {
        this.id = (PrismDisplayProperty) id;
    }

    public final PrismDisplayCategory getDisplayCategory() {
        return displayCategory;
    }

    public final void setDisplayCategory(PrismDisplayCategory displayCategory) {
        this.displayCategory = displayCategory;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public DisplayProperty withId(PrismDisplayProperty id) {
        this.id = id;
        return this;
    }
    
    public DisplayProperty withDisplayCategory(PrismDisplayCategory displayCategory) {
        this.displayCategory = displayCategory;
        return this;
    }
    
    public DisplayProperty withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
