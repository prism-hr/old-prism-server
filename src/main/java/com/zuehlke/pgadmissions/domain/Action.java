package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.NotificationMethod;

@Entity(name = "ACTION")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Action implements Serializable {

    private static final long serialVersionUID = 52046298022482941L;

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private ApplicationFormAction id;
    
    @Column(name = "action_type_id")
    @Enumerated(EnumType.STRING)
    private ActionType actionType;
    
    @Column(name = "precedence")
    private Integer precedence = 0;
    
    @Column(name = "notification_method_id")
    @Enumerated(EnumType.STRING)
    private NotificationMethod notificationMethod = null;
    
    public ApplicationFormAction getId() {
        return id;
    }

    public void setId(ApplicationFormAction id) {
        this.id = id;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Integer getPrecedence() {
        return precedence;
    }

    public void setPrecedence(Integer precedence) {
        this.precedence = precedence;
    }

    public NotificationMethod getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(NotificationMethod notification) {
        this.notificationMethod = notification;
    }

}
