package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

public class PrismStateAction {

	private PrismAction action;

	private Boolean raisesUrgentFlag = false;

	private PrismActionEnhancement actionEnhancement;

	private PrismNotificationDefinition notification;

	private List<PrismStateActionAssignment> assignments = Lists.newLinkedList();

	private List<PrismStateActionNotification> notifications = Lists.newLinkedList();

	private List<PrismStateTransition> transitions = Lists.newLinkedList();

	public PrismAction getAction() {
		return action;
	}

	public boolean isRaisesUrgentFlag() {
		return raisesUrgentFlag;
	}

	public PrismActionEnhancement getActionEnhancement() {
		return actionEnhancement;
	}

	public PrismNotificationDefinition getNotification() {
		return notification;
	}

	public List<PrismStateActionAssignment> getAssignments() {
		return assignments;
	}

	public List<PrismStateActionNotification> getNotifications() {
		return notifications;
	}

	public List<PrismStateTransition> getTransitions() {
		return transitions;
	}

	public PrismStateAction withAction(PrismAction action) {
		this.action = action;
		return this;
	}

	public PrismStateAction withRaisesUrgentFlag() {
		this.raisesUrgentFlag = true;
		return this;
	}

	public PrismStateAction withActionEnhancement(PrismActionEnhancement actionEnhancement) {
		this.actionEnhancement = actionEnhancement;
		return this;
	}

	public PrismStateAction withNotification(PrismNotificationDefinition notification) {
		this.notification = notification;
		return this;
	}

	public PrismStateAction withAssignments(PrismStateActionAssignment... assignments) {
		this.assignments.addAll(Arrays.asList(assignments));
		return this;
	}
	
	public PrismStateAction withAssignments(PrismRole... roles) {
		List<PrismStateActionAssignment> assignments = Lists.newLinkedList();
		for (PrismRole role : roles) {
			assignments.add(new PrismStateActionAssignment().withRole(role));
		}
		this.assignments.addAll(assignments);
		return this;
	}

	public PrismStateAction withAssignments(PrismRoleGroup roleGroup) {
		List<PrismStateActionAssignment> assignments = Lists.newLinkedList();
		for (PrismRole role : roleGroup.getRoles()) {
			assignments.add(new PrismStateActionAssignment().withRole(role));
		}
		this.assignments.addAll(assignments);
		return this;
	}

	public PrismStateAction withNotifications(PrismStateActionNotification... notifications) {
		this.notifications.addAll(Arrays.asList(notifications));
		return this;
	}

	public PrismStateAction withNotifications(PrismRoleGroup roleGroup, PrismNotificationDefinition notification) {
		List<PrismStateActionNotification> notifications = Lists.newLinkedList();
		for (PrismRole role : roleGroup.getRoles()) {
			notifications.add(new PrismStateActionNotification().withRole(role).withDefinition(notification));
		}
		this.notifications.addAll(notifications);
		return this;
	}

	public PrismStateAction withTransitions(PrismStateTransition... transitions) {
		this.transitions.addAll(Arrays.asList(transitions));
		return this;
	}

	public PrismStateAction withTransitions(PrismStateTransitionGroup stateTransitionGroup) {
		List<PrismStateTransition> transitions = Lists.newLinkedList();
		for (PrismStateTransition stateTransition : stateTransitionGroup.getStateTransitions()) {
			transitions.add(stateTransition);
		}
		this.transitions.addAll(transitions);
		return this;
	}

}
