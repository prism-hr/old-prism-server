package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition;

import javax.persistence.*;

@Entity
@Table(name = "action_custom_question_definition")
public class ActionCustomQuestionDefinition extends WorkflowDefinition {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismActionCustomQuestionDefinition id;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    public final PrismActionCustomQuestionDefinition getId() {
        return id;
    }

    public final void setId(PrismActionCustomQuestionDefinition id) {
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

    public ActionCustomQuestionDefinition withId(PrismActionCustomQuestionDefinition id) {
        this.id = id;
        return this;
    }

    public ActionCustomQuestionDefinition withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
