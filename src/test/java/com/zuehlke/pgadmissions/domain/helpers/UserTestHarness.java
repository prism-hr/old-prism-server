package com.zuehlke.pgadmissions.domain.helpers;

import java.util.Arrays;
import java.util.List;

import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.User;

public class UserTestHarness {

    private User registeredUser;
    private NotificationListTestScenario notificationListTestScenario;
    private List<UserRole> applicationFormUserRoles;

    public UserTestHarness(User registeredUser, NotificationListTestScenario notificationListTestScenario,
            UserRole... applicationFormUserRole) {
        this.setRegisteredUser(registeredUser);
        this.setNotificationListTestScenario(notificationListTestScenario);
        this.setUserRoles(Arrays.asList(applicationFormUserRole));

    }

    public User getRegisteredUser() {
        return registeredUser;
    }

    public void setUserRoles(List<UserRole> applicationFormUserRoles) {
        this.applicationFormUserRoles = applicationFormUserRoles;
    }

    public void setRegisteredUser(User registeredUser) {
        this.registeredUser = registeredUser;
    }

    public NotificationListTestScenario getNotificationListTestScenario() {
        return notificationListTestScenario;
    }

    public void setNotificationListTestScenario(NotificationListTestScenario notificationListTestScenario) {
        this.notificationListTestScenario = notificationListTestScenario;
    }

    public List<UserRole> getUserRoles() {
        return applicationFormUserRoles;
    }

    public void setUserRoles(UserRole... applicationFormUserRoles) {
        this.applicationFormUserRoles = Arrays.asList(applicationFormUserRoles);
    }

}