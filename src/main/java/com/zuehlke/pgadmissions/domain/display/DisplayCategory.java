package com.zuehlke.pgadmissions.domain.display;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;

@Entity
@Table(name = "DISPLAY_CATEGORY")
public class DisplayCategory extends WorkflowDefinition {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismDisplayCategory id;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public final PrismDisplayCategory getId() {
        return id;
    }

    @Override
    public final void setId(Enum<?> id) {
        this.id = (PrismDisplayCategory) id;
    }

    @Override
    public final Scope getScope() {
        return scope;
    }

    @Override
    public final void setScope(Scope scope) {
        this.scope = scope;
    }

    public DisplayCategory withId(PrismDisplayCategory id) {
        this.id = id;
        return this;
    }

    public DisplayCategory withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
