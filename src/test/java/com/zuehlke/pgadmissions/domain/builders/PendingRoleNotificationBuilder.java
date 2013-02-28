package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;

public class PendingRoleNotificationBuilder {
	
	private Role role;
	private Program program;
	private RegisteredUser user;	
	private Integer id;
	private RegisteredUser addedByUser;
	private Date notificationDate;
	
	public PendingRoleNotificationBuilder notificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
        return this;
    }
	
	public PendingRoleNotificationBuilder addedByUser(RegisteredUser addedByUser) {
		this.addedByUser = addedByUser;
		return this;
	}
	
	public PendingRoleNotificationBuilder role(Role role) {
		this.role = role;
		return this;
	}
	
	
	public PendingRoleNotificationBuilder program(Program program) {
		this.program = program;
		return this;
	}
	
	public PendingRoleNotificationBuilder user(RegisteredUser user) {
		this.user = user;
		return this;
	}
	
	public PendingRoleNotificationBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public PendingRoleNotification build() {
		PendingRoleNotification pendingRoleNotification = new PendingRoleNotification();
		pendingRoleNotification.setId(id);
		pendingRoleNotification.setProgram(program);
		pendingRoleNotification.setRole(role);
		pendingRoleNotification.setUser(user);
		pendingRoleNotification.setAddedByUser(addedByUser);
		pendingRoleNotification.setNotificationDate(notificationDate);
		return pendingRoleNotification;
	}
}
