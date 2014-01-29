package com.zuehlke.pgadmissions.domain.builders;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.SessionFactory;

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
    private final HashMap<Integer, RegisteredUser> testUsers = new HashMap<Integer, RegisteredUser>();
    private final Action actionWithSyndicatedNotification;
    private final Action actionWithIndividualNotification;
    private final Role roleWithUpdateNotification;
    private final Role roleWithoutUpdateNotification;
    private final EncryptionUtils encryptionUtils;
    private final SessionFactory sessionFactory;
    private final Date baselineDate;
    private final int testIterations;
    private final Date updateBaselineDate;
    private final Date reminderBaselineDate;
    private final Date expiryBaselineDate;

    public UserNotificationListBuilder(SessionFactory sessionFactory, Date baselineDate, int testIterations, int reminderIntervalInDays,
            int expiryIntervalInDays) {
        this.encryptionUtils = new EncryptionUtils();
        this.sessionFactory = sessionFactory;
        this.baselineDate = baselineDate;
        this.testIterations = testIterations;
        this.updateBaselineDate = DateUtils.addDays((Date) baselineDate.clone(), -1);
        this.reminderBaselineDate = DateUtils.addDays((Date) baselineDate.clone(), -reminderIntervalInDays);
        this.expiryBaselineDate = DateUtils.addDays((Date) baselineDate.clone(), -expiryIntervalInDays);
        this.actionWithSyndicatedNotification = getDummyAction(ApplicationFormAction.COMPLETE_VALIDATION_STAGE, NotificationMethod.SYNDICATED);
        this.actionWithIndividualNotification = getDummyAction(ApplicationFormAction.PROVIDE_REFERENCE, NotificationMethod.INDIVIDUAL);
        this.roleWithUpdateNotification = getDummyRole(Authority.APPLICANT, true);
        this.roleWithoutUpdateNotification = getDummyRole(Authority.ADMINISTRATOR, false);
    }

    public HashMap<Integer, RegisteredUserTestHarness> builtTestInstances() {
        HashMap<Integer, RegisteredUserTestHarness> testHarnesses = new HashMap<Integer, RegisteredUserTestHarness>();
        for (int i = 0; i < testIterations; i++) {
            int testInstance = 0;
            for (NotificationListTestScenario testScenario : NotificationListTestScenario.values()) {
                switch (testScenario) {
                case TASKREMINDERSUCCESS:
                    for (NotificationListTestCase testCase : testScenario.getDisplayValue()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatRequiresTaskReminder(allocateTestUser(i, testInstance), testScenario, testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        taskReminderSuccessCount++;
                        testInstance++;
                    }
                    break;
                case TASKREMINDERFAILURE:
                    for (NotificationListTestCase testCase : testScenario.getDisplayValue()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatDoesNotRequireTaskReminder(allocateTestUser(i, testInstance), testScenario, testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        testInstance++;
                    }
                    break;
                case TASKNOTIFICATIONSUCCESS:
                    for (NotificationListTestCase testCase : testScenario.getDisplayValue()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatRequiresTaskNotification(allocateTestUser(i, testInstance), testScenario, testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        taskNotificationSuccessCount++;
                        testInstance++;
                    }
                    break;
                case TASKNOTIFICATIONFAILURE:
                    for (NotificationListTestCase testCase : testScenario.getDisplayValue()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatDoesNotRequireTaskNotification(allocateTestUser(i, testInstance), testScenario, testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        testInstance++;
                    }
                    break;
                case UPDATENOTIFICATIONSUCCESS:
                    for (NotificationListTestCase testCase : testScenario.getDisplayValue()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatRequiresUpdateNotification(allocateTestUser(i, testInstance), testScenario, testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        updateNotificationSuccessCount++;
                        testInstance++;
                    }
                    break;
                case UPDATENOTIFICATIONFAILURE:
                    for (NotificationListTestCase testCase : testScenario.getDisplayValue()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatDoesNotRequireUpdateNotification(allocateTestUser(i, testInstance), testScenario, testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        testInstance++;
                    }
                    break;
                }
            }
        }
        return testHarnesses;
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

    private RegisteredUserTestHarness buildInstanceThatRequiresTaskReminder(RegisteredUser user, NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        user.setLatestTaskNotificationDate(reminderBaselineDate);
        ApplicationFormUserRole applicationFormUserRole = getDummyApplicationFormUserRole(getDummyApplication(), user, roleWithoutUpdateNotification, true,
                false, getDummyApplicationFormActionRequired(actionWithSyndicatedNotification));
        return new RegisteredUserTestHarness(user, testScenario, testCase, applicationFormUserRole);
    }

    private RegisteredUserTestHarness buildInstanceThatDoesNotRequireTaskReminder(RegisteredUser user, NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        return buildInstanceThatDoesNotRequiredTaskNotificationOrReminder(user, testScenario, testCase, (Date) reminderBaselineDate.clone());
    }

    private RegisteredUserTestHarness buildInstanceThatRequiresTaskNotification(RegisteredUser user, NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        switch (testCase) {
        case DUETASKNOTIFICATIONNEVERRECEIVED:
            user.setLatestTaskNotificationDate(null);
            break;
        case DUETASKNOTIFICATIONRECEIVEDAGESAGO:
            user.setLatestTaskNotificationDate(DateUtils.addDays((Date) expiryBaselineDate.clone(), 1));
            break;
        default:
            break;
        }
        ;
        saveDummyObject(user);
        ApplicationFormUserRole applicationFormUserRole = getDummyApplicationFormUserRole(getDummyApplication(), user, roleWithoutUpdateNotification, true,
                false, getDummyApplicationFormActionRequired(actionWithSyndicatedNotification));
        return new RegisteredUserTestHarness(user, testScenario, testCase, applicationFormUserRole);
    }

    private RegisteredUserTestHarness buildInstanceThatDoesNotRequireTaskNotification(RegisteredUser user, NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        return buildInstanceThatDoesNotRequiredTaskNotificationOrReminder(user, testScenario, testCase,
                DateUtils.addDays((Date) reminderBaselineDate.clone(), 1));
    }

    private RegisteredUserTestHarness buildInstanceThatRequiresUpdateNotification(RegisteredUser user, NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        switch (testCase) {
        case DUEUPDATENOTIFICATIONNEVERRECEIVED:
            user.setLatestUpdateNotificationDate(null);
            break;
        case DUEUPDATENOTIFICATIONRECEIVEDAGESAGO:
            user.setLatestUpdateNotificationDate(DateUtils.addDays((Date) baselineDate.clone(), -1));
            break;
        default:
            break;
        }
        ;
        updateDummyObject(user);
        ApplicationFormUserRole applicationFormUserRole = getDummyApplicationFormUserRole(getDummyApplication(), user, roleWithUpdateNotification, false, true);
        applicationFormUserRole.setUpdateTimestamp(baselineDate);
        return new RegisteredUserTestHarness(user, testScenario, testCase, applicationFormUserRole);
    }

    private RegisteredUserTestHarness buildInstanceThatDoesNotRequireUpdateNotification(RegisteredUser user, NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        user.setLatestUpdateNotificationDate(updateBaselineDate);
        ApplicationFormUserRole applicationFormUserRole = getDummyApplicationFormUserRole(getDummyApplication(), user, roleWithUpdateNotification, true, false);
        switch (testCase) {
        case RECIEVEDRECENTUPDATENOTIFICATION:
            user.setLatestUpdateNotificationDate(baselineDate);
            updateDummyObject(user);
            break;
        case ROLENOTSUBSCRIBEDTOUPDATES:
            applicationFormUserRole.setRole(roleWithoutUpdateNotification);
            updateDummyObject(applicationFormUserRole);
            break;
        case NOUPDATES:
            applicationFormUserRole.setRaisesUpdateFlag(false);
            updateDummyObject(applicationFormUserRole);
            break;
        case OLDUPDATES:
            applicationFormUserRole.setUpdateTimestamp(reminderBaselineDate);
            updateDummyObject(applicationFormUserRole);
            break;
        case USERACCOUNTDISABLED:
            user.setEnabled(false);
            updateDummyObject(user);
            break;
        case USERACCOUNTEXPIRED:
            user.setAccountNonExpired(false);
            updateDummyObject(user);
            break;
        case USERACCOUNTLOCKED:
            user.setAccountNonLocked(false);
            updateDummyObject(user);
            break;
        case USERCREDENTIALSEXPIRED:
            user.setCredentialsNonExpired(false);
            updateDummyObject(user);
            break;
        default:
            break;
        }
        return new RegisteredUserTestHarness(user, testScenario, testCase, applicationFormUserRole);
    }

    private RegisteredUserTestHarness buildInstanceThatDoesNotRequiredTaskNotificationOrReminder(RegisteredUser user,
            NotificationListTestScenario testScenario, NotificationListTestCase testCase, Date instanceBaselineDate) {
        user.setLatestTaskNotificationDate(instanceBaselineDate);
        ApplicationFormActionRequired applicationFormActionRequired = getDummyApplicationFormActionRequired(actionWithSyndicatedNotification);
        ApplicationFormUserRole applicationFormUserRole = getDummyApplicationFormUserRole(getDummyApplication(), user, roleWithoutUpdateNotification, true,
                false, applicationFormActionRequired);
        switch (testCase) {
        case RECEIVEDRECENTTASKNOTIFICATION:
            user.setLatestTaskNotificationDate(DateUtils.addDays(instanceBaselineDate, 1));
            updateDummyObject(user);
            break;
        case NOTIFICATIONWINDOWEXPIRED:
            applicationFormActionRequired.setDeadlineTimestamp(expiryBaselineDate);
            updateDummyObject(applicationFormActionRequired);
            break;
        case NOURGENTACTIONS:
            applicationFormUserRole.setRaisesUrgentFlag(false);
            updateDummyObject(applicationFormUserRole);
            break;
        case ACTIONNOTSYNDICATED:
            applicationFormActionRequired.setAction(actionWithIndividualNotification);
            updateDummyObject(applicationFormActionRequired);
            break;
        case USERACCOUNTDISABLED:
            user.setEnabled(false);
            updateDummyObject(user);
            break;
        case USERACCOUNTEXPIRED:
            user.setAccountNonExpired(false);
            updateDummyObject(user);
            break;
        case USERACCOUNTLOCKED:
            user.setAccountNonLocked(false);
            updateDummyObject(user);
            break;
        case USERCREDENTIALSEXPIRED:
            user.setCredentialsNonExpired(false);
            updateDummyObject(user);
            break;
        default:
            break;
        }
        return new RegisteredUserTestHarness(user, testScenario, testCase, applicationFormUserRole);
    }

    private ApplicationFormUserRole getDummyApplicationFormUserRole(ApplicationForm application, RegisteredUser user, Role role, Boolean raisesUrgentFlag,
            Boolean raisesUpdateFlag, ApplicationFormActionRequired... applicationFormActionRequireds) {
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(role)
                .raisesUrgentFlag(raisesUrgentFlag).raisesUpdateFlag(raisesUpdateFlag).updateTimestamp(expiryBaselineDate)
                .actions(Arrays.asList(applicationFormActionRequireds)).build();
        saveDummyObject(applicationFormUserRole);
        return applicationFormUserRole;
    }

    private ApplicationForm getDummyApplication() {
        ApplicationForm application = new ApplicationFormBuilder().applicant(getDummyUser()).build();
        saveDummyObject(application);
        return application;
    }
    
    private RegisteredUser allocateTestUser(int testIteration, int testInstance) {
        RegisteredUser testUser = null;
        if (testIteration == 0) {
            testUser = getDummyUser();
            testUsers.put(testInstance, testUser);
        } else {
            testUser = testUsers.get(testInstance);
        }
        return testUser;
    }

    private RegisteredUser getDummyUser() {
        RegisteredUser user = new RegisteredUserBuilder().username(encryptionUtils.generateUUID()).enabled(true).accountNonExpired(true).accountNonLocked(true)
                .credentialsNonExpired(true).build();
        saveDummyObject(user);
        return user;
    }

    private ApplicationFormActionRequired getDummyApplicationFormActionRequired(Action action) {
        ApplicationFormActionRequired applicationFormActionRequired = new ApplicationFormActionRequired();
        applicationFormActionRequired.setAction(action);
        applicationFormActionRequired.setDeadlineTimestamp(baselineDate);
        return applicationFormActionRequired;
    }

    private Action getDummyAction(ApplicationFormAction actionId, NotificationMethod notification) {
        Action action = new ActionBuilder().id(actionId).notification(notification).build();
        updateDummyObject(action);
        return action;
    }

    private Role getDummyRole(Authority authority, Boolean doSendUpdateNotification) {
        Role role = new RoleBuilder().id(authority).doSendUpdateNotification(doSendUpdateNotification).build();
        updateDummyObject(role);
        return role;
    }

    private void saveDummyObject(Object dummyObject) {
        sessionFactory.getCurrentSession().save(dummyObject);
    }

    private void updateDummyObject(Object dummyObject) {
        sessionFactory.getCurrentSession().update(dummyObject);
    }

}