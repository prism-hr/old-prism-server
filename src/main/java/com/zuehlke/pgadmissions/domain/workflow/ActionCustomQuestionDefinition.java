package com.zuehlke.pgadmissions.domain.workflow;

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

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestion;

@Entity
@Table(name = "ACTION_CUSTOM_QUESTION_DEFINITION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ActionCustomQuestionDefinition extends WorkflowDefinition {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismActionCustomQuestion id;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    public final PrismActionCustomQuestion getId() {
        return id;
    }

    public final void setId(PrismActionCustomQuestion id) {
        this.id = id;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public ActionCustomQuestionDefinition withId(PrismActionCustomQuestion id) {
        this.id = id;
        return this;
    }

    public ActionCustomQuestionDefinition withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
