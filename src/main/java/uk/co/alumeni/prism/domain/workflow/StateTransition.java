package uk.co.alumeni.prism.domain.workflow;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import uk.co.alumeni.prism.domain.UniqueEntity;

import com.google.common.collect.Sets;

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

    @Column(name = "replicable_sequence_close", nullable = false)
    private Boolean replicableSequenceClose;

    @Column(name = "replicable_sequence_filter_theme", nullable = false)
    private Boolean replicableSequenceFilterTheme;

    @Column(name = "replicable_sequence_filter_secondary_theme", nullable = false)
    private Boolean replicableSequenceFilterSecondaryTheme;

    @Column(name = "replicable_sequence_filter_location", nullable = false)
    private Boolean replicableSequenceFilterLocation;

    @Column(name = "replicable_sequence_filter_secondary_location", nullable = false)
    private Boolean replicableSequenceFilterSecondaryLocation;

    @ManyToOne
    @JoinColumn(name = "state_transition_evaluation_id")
    private StateTransitionEvaluation stateTransitionEvaluation;

    @OneToMany(mappedBy = "stateTransition")
    private Set<RoleTransition> roleTransitions = Sets.newHashSet();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "state_transition_propagation", joinColumns = { @JoinColumn(name = "state_transition_id", nullable = false) }, inverseJoinColumns = {
            @JoinColumn(name = "propagated_action_id", nullable = false) })
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

    public Boolean getReplicableSequenceClose() {
        return replicableSequenceClose;
    }

    public void setReplicableSequenceClose(Boolean replicableSequenceClose) {
        this.replicableSequenceClose = replicableSequenceClose;
    }

    public Boolean getReplicableSequenceFilterTheme() {
        return replicableSequenceFilterTheme;
    }

    public void setReplicableSequenceFilterTheme(Boolean replicableSequenceFilterTheme) {
        this.replicableSequenceFilterTheme = replicableSequenceFilterTheme;
    }

    public Boolean getReplicableSequenceFilterSecondaryTheme() {
        return replicableSequenceFilterSecondaryTheme;
    }

    public void setReplicableSequenceFilterSecondaryTheme(Boolean replicableSequenceFilterSecondaryTheme) {
        this.replicableSequenceFilterSecondaryTheme = replicableSequenceFilterSecondaryTheme;
    }

    public Boolean getReplicableSequenceFilterLocation() {
        return replicableSequenceFilterLocation;
    }

    public void setReplicableSequenceFilterLocation(Boolean replicableSequenceFilterLocation) {
        this.replicableSequenceFilterLocation = replicableSequenceFilterLocation;
    }

    public Boolean getReplicableSequenceFilterSecondaryLocation() {
        return replicableSequenceFilterSecondaryLocation;
    }

    public void setReplicableSequenceFilterSecondaryLocation(Boolean replicableSequenceFilterSecondaryLocation) {
        this.replicableSequenceFilterSecondaryLocation = replicableSequenceFilterSecondaryLocation;
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

    public StateTransition withReplicableSequenceClose(Boolean replicableSequenceClose) {
        this.replicableSequenceClose = replicableSequenceClose;
        return this;
    }

    public StateTransition withReplicableSequenceFilterTheme(Boolean replicableSequenceFilterTheme) {
        this.replicableSequenceFilterTheme = replicableSequenceFilterTheme;
        return this;
    }

    public StateTransition withReplicableSequenceFilterSecondaryTheme(Boolean replicableSequenceFilterSecondaryTheme) {
        this.replicableSequenceFilterSecondaryTheme = replicableSequenceFilterSecondaryTheme;
        return this;
    }

    public StateTransition withReplicableSequenceFilterLocation(Boolean replicableSequenceFilterLocation) {
        this.replicableSequenceFilterLocation = replicableSequenceFilterLocation;
        return this;
    }

    public StateTransition withReplicableSequenceFilterSecondaryLocation(Boolean replicableSequenceFilterSecondaryLocation) {
        this.replicableSequenceFilterSecondaryLocation = replicableSequenceFilterSecondaryLocation;
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
