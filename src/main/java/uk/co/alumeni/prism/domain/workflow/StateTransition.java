package uk.co.alumeni.prism.domain.workflow;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Sets;

import uk.co.alumeni.prism.domain.UniqueEntity;

@Entity
@Table(name = "state_transition", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_action_id", "transition_state_id" }) })
public class StateTransition implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_action_id", nullable = false)
    private StateAction stateAction;

    @ManyToOne
    @JoinColumn(name = "transition_state_id")
    private State transitionState;

    @ManyToOne
    @JoinColumn(name = "transition_action_id", nullable = false)
    private Action transitionAction;

    @ManyToOne
    @JoinColumn(name = "state_transition_evaluation_id")
    private StateTransitionEvaluation stateTransitionEvaluation;

    @OneToMany(mappedBy = "stateTransition")
    private Set<RoleTransition> roleTransitions = Sets.newHashSet();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "state_transition_propagation", joinColumns = { @JoinColumn(name = "state_transition_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "propagated_action_id", nullable = false) })
    private Set<Action> propagatedActions = Sets.newHashSet();

    @OneToMany(mappedBy = "stateTransition")
    private Set<StateTermination> stateTerminations = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StateAction getStateAction() {
        return stateAction;
    }

    public void setStateAction(StateAction stateAction) {
        this.stateAction = stateAction;
    }

    public State getTransitionState() {
        return transitionState;
    }

    public void setTransitionState(State transitionState) {
        this.transitionState = transitionState;
    }

    public Action getTransitionAction() {
        return transitionAction;
    }

    public void setTransitionAction(Action transitionAction) {
        this.transitionAction = transitionAction;
    }

    public final StateTransitionEvaluation getStateTransitionEvaluation() {
        return stateTransitionEvaluation;
    }

    public final void setStateTransitionEvaluation(StateTransitionEvaluation stateTransitionEvaluation) {
        this.stateTransitionEvaluation = stateTransitionEvaluation;
    }

    public Set<RoleTransition> getRoleTransitions() {
        return roleTransitions;
    }

    public Set<Action> getPropagatedActions() {
        return propagatedActions;
    }

    public final Set<StateTermination> getStateTerminations() {
        return stateTerminations;
    }

    public StateTransition withStateAction(StateAction stateAction) {
        this.stateAction = stateAction;
        return this;
    }

    public StateTransition withTransitionState(State transitionState) {
        this.transitionState = transitionState;
        return this;
    }

    public StateTransition withTransitionAction(Action transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }

    public StateTransition withStateTransitionEvaluation(StateTransitionEvaluation stateTransitionEvaluation) {
        this.stateTransitionEvaluation = stateTransitionEvaluation;
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("stateAction", stateAction).addProperty("transitionState", transitionState);
    }

}