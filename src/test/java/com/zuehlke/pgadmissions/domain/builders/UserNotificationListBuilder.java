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
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
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
    private int opportunityRequestNotificationSuccessCount = 0;
    private Program program;
    private final TestObjectProvider testObjectProvider;
    private final HashMap<Integer, RegisteredUser> testUsers = new HashMap<Integer, RegisteredUser>();
    private final Action actionWithSyndicatedNotification;
    private final Action actionWithIndividualNotification;
    private final Role roleWithUpdateNotification;
    private final Role roleWithoutUpdateNotification;
    private final Role superadministratorRole;
    private final EncryptionUtils encryptionUtils;
    private final SessionFactory sessionFactory;
    private final Date baselineDate;
    private final int testIterations;
    private final Date updateBaselineDate;
    private final Date opportunityRequestBaselineDate;
    private final Date reminderBaselineDate;
    private final Date expiryBaselineDate;    

    public UserNotificationListBuilder(SessionFactory sessionFactory, Date baselineDate, int testIterations, int reminderIntervalInDays,
            int expiryIntervalInDays, Program program) {
        this.encryptionUtils = new EncryptionUtils();
        this.sessionFactory = sessionFactory;
        this.baselineDate = baselineDate;
        this.testIterations = testIterations;
        this.testObjectProvider = new TestObjectProvider(sessionFactory);
        this.program = testObjectProvider.getEnabledProgram();
        this.updateBaselineDate = DateUtils.addDays((Date) baselineDate.clone(), -1);
        this.opportunityRequestBaselineDate = DateUtils.addDays((Date) baselineDate.clone(), -1);
        this.reminderBaselineDate = DateUtils.addDays((Date) baselineDate.clone(), -reminderIntervalInDays);
        this.expiryBaselineDate = DateUtils.addDays((Date) baselineDate.clone(), -expiryIntervalInDays);
        this.actionWithSyndicatedNotification = testObjectProvider.getAction(NotificationMethod.SYNDICATED);
        this.actionWithIndividualNotification = testObjectProvider.getAction(NotificationMethod.INDIVIDUAL);             
        this.roleWithUpdateNotification = testObjectProvider.getRole(true);
        this.roleWithoutUpdateNotification = testObjectProvider.getRole(false);
        this.superadministratorRole = testObjectProvider.getRole(Authority.SUPERADMINISTRATOR);
    }

    public HashMap<Integer, RegisteredUserTestHarness> builtTestInstances() {
        HashMap<Integer, RegisteredUserTestHarness> testHarnesses = new HashMap<Integer, RegisteredUserTestHarness>();
        for (int i = 0; i < testIterations; i++) {
            int testInstance = 0;
            for (NotificationListTestScenario testScenario : NotificationListTestScenario.values()) {
                switch (testScenario) {
                case TASK_REMINDER_SUCCESS:
                    for (NotificationListTestCase testCase : testScenario.getTestCases()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatRequiresTaskReminder(allocateTestUser(i, testInstance), testScenario, testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        taskReminderSuccessCount++;
                        testInstance++;
                    }
                    break;
                case TASK_REMINDER_FAILURE:
                    for (NotificationListTestCase testCase : testScenario.getTestCases()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatDoesNotRequireTaskReminder(allocateTestUser(i, testInstance), testScenario,
                                testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        testInstance++;
                    }
                    break;
                case TASK_NOTIFICATION_SUCCESS:
                    for (NotificationListTestCase testCase : testScenario.getTestCases()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatRequiresTaskNotification(allocateTestUser(i, testInstance), testScenario,
                                testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        taskNotificationSuccessCount++;
                        testInstance++;
                    }
                    break;
                case TASK_NOTIFICATION_FAILURE:
                    for (NotificationListTestCase testCase : testScenario.getTestCases()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatDoesNotRequireTaskNotification(allocateTestUser(i, testInstance),
                                testScenario, testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        testInstance++;
                    }
                    break;
                case UPDATE_NOTIFICATION_SUCCESS:
                    for (NotificationListTestCase testCase : testScenario.getTestCases()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatRequiresUpdateNotification(allocateTestUser(i, testInstance), testScenario,
                                testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        updateNotificationSuccessCount++;
                        testInstance++;
                    }
                    break;
                case UPDATE_NOTIFICATION_FAILURE:
                    for (NotificationListTestCase testCase : testScenario.getTestCases()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatDoesNotRequireUpdateNotification(allocateTestUser(i, testInstance),
                                testScenario, testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        testInstance++;
                    }
                    break;
                case OPPORTUNITY_REQUEST_NOTIFICATION_SUCCESS:
                    for (NotificationListTestCase testCase : testScenario.getTestCases()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatRequiresOpportunityRequestNotification(allocateTestUser(i, testInstance),
                                testScenario, testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        opportunityRequestNotificationSuccessCount++;
                        testInstance++;
                    }
                    break;
                case OPPORTUNITY_REQUEST_NOTIFICATION_FAILURE:
                    for (NotificationListTestCase testCase : testScenario.getTestCases()) {
                        RegisteredUserTestHarness userHarness = buildInstanceThatDoesNotRequireOpportunityRequestNotification(
                                allocateTestUser(i, testInstance), testScenario, testCase);
                        testHarnesses.put(userHarness.getRegisteredUser().getId(), userHarness);
                        testInstance++;
                    }
                    break;
                default:
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
    
    public int getOpportunityRequestNotificationSuccessCount() {
        return opportunityRequestNotificationSuccessCount;
    }

    private RegisteredUserTestHarness buildInstanceThatRequiresTaskReminder(RegisteredUser user, NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        user.setLatestTaskNotificationDate(reminderBaselineDate);
        ApplicationFormUserRole applicationFormUserRole = getDummyApplicationFormUserRole(getDummyApplication(), user, roleWithoutUpdateNotification, true,
                false, getDummyApplicationFormActionRequired(actionWithSyndicatedNotification));
        return new RegisteredUserTestHarness(user, testScenario, applicationFormUserRole);
    }

    private RegisteredUserTestHarness buildInstanceThatDoesNotRequireTaskReminder(RegisteredUser user, NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        return buildInstanceThatDoesNotRequiredTaskNotificationOrReminder(user, testScenario, testCase, (Date) reminderBaselineDate.clone());
    }

    private RegisteredUserTestHarness buildInstanceThatRequiresTaskNotification(RegisteredUser user, NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        switch (testCase) {
        case DUE_TASK_NOTIFICATION_NEVER_RECEIVED:
            user.setLatestTaskNotificationDate(null);
            break;
        case DUE_TASK_NOTIFICATION_RECEIVED_AGES_AGO:
            user.setLatestTaskNotificationDate(DateUtils.addDays((Date) expiryBaselineDate.clone(), 1));
            break;
        default:
            break;
        }
        saveDummyObject(user);
        ApplicationFormUserRole applicationFormUserRole = getDummyApplicationFormUserRole(getDummyApplication(), user, roleWithoutUpdateNotification, true,
                false, getDummyApplicationFormActionRequired(actionWithSyndicatedNotification));
        return new RegisteredUserTestHarness(user, testScenario, applicationFormUserRole);
    }

    private RegisteredUserTestHarness buildInstanceThatDoesNotRequireTaskNotification(RegisteredUser user, NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        return buildInstanceThatDoesNotRequiredTaskNotificationOrReminder(user, testScenario, testCase,
                DateUtils.addDays((Date) reminderBaselineDate.clone(), 1));
    }

    private RegisteredUserTestHarness buildInstanceThatRequiresUpdateNotification(RegisteredUser user, NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        switch (testCase) {
        case DUE_UPDATE_NOTIFICATION_NEVER_RECEIVED:
            user.setLatestUpdateNotificationDate(null);
            break;
        case DUE_UPDATE_NOTIFICATION_RECEIVED_AGES_AGO:
            user.setLatestUpdateNotificationDate(DateUtils.addDays((Date) baselineDate.clone(), -1));
            break;
        default:
            break;
        }
        updateDummyObject(user);
        ApplicationFormUserRole applicationFormUserRole = getDummyApplicationFormUserRole(getDummyApplication(), user, roleWithUpdateNotification, false, true);
        applicationFormUserRole.setUpdateTimestamp(baselineDate);
        return new RegisteredUserTestHarness(user, testScenario, applicationFormUserRole);
    }

    private RegisteredUserTestHarness buildInstanceThatDoesNotRequireUpdateNotification(RegisteredUser user, NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        user.setLatestUpdateNotificationDate(updateBaselineDate);
        ApplicationFormUserRole applicationFormUserRole = getDummyApplicationFormUserRole(getDummyApplication(), user, roleWithUpdateNotification, true, false);
        switch (testCase) {
        case RECEIVED_RECENT_UPDATE_NOTIFICATION:
            user.setLatestUpdateNotificationDate(baselineDate);
            updateDummyObject(user);
            break;
        case ROLE_NOT_SUBSCRIBED_TO_UPDATES:
            applicationFormUserRole.setRole(roleWithoutUpdateNotification);
            updateDummyObject(applicationFormUserRole);
            break;
        case NO_UPDATES:
            applicationFormUserRole.setRaisesUpdateFlag(false);
            updateDummyObject(applicationFormUserRole);
            break;
        case OLD_UPDATES:
            applicationFormUserRole.setUpdateTimestamp(reminderBaselineDate);
            updateDummyObject(applicationFormUserRole);
            break;
        case USER_ACCOUNT_DISABLED:
            user.setEnabled(false);
            updateDummyObject(user);
            break;
        case USER_ACCOUNT_EXPIRED:
            user.setAccountNonExpired(false);
            updateDummyObject(user);
            break;
        case USER_ACCOUNT_LOCKED:
            user.setAccountNonLocked(false);
            updateDummyObject(user);
            break;
        case USER_CREDENTIALS_EXPIRED:
            user.setCredentialsNonExpired(false);
            updateDummyObject(user);
            break;
        default:
            break;
        }
        return new RegisteredUserTestHarness(user, testScenario, applicationFormUserRole);
    }

    private RegisteredUserTestHarness buildInstanceThatDoesNotRequiredTaskNotificationOrReminder(RegisteredUser user,
            NotificationListTestScenario testScenario, NotificationListTestCase testCase, Date instanceBaselineDate) {
        user.setLatestTaskNotificationDate(instanceBaselineDate);
        ApplicationFormActionRequired applicationFormActionRequired = getDummyApplicationFormActionRequired(actionWithSyndicatedNotification);
        ApplicationFormUserRole applicationFormUserRole = getDummyApplicationFormUserRole(getDummyApplication(), user, roleWithoutUpdateNotification, true,
                false, applicationFormActionRequired);
        switch (testCase) {
        case RECEIVED_RECENT_TASK_NOTIFICATION:
            user.setLatestTaskNotificationDate(DateUtils.addDays(instanceBaselineDate, 1));
            updateDummyObject(user);
            break;
        case NOTIFICATION_WINDOW_EXPIRED:
            applicationFormActionRequired.setDeadlineTimestamp(expiryBaselineDate);
            updateDummyObject(applicationFormActionRequired);
            break;
        case NO_URGENT_ACTIONS:
            applicationFormUserRole.setRaisesUrgentFlag(false);
            updateDummyObject(applicationFormUserRole);
            break;
        case ACTION_NOT_SYNDICATED:
            applicationFormActionRequired.setAction(actionWithIndividualNotification);
            updateDummyObject(applicationFormActionRequired);
            break;
        case USER_ACCOUNT_DISABLED:
            user.setEnabled(false);
            updateDummyObject(user);
            break;
        case USER_ACCOUNT_EXPIRED:
            user.setAccountNonExpired(false);
            updateDummyObject(user);
            break;
        case USER_ACCOUNT_LOCKED:
            user.setAccountNonLocked(false);
            updateDummyObject(user);
            break;
        case USER_CREDENTIALS_EXPIRED:
            user.setCredentialsNonExpired(false);
            updateDummyObject(user);
            break;
        default:
            break;
        }
        return new RegisteredUserTestHarness(user, testScenario, applicationFormUserRole);
    }

    private RegisteredUserTestHarness buildInstanceThatDoesNotRequireOpportunityRequestNotification(RegisteredUser user,
            NotificationListTestScenario testScenario, NotificationListTestCase testCase) {
        switch (testCase) {
        case RECEIVED_RECENT_OPPORTUNITY_REQUEST_NOTIFICATION:
            user.getRoles().add(superadministratorRole);
            user.setLatestOpportunityRequestNotificationDate(baselineDate);
            break;
        case ROLE_NOT_SUPERADMINISTRATOR:
            user.getRoles().clear();
            break;
        default:
            break;
        }
        updateDummyObject(user);
        return new RegisteredUserTestHarness(user, testScenario);
    }

    private RegisteredUserTestHarness buildInstanceThatRequiresOpportunityRequestNotification(RegisteredUser user, NotificationListTestScenario testScenario,
            NotificationListTestCase testCase) {
        switch (testCase) {
        case OPPORTUNITY_REQUEST_NOTIFICATION_NEVER_RECEIVED:
            user.getRoles().add(superadministratorRole);
            user.setLatestOpportunityRequestNotificationDate(null);
            break;
        case OPPORTUNITY_REQUEST_NOTIFICATION_RECEIVED_AGES_AGO:
            user.getRoles().add(superadministratorRole);
            user.setLatestOpportunityRequestNotificationDate(DateUtils.addDays(opportunityRequestBaselineDate, -1));
            break;
        default:
            break;
        }
        updateDummyObject(user);
        return new RegisteredUserTestHarness(user, testScenario);
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
        ApplicationForm application = new ApplicationFormBuilder().applicant(getDummyUser()).advert(program).build();
        saveDummyObject(application);
        return application;
    }

    private RegisteredUser allocateTestUser(int testIteration, int testInstance) {
        RegisteredUser testUser = null;
        if (testIteration == 0 || (testIteration % 2) == 0) {
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

    private void saveDummyObject(Object dummyObject) {
        sessionFactory.getCurrentSession().save(dummyObject);
    }

    private void updateDummyObject(Object dummyObject) {
        sessionFactory.getCurrentSession().update(dummyObject);
    }

}