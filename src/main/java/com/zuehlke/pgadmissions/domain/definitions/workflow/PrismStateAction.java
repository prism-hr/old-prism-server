package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.List;

import com.google.common.collect.Lists;

public class PrismStateAction {
    
    private PrismAction action;
    
    private boolean raisesUrgentFlag;
    
    private boolean defaultAction;
    
    private boolean postComment;
    
    private PrismNotificationTemplate notificationTemplate;
    
    private List<PrismStateActionAssignment> assignments = Lists.newArrayList();
    
    private List<PrismStateActionNotification> notifications = Lists.newArrayList();
    
    private List<PrismStateTransition> transitions = Lists.newArrayList();

    public PrismAction getAction() {
        return action;
    }

    public boolean isRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public boolean isDefaultAction() {
        return defaultAction;
    }

    public boolean isPostComment() {
        return postComment;
    }

    public PrismNotificationTemplate getNotificationTemplate() {
        return notificationTemplate;
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
    
    public PrismStateAction withRaisesUrgentFlag(boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
        return this;
    }
    
    public PrismStateAction withDefaultAction(boolean defaultAction) {
        this.defaultAction = defaultAction;
        return this;
    }
    
    public PrismStateAction withPostComment(boolean postComment) {
        this.postComment = postComment;
        return this;
    }
    
    public PrismStateAction withNotificationTemplate(PrismNotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
        return this;
    }
    
    public PrismStateAction withAssignments(List<PrismStateActionAssignment> assignments) {
        this.assignments = assignments == null ? this.assignments : assignments;
        return this;
    }
    
    public PrismStateAction withNotifications(List<PrismStateActionNotification> notifications) {
        this.notifications = notifications == null ? this.notifications : notifications;
        return this;
    }
    
    public PrismStateAction withTransitions(List<PrismStateTransition> transitions) {
        this.transitions = transitions == null ? this.transitions : transitions;
        return this;
    }
    
}