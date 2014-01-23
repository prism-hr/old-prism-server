package com.zuehlke.pgadmissions.domain.builders;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.time.DateUtils;

import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormActionRequired;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.NotificationMethod;
import com.zuehlke.pgadmissions.domain.helpers.NotificationListTestCase;
import com.zuehlke.pgadmissions.domain.helpers.NotificationListTestScenario;
import com.zuehlke.pgadmissions.domain.helpers.RegisteredUserTestHarness;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class UserNotificationListBuilder {

    private int taskReminderSuccessCount = 0;
    private int taskNotificationSuccessCount = 0;
    private int updateNotificationSuccessCount = 0;

    private final Date baselineDate;
    private final UserDAO userDAO;
    private final ApplicationFormDAO applicationFormDAO;
    private final RoleDAO roleDAO;
    private final ActionDAO actionDAO;
    private final ApplicationFormUserRoleDAO applicationFormUserRoleDAO;
    private final EncryptionUtils encryptionUtils;

    private final Date updateBaselineDate;
    private final Date reminderBaselineDate;
    private final Date expiryBaselineDate;

    public UserNotificationListBuilder(Date baselineDate, int reminderIntervalInDays, int expiryIntervalInDays, UserDAO userDAO,
            ApplicationFormDAO applicationFormDAO, RoleDAO roleDAO, ActionDAO actionDAO, ApplicationFormUserRoleDAO applicationFormUserRoleDAO) {
        this.baselineDate = baselineDate;
        this.userDAO = userDAO;
        this.applicationFormDAO = applicationFormDAO;
        this.roleDAO = roleDAO;
        this.actionDAO = actionDAO;
        this.applicationFormUserRoleDAO = applicationFormUserRoleDAO;
        this.encryptionUtils = new EncryptionUtils();
        this.updateBaselineDate = DateUtils.addDays((Date) baselineDate.clone(), -1);
        this.reminderBaselineDate = DateUtils.addDays((Date) baselineDate.clone(), -reminderIntervalInDays);
        this.expiryBaselineDate = DateUtils.addDays((Date) baselineDate.clone(), -expiryIntervalInDays);
    }

    public HashMap<RegisteredUser, RegisteredUserTestHarness> builtTestInstances() {
        HashMap<RegisteredUser, RegisteredUserTestHarness> testInstances = new HashMap<RegisteredUser, RegisteredUserTestHarness>();
        for (NotificationListTestScenario testScenario : NotificationListTestScenario.values()) {
            switch (testScenario) {
                case TASKREMINDERSUCCESS:
                    for (NotificationListTestCase testCase : testScenario.getDisplayValue()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatRequiresTaskReminder(testScenario, testCase);
                        testInstances.put(userHarness.getRegisteredUser(), userHarness);
                        taskReminderSuccessCount++;
                    }
                    break;
                case TASKREMINDERFAILURE:
//                    for (NotificationListTestCase testCase : testScenario.getDisplayValue()) {
//                        RegisteredUserTestHarness userHarness = buildInstanceThatDoesNotRequireTaskReminder(testScenario, testCase);
//                        testInstances.put(userHarness.getRegisteredUser(), userHarness);
//                    }
                    break;
                case TASKNOTIFICATIONSUCCESS:
                    for (NotificationListTestCase testCase : testScenario.getDisplayValue()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatRequiresTaskNotification(testScenario, testCase);
                        testInstances.put(userHarness.getRegisteredUser(), userHarness);
                        taskNotificationSuccessCount++;
                    }
                    break;
                case TASKNOTIFICATIONFAILURE:
//                    for (NotificationListTestCase testCase : testScenario.getDisplayValue()) {
//                        RegisteredUserTestHarness userHarness = buildInstanceThatDoesNotRequireTaskNotification(testScenario, testCase);
//                        testInstances.put(userHarness.getRegisteredUser(), userHarness);
//                    }
                    break;
                case UPDATENOTIFICATIONSUCCESS:
//                    for (NotificationListTestCase testCase : testScenario.getDisplayValue()) {
//                        RegisteredUserTestHarness userHarness = buildInstanceThatRequiresUpdateNotification(testScenario, testCase);
//                        testInstances.put(userHarness.getRegisteredUser(), userHarness);
//                        updateNotificationSuccessCount++;
//                    }
                    break;
                case UPDATENOTIFICATIONFAILURE:
//                    for (NotificationListTestCase testCase : testScenario.getDisplayValue()) {
//                        RegisteredUserTestHarness userHarness = buildInstanceThatDoesNotRequireUpdateNotification(testScenario, testCase);
//                        testInstances.put(userHarness.getRegisteredUser(), userHarness);
//                    }
                    break;
            }
        }
        return testInstances;
    }

    public int getTaskReminderSuccessCount() {
        return taskReminderSuccessCount;
    }

    public int getTaskNotificationSuccessCount() {
        return taskNotificationSuccessCount;
    }

    public int getUpdateNotificationSuccessCount() {
        return updateNotificationSuccessCount;
    }

    private RegisteredUserTestHarness buildInstanceThatRequiresTaskReminder(NotificationListTestScenario testScenario, NotificationListTestCase testCase) {
        RegisteredUser user = getDummyUser();
        user.setLatestTaskNotificationDate(reminderBaselineDate);
        Action action = getDummyAction(NotificationMethod.SYNDICATED);
        ApplicationFormActionRequired applicationFormActionRequired = getDummyApplicationFormActionRequired(action);
        return new RegisteredUserTestHarness(user, testScenario, testCase, getDummyApplicationFormUserRole(getDummyApplication(), user, getDummyRole(false),
                true, false, applicationFormActionRequired));
    }

    private RegisteredUserTestHarness buildInstanceThatDoesNotRequireTaskReminder(NotificationListTestScenario testScenario, NotificationListTestCase testCase) {
        return buildInstanceThatDoesNotRequiredTaskNotificationOrReminder(testScenario, testCase, (Date) reminderBaselineDate.clone());
    }

    private RegisteredUserTestHarness buildInstanceThatRequiresTaskNotification(NotificationListTestScenario testScenario, NotificationListTestCase testCase) {
        RegisteredUser user = getDummyUser();
        switch (testCase) {
            case DUETASKNOTIFICATIONNEVERRECEIVED:
                user.setLatestTaskNotificationDate(null);
                break;
            case DUETASKNOTIFICATIONRECEIVEDAGESAGO:
                user.setLatestTaskNotificationDate(DateUtils.addDays((Date) expiryBaselineDate.clone(), 1));
                break;
            default: break;
        };
        Action action = getDummyAction(NotificationMethod.SYNDICATED);
        ApplicationFormActionRequired applicationFormActionRequired = getDummyApplicationFormActionRequired(action);
        return new RegisteredUserTestHarness(user, testScenario, testCase, getDummyApplicationFormUserRole(getDummyApplication(), user, getDummyRole(false),
                true, false, applicationFormActionRequired));
    }

    private RegisteredUserTestHarness buildInstanceThatDoesNotRequireTaskNotification(NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        return buildInstanceThatDoesNotRequiredTaskNotificationOrReminder(testScenario, testCase, DateUtils.addDays((Date) reminderBaselineDate.clone(), 1));
    }

    private RegisteredUserTestHarness buildInstanceThatRequiresUpdateNotification(NotificationListTestScenario testScenario, NotificationListTestCase testCase) {
        RegisteredUser user = getDummyUser();
        switch (testCase) {
            case DUEUPDATENOTIFICATIONNEVERRECEIVED:
                user.setLatestUpdateNotificationDate(null);
                break;
            case DUEUPDATENOTIFICATIONRECEIVEDAGESAGO:
                user.setLatestUpdateNotificationDate(DateUtils.addDays((Date) expiryBaselineDate.clone(), -1));
                break;
            default: break;
        };
        return new RegisteredUserTestHarness(user, testScenario, testCase, getDummyApplicationFormUserRole(getDummyApplication(), user, getDummyRole(true),
                false, true));
    }

    private RegisteredUserTestHarness buildInstanceThatDoesNotRequireUpdateNotification(NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        RegisteredUser user = getDummyUser();
        Role role = getDummyRole(true);
        user.setLatestUpdateNotificationDate(updateBaselineDate);
        ApplicationFormUserRole applicationFormUserRole = getDummyApplicationFormUserRole(getDummyApplication(), user, role, true, false);
        switch (testCase) {
            case RECIEVEDRECENTUPDATENOTIFICATION:
                user.setLatestUpdateNotificationDate(baselineDate);
                break;
            case ROLENOTSUBSCRIBEDTOUPDATES:
                role.setDoSendUpdateNotification(false);
                break;
            case NOUPDATES:
                applicationFormUserRole.setRaisesUpdateFlag(false);
                break;
            case OLDUPDATES:
                applicationFormUserRole.setUpdateTimestamp(reminderBaselineDate);
                break;
            case USERACCOUNTDISABLED:
                user.setEnabled(false);
                break;
            case USERACCOUNTEXPIRED:
                user.setAccountNonExpired(false);
                break;
            case USERACCOUNTLOCKED:
                user.setAccountNonLocked(false);
                break;
            case USERCREDENTIALSEXPIRED:
                user.setCredentialsNonExpired(false);
                break;
            default: break;
        }
        return new RegisteredUserTestHarness(user, testScenario, testCase, applicationFormUserRole);
    }

    private RegisteredUserTestHarness buildInstanceThatDoesNotRequiredTaskNotificationOrReminder(NotificationListTestScenario testScenario,
            NotificationListTestCase testCase, Date instanceBaselineDate) {
        RegisteredUser user = getDummyUser();
        user.setLatestTaskNotificationDate(instanceBaselineDate);
        Action action = getDummyAction(NotificationMethod.SYNDICATED);
        ApplicationFormActionRequired applicationFormActionRequired = getDummyApplicationFormActionRequired(action);
        ApplicationFormUserRole applicationFormUserRole = getDummyApplicationFormUserRole(getDummyApplication(), user, getDummyRole(false), true, false,
                applicationFormActionRequired);
        switch (testCase) {
            case RECEIVEDRECENTTASKNOTIFICATION:
                user.setLatestTaskNotificationDate(DateUtils.addDays(instanceBaselineDate, 1));
                break;
            case NOTIFICATIONWINDOWEXPIRED:
                applicationFormUserRole.setAssignedTimestamp(DateUtils.addDays((Date) expiryBaselineDate.clone(), -1));
                break;
            case NOURGENTACTIONS:
                applicationFormUserRole.setRaisesUrgentFlag(false);
                break;
            case ACTIONNOTSYNDICATED:
                action.setNotification(NotificationMethod.INDIVIDUAL);
                break;
            case USERACCOUNTDISABLED:
                user.setEnabled(false);
                break;
            case USERACCOUNTEXPIRED:
                user.setAccountNonExpired(false);
                break;
            case USERACCOUNTLOCKED:
                user.setAccountNonLocked(false);
                break;
            case USERCREDENTIALSEXPIRED:
                user.setCredentialsNonExpired(false);
                break;
            default: break;
        }
        return new RegisteredUserTestHarness(user, testScenario, testCase, applicationFormUserRole);
    }

    private ApplicationFormUserRole getDummyApplicationFormUserRole(ApplicationForm application, RegisteredUser user, Role role, Boolean raisesUrgentFlag,
            Boolean raisesUpdateFlag, ApplicationFormActionRequired... applicationFormActionRequireds) {
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(role)
                .raisesUrgentFlag(raisesUrgentFlag).raisesUpdateFlag(raisesUpdateFlag).updateTimestamp(baselineDate).actions(Arrays.asList(applicationFormActionRequireds)).build();
        applicationFormUserRoleDAO.save(applicationFormUserRole);
        return applicationFormUserRole;
    }

    private ApplicationForm getDummyApplication() {
        ApplicationForm application = new ApplicationFormBuilder().applicant(getDummyUser()).build();
        applicationFormDAO.save(application);
        return application;
    }

    private RegisteredUser getDummyUser() {
        RegisteredUser user = new RegisteredUserBuilder().username(encryptionUtils.generateUUID()).enabled(true).accountNonExpired(true).accountNonLocked(true)
                .credentialsNonExpired(true).build();
        userDAO.save(user);
        return user;
    }

    private Role getDummyRole(Boolean doSendUpdateNotification) {
        Role role = new RoleBuilder().id(Authority.SUPERADMINISTRATOR).doSendUpdateNotification(doSendUpdateNotification).build();
        roleDAO.save(role);
        return role;
    }

    private ApplicationFormActionRequired getDummyApplicationFormActionRequired(Action action) {
        ApplicationFormActionRequired applicationFormActionRequired = new ApplicationFormActionRequired();
        applicationFormActionRequired.setAction(action);
        applicationFormActionRequired.setDeadlineTimestamp(baselineDate);
        return applicationFormActionRequired;
    }

    private Action getDummyAction(NotificationMethod notification) {
        Action action = new ActionBuilder().id(ApplicationFormAction.COMPLETE_VALIDATION_STAGE).notification(notification).build();
        actionDAO.save(action);
        return action;
    }

}