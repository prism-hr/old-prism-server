package com.zuehlke.pgadmissions.domain.workflow;

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
import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.resource.ResourceBatchProcess;

@Entity
@Table(name = "STATE_TRANSITION", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_action_id", "transition_state_id" }) })
public class StateTransition implements IUniqueEntity {

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
	
	@ManyToOne
	@JoinColumn(name = "resource_batch_process_join_id")
	private ResourceBatchProcess resourceBatchProcessJoin;

	@ManyToOne
	@JoinColumn(name = "resource_batch_process_exit_id")
	private ResourceBatchProcess resourceBatchProcessExit;
	
	@OneToMany(mappedBy = "stateTransition")
	private Set<RoleTransition> roleTransitions = Sets.newHashSet();

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "STATE_TRANSITION_PROPAGATION", joinColumns = { @JoinColumn(name = "state_transition_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "propagated_action_id", nullable = false) })
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

	public StateTransitionEvaluation getStateTransitionEvaluation() {
		return stateTransitionEvaluation;
	}

	public void setStateTransitionEvaluation(StateTransitionEvaluation stateTransitionEvaluation) {
		this.stateTransitionEvaluation = stateTransitionEvaluation;
	}

	public ResourceBatchProcess getResourceBatchProcessJoin() {
		return resourceBatchProcessJoin;
	}

	public void setResourceBatchProcessJoin(ResourceBatchProcess resourceBatchProcessJoin) {
		this.resourceBatchProcessJoin = resourceBatchProcessJoin;
	}

	public ResourceBatchProcess getResourceBatchProcessExit() {
		return resourceBatchProcessExit;
	}

	public void setResourceBatchProcessExit(ResourceBatchProcess resourceBatchProcessExit) {
		this.resourceBatchProcessExit = resourceBatchProcessExit;
	}

	public Set<RoleTransition> getRoleTransitions() {
		return roleTransitions;
	}

	public Set<Action> getPropagatedActions() {
		return propagatedActions;
	}

	public Set<StateTermination> getStateTerminations() {
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
	
	public StateTransition withResourceBatchProcessJoin(ResourceBatchProcess resourceBatchProcessJoin) {
		this.resourceBatchProcessJoin = resourceBatchProcessJoin;
		return this;
	}
	
	public StateTransition withResourceBatchProcessExit(ResourceBatchProcess resourceBatchProcessExit) {
		this.resourceBatchProcessExit = resourceBatchProcessExit;
		return this;
	}

	public boolean isResourceBatchProcessJoin() {
		return resourceBatchProcessJoin != null;
	}
	
	public boolean isResourceBatchProcessExit() {
		return resourceBatchProcessExit != null;
	}
	
	public boolean hasPropagatedActions() {
		return propagatedActions.size() > 0;
	}

	@Override
	public ResourceSignature getResourceSignature() {
		return new ResourceSignature().addProperty("stateAction", stateAction).addProperty("transitionState", transitionState);
	}

}
