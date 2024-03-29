package uk.co.alumeni.prism.domain.workflow;

import com.google.common.collect.Sets;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "action")
public class Action extends WorkflowDefinition {

    @Id
    @Enumerated(EnumType.STRING)
    private PrismAction id;

    @Column(name = "system_invocation_only", nullable = false)
    private Boolean systemInvocationOnly;

    @Column(name = "action_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismActionCategory actionCategory;

    @Column(name = "action_condition")
    @Enumerated(EnumType.STRING)
    private PrismActionCondition actionCondition;

    @Column(name = "rating_action", nullable = false)
    private Boolean ratingAction;

    @Column(name = "transition_action")
    private Boolean transitionAction;

    @Column(name = "declinable_action", nullable = false)
    private Boolean declinableAction;

    @Column(name = "visible_action", nullable = false)
    private Boolean visibleAction;

    @Column(name = "document_circulation_action", nullable = false)
    private Boolean documentCirculationAction;

    @Column(name = "replicable_user_assignment_action", nullable = false)
    private Boolean replicableUserAssignmentAction;

    @Enumerated(EnumType.STRING)
    @Column(name = "partnership_state")
    private PrismPartnershipState partnershipState;

    @Enumerated(EnumType.STRING)
    @Column(name = "partnership_transition_state")
    private PrismPartnershipState partnershipTransitionState;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @ManyToOne
    @JoinColumn(name = "fallback_action_id")
    private Action fallbackAction;

    @ManyToOne
    @JoinColumn(name = "delegated_action_id")
    private Action delegatedAction;

    @ManyToOne
    @JoinColumn(name = "creation_scope_id")
    private Scope creationScope;

    @OneToMany(mappedBy = "action")
    private Set<ActionRedaction> redactions = Sets.newHashSet();

    @OneToMany(mappedBy = "action")
    private Set<StateAction> stateActions = Sets.newHashSet();

    @Override
    public PrismAction getId() {
        return id;
    }

    public void setId(PrismAction id) {
        this.id = id;
    }

    public Boolean getSystemInvocationOnly() {
        return systemInvocationOnly;
    }

    public void setSystemInvocationOnly(Boolean systemInvocationOnly) {
        this.systemInvocationOnly = systemInvocationOnly;
    }

    public PrismActionCategory getActionCategory() {
        return actionCategory;
    }

    public void setActionCategory(PrismActionCategory actionCategory) {
        this.actionCategory = actionCategory;
    }

    public PrismActionCondition getActionCondition() {
        return actionCondition;
    }

    public void setActionCondition(PrismActionCondition actionCondition) {
        this.actionCondition = actionCondition;
    }

    public Boolean getRatingAction() {
        return ratingAction;
    }

    public void setRatingAction(Boolean ratingAction) {
        this.ratingAction = ratingAction;
    }

    public Boolean getTransitionAction() {
        return transitionAction;
    }

    public void setTransitionAction(Boolean transitionAction) {
        this.transitionAction = transitionAction;
    }

    public Boolean getDeclinableAction() {
        return declinableAction;
    }

    public void setDeclinableAction(Boolean declinableAction) {
        this.declinableAction = declinableAction;
    }

    public Boolean getVisibleAction() {
        return visibleAction;
    }

    public void setVisibleAction(Boolean visibleAction) {
        this.visibleAction = visibleAction;
    }

    public Boolean getDocumentCirculationAction() {
        return documentCirculationAction;
    }

    public void setDocumentCirculationAction(Boolean documentCirculationAction) {
        this.documentCirculationAction = documentCirculationAction;
    }

    public Boolean getReplicableUserAssignmentAction() {
        return replicableUserAssignmentAction;
    }

    public void setReplicableUserAssignmentAction(Boolean replicableUserAssignmentAction) {
        this.replicableUserAssignmentAction = replicableUserAssignmentAction;
    }

    public PrismPartnershipState getPartnershipState() {
        return partnershipState;
    }

    public void setPartnershipState(PrismPartnershipState partnershipState) {
        this.partnershipState = partnershipState;
    }

    public PrismPartnershipState getPartnershipTransitionState() {
        return partnershipTransitionState;
    }

    public void setPartnershipTransitionState(PrismPartnershipState partnershipTransitionState) {
        this.partnershipTransitionState = partnershipTransitionState;
    }

    public Action getFallbackAction() {
        return fallbackAction;
    }

    public void setFallbackAction(Action fallbackAction) {
        this.fallbackAction = fallbackAction;
    }

    public Action getDelegatedAction() {
        return delegatedAction;
    }

    public void setDelegatedAction(Action delegatedAction) {
        this.delegatedAction = delegatedAction;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Scope getCreationScope() {
        return creationScope;
    }

    public void setCreationScope(Scope creationScope) {
        this.creationScope = creationScope;
    }

    public Set<ActionRedaction> getRedactions() {
        return redactions;
    }

    public void setRedactions(Set<ActionRedaction> redactions) {
        this.redactions = redactions;
    }

    public Set<StateAction> getStateActions() {
        return stateActions;
    }

    public void setStateActions(Set<StateAction> stateActions) {
        this.stateActions = stateActions;
    }

    public Action withId(PrismAction id) {
        this.id = id;
        return this;
    }

    public Action withSystemInvocationOnly(Boolean systemInvocationOnly) {
        this.systemInvocationOnly = systemInvocationOnly;
        return this;
    }

    public Action withActionCategory(PrismActionCategory actionCategory) {
        this.actionCategory = actionCategory;
        return this;
    }

    public Action withActionCondition(PrismActionCondition actionCondition) {
        this.actionCondition = actionCondition;
        return this;
    }

    public Action withRatingAction(Boolean ratingAction) {
        this.ratingAction = ratingAction;
        return this;
    }

    public Action withTransitionAction(Boolean transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }

    public Action withDeclinableAction(Boolean declinableAction) {
        this.declinableAction = declinableAction;
        return this;
    }

    public Action withVisibleAction(Boolean visibleAction) {
        this.visibleAction = visibleAction;
        return this;
    }

    public Action withDocumentCirculationAction(Boolean documentCirculationAction) {
        this.documentCirculationAction = documentCirculationAction;
        return this;
    }

    public Action withReplicableUserAssignmentAction(Boolean replicableUserAssignmentAction) {
        this.replicableUserAssignmentAction = replicableUserAssignmentAction;
        return this;
    }

    public Action withPartnershipState(PrismPartnershipState partnershipState) {
        this.partnershipState = partnershipState;
        return this;
    }

    public Action withPartnershipTransitionState(PrismPartnershipState partnershipTransitionState) {
        this.partnershipTransitionState = partnershipTransitionState;
        return this;
    }

    public Action withFallbackAction(Action fallbackAction) {
        this.fallbackAction = fallbackAction;
        return this;
    }

    public Action withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

    public Action withCreationScope(Scope creationScope) {
        this.creationScope = creationScope;
        return this;
    }

}
