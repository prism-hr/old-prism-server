package com.zuehlke.pgadmissions.domain.enums;

public enum DigestNotificationType {

    UPDATE_NOTIFICATION(1),
    
    TASK_NOTIFICATION(2),
    
    TASK_REMINDER(3),
    
    NONE(0);
    
    private final int priority;
    
    private String whatever;
    
    DigestNotificationType(final int priority) {
        this.priority = priority;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setWhatever(final String ss) {
        this.whatever = ss;
    }
    
    public String getWhatever() {
        return whatever;
    }
    
    public DigestNotificationType max(final DigestNotificationType type) {
        switch (Math.max(getPriority(), type.getPriority())) {
        case 0:
            return NONE;
        case 1:
            return UPDATE_NOTIFICATION;
        case 2:
            return TASK_NOTIFICATION;
        case 3:
            return TASK_REMINDER;
        default:
            return NONE;
        }
    }
}
