package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Service
@Transactional
public class RegistrationService {
	
	private final UserService userService;
	private final RefereeService refereeService;
	
	public RegistrationService() {
		this(null, null);
	}

	@Autowired
	public RegistrationService (UserService userService, RefereeService refereeService) {
		this.userService = userService;
		this.refereeService = refereeService;
	}

	public RegisteredUser updateOrSaveUser(RegisteredUser pendingUser, String queryString) {
		return userService.enableRegisteredUser(pendingUser, queryString);
	}
	
	public void sendInstructionsToRegisterIfActivationCodeIsMissing(final RegisteredUser user) {
		Referee referee = refereeService.getRefereeByUser(user);
		if (!user.getPendingRoleNotifications().isEmpty()) {
			for (PendingRoleNotification notification : user.getPendingRoleNotifications()) {
				notification.setNotificationDate(null);
			}
			userService.save(user);
		} else if (referee != null) {
			referee.setLastNotified(null);
			refereeService.save(referee);
		}
	}

}