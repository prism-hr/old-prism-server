package com.zuehlke.pgadmissions.domain.helpers;

import java.util.Arrays;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class RegisteredUserTestHarness {

    private RegisteredUser registeredUser;
    private NotificationListTestScenario notificationListTestScenario;
    private List<ApplicationFormUserRole> applicationFormUserRoles;

    public RegisteredUserTestHarness(RegisteredUser registeredUser, NotificationListTestScenario notificationListTestScenario,
            ApplicationFormUserRole... applicationFormUserRole) {
        this.setRegisteredUser(registeredUser);
        this.setNotificationListTestScenario(notificationListTestScenario);
        this.setApplicationFormUserRoles(Arrays.asList(applicationFormUserRole));

    }

    public RegisteredUser getRegisteredUser() {
        return registeredUser;
    }

    public void setApplicationFormUserRoles(List<ApplicationFormUserRole> applicationFormUserRoles) {
        this.applicationFormUserRoles = applicationFormUserRoles;
    }

    public void setRegisteredUser(RegisteredUser registeredUser) {
        this.registeredUser = registeredUser;
    }

    public NotificationListTestScenario getNotificationListTestScenario() {
        return notificationListTestScenario;
    }

    public void setNotificationListTestScenario(NotificationListTestScenario notificationListTestScenario) {
        this.notificationListTestScenario = notificationListTestScenario;
    }

    public List<ApplicationFormUserRole> getApplicationFormUserRoles() {
        return applicationFormUserRoles;
    }

    public void setApplicationFormUserRoles(ApplicationFormUserRole... applicationFormUserRoles) {
        this.applicationFormUserRoles = Arrays.asList(applicationFormUserRoles);
    }

}