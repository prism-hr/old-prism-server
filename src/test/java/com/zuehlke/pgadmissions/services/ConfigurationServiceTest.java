package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.NotificationsDurationDAO;
import com.zuehlke.pgadmissions.dao.PersonDAO;
import com.zuehlke.pgadmissions.dao.ReminderIntervalDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.NotificationsDuration;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.NotificationsDurationBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReminderIntervalBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;
import com.zuehlke.pgadmissions.dto.ServiceLevelsDTO;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class ConfigurationServiceTest {

    private StageDurationDAO stageDurationDAOMock;

    private ReminderIntervalDAO reminderIntervalDAOMock;

    private NotificationsDurationDAO notificationsDurationDAOMock;

    private PersonDAO personDAOMock;

    private ConfigurationService service;

    private UserFactory userFactoryMock;

    private UserFactory userFactory;

    private UserDAO userDAOMock;

    private ApplicationFormUserRoleService applicationFormUserRoleService;

    private RoleService roleServiceMock;

    private RoleDAO roleDAOMock;

    @Test
    public void voidShouldGetRegistryUserWithIdFromDAO() {
        Person registryUser = new Person();
        registryUser.setId(2);
        EasyMock.expect(personDAOMock.getPersonWithId(2)).andReturn(registryUser);
        EasyMock.replay(personDAOMock);

        Assert.assertEquals(registryUser, service.getRegistryUserWithId(2));
    }

    @Test
    public void shouldGetAllPersonsWHoAreNotSuggestedSupervisorsASRegistryContacts() {
        Person registryUser = new Person();
        registryUser.setId(2);
        SuggestedSupervisor suggestedSupervisor = new SuggestedSupervisor();
        suggestedSupervisor.setId(5);
        EasyMock.expect(personDAOMock.getAllPersons()).andReturn(Arrays.asList(registryUser, suggestedSupervisor)).anyTimes();
        EasyMock.replay(personDAOMock);
        assertEquals(1, service.getAllRegistryUsers().size());
        Assert.assertEquals(registryUser, service.getAllRegistryUsers().get(0));
    }

    @Test
    public void shouldSaveConfigurationObjects() {

        StageDuration validationDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).duration(1).unit(DurationUnitEnum.DAYS).build();
        StageDuration oldValidationDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).duration(5).unit(DurationUnitEnum.WEEKS)
                .build();

        ReminderInterval reminderInterval = new ReminderIntervalBuilder().reminderType(ReminderType.INTERVIEW_SCHEDULE).duration(8)
                .unit(DurationUnitEnum.WEEKS).build();
        ReminderInterval oldReminderInterval = new ReminderIntervalBuilder().reminderType(ReminderType.INTERVIEW_SCHEDULE).duration(10)
                .unit(DurationUnitEnum.DAYS).build();

        NotificationsDuration notificationsDuration = new NotificationsDurationBuilder().duration(8).unit(DurationUnitEnum.WEEKS).build();
        NotificationsDuration oldNotificationsDuration = new NotificationsDurationBuilder().id(1).duration(1).unit(DurationUnitEnum.DAYS).build();

        ServiceLevelsDTO serviceLevelsDTO = new ServiceLevelsDTO();
        serviceLevelsDTO.setStagesDuration(Lists.newArrayList(validationDuration));
        serviceLevelsDTO.setReminderIntervals(Lists.newArrayList(reminderInterval));
        serviceLevelsDTO.setNotificationsDuration(notificationsDuration);

        EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.VALIDATION)).andReturn(oldValidationDuration);
        EasyMock.expect(reminderIntervalDAOMock.getReminderInterval(ReminderType.INTERVIEW_SCHEDULE)).andReturn(oldReminderInterval);
        EasyMock.expect(notificationsDurationDAOMock.getNotificationsDuration()).andReturn(oldNotificationsDuration);

        EasyMock.replay(stageDurationDAOMock, reminderIntervalDAOMock, notificationsDurationDAOMock);
        service.saveConfigurations(serviceLevelsDTO);
        EasyMock.verify(stageDurationDAOMock, reminderIntervalDAOMock, notificationsDurationDAOMock);

        assertEquals(DurationUnitEnum.DAYS, oldValidationDuration.getUnit());
        assertEquals(DurationUnitEnum.WEEKS, oldReminderInterval.getUnit());
        assertEquals(DurationUnitEnum.WEEKS, oldNotificationsDuration.getUnit());
    }

    @Test
    public void shouldSaveRegistryUsers() {
        final Person registryUserOne = new PersonBuilder().id(1).email("registryUserOne@registryUserOne.com").build();
        final Person registryUserTwo = new PersonBuilder().id(2).email("registryUserTwo@registryUserTwo.com").build();
        final Person registryUserThree = new PersonBuilder().id(3).email("registryUserThree@registryUserThree.com").build();
        final RegisteredUser registryUserThree_ = new RegisteredUserBuilder().id(33).email("registryUserThree@registryUserThree.com").build();

        service = new ConfigurationService(stageDurationDAOMock, reminderIntervalDAOMock, notificationsDurationDAOMock, personDAOMock, userDAOMock,
                applicationFormUserRoleService, userFactoryMock, roleDAOMock) {
            @Override
            public List<Person> getAllRegistryUsers() {
                return Arrays.asList(registryUserOne, registryUserThree);
            }
        };

        personDAOMock.save(registryUserOne);
        personDAOMock.save(registryUserTwo);
        personDAOMock.delete(registryUserThree);

        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserThree.getEmail())).andReturn(registryUserThree_);
        userDAOMock.save(registryUserThree_);

        expect(roleDAOMock.getRoleByAuthority(Authority.ADMITTER)).andReturn(new Role()).anyTimes();
        expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(new Role()).anyTimes();

        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserOne.getEmail())).andReturn(
                new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.ADMITTER).build()).build());
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserTwo.getEmail())).andReturn(
                new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.ADMITTER).build()).build());

        EasyMock.replay(roleDAOMock, personDAOMock, userDAOMock);

        service.saveRegistryUsers(Arrays.asList(registryUserOne, registryUserTwo), new RegisteredUser());

        EasyMock.verify(roleDAOMock, personDAOMock, userDAOMock);
    }

    @Test
    public void shouldCreateNewRegisteredUsersForAdmissionContactsThatDoNotExistYet() {
        final Person registryUserOne = new PersonBuilder().id(1).email("registryUserOne@registryUserOne.com").build();
        final Person registryUserTwo = new PersonBuilder().id(2).email("registryUserTwo@registryUserTwo.com").build();
        final Person registryUserThree = new PersonBuilder().id(3).email("registryUserThree@registryUserThree.com").build();
        final RegisteredUser registryUserThree_ = new RegisteredUserBuilder().id(33).email("registryUserThree@registryUserThree.com").build();

        service = new ConfigurationService(stageDurationDAOMock, reminderIntervalDAOMock, notificationsDurationDAOMock, personDAOMock, userDAOMock,
                applicationFormUserRoleService, userFactory, roleDAOMock) {
            @Override
            public List<Person> getAllRegistryUsers() {
                return Arrays.asList(registryUserOne, registryUserThree);
            }
        };

        personDAOMock.save(registryUserOne);
        personDAOMock.save(registryUserTwo);
        personDAOMock.delete(registryUserThree);

        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserThree.getEmail())).andReturn(registryUserThree_);
        userDAOMock.save(registryUserThree_);

        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserOne.getEmail())).andReturn(null);
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserTwo.getEmail())).andReturn(null);

        Capture<RegisteredUser> captureRegistryUserOne = new Capture<RegisteredUser>();
        Capture<RegisteredUser> captureRegistryUserTwo = new Capture<RegisteredUser>();

        EasyMock.expect(roleServiceMock.getRoleByAuthority(Authority.VIEWER)).andReturn(new RoleBuilder().id(Authority.VIEWER).build()).times(2);
        EasyMock.expect(roleServiceMock.getRoleByAuthority(Authority.ADMITTER)).andReturn(new RoleBuilder().id(Authority.ADMITTER).build()).times(2);

        expect(roleDAOMock.getRoleByAuthority(Authority.ADMITTER)).andReturn(new Role()).anyTimes();
        expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(new Role()).anyTimes();

        userDAOMock.save(EasyMock.capture(captureRegistryUserOne));
        userDAOMock.save(EasyMock.capture(captureRegistryUserTwo));
        
        EasyMock.replay(roleDAOMock, personDAOMock, userDAOMock, roleServiceMock);

        service.saveRegistryUsers(Arrays.asList(registryUserOne, registryUserTwo), new RegisteredUser());

        EasyMock.verify(roleDAOMock, personDAOMock, userDAOMock, roleServiceMock);

        assertEquals(registryUserOne.getFirstname(), captureRegistryUserOne.getValue().getFirstName());
        assertEquals(registryUserOne.getLastname(), captureRegistryUserOne.getValue().getLastName());
        assertEquals(registryUserOne.getEmail(), captureRegistryUserOne.getValue().getEmail());

        assertEquals(registryUserTwo.getFirstname(), captureRegistryUserTwo.getValue().getFirstName());
        assertEquals(registryUserTwo.getLastname(), captureRegistryUserTwo.getValue().getLastName());
        assertEquals(registryUserTwo.getEmail(), captureRegistryUserTwo.getValue().getEmail());
    }

    @Test
    public void shouldAddTheAdmitterRoleToAnAdmissionContactsThatAlreadyExists() {
        final Person registryUserOne = new PersonBuilder().id(1).email("registryUserOne@registryUserOne.com").build();
        final Person registryUserTwo = new PersonBuilder().id(2).email("registryUserTwo@registryUserTwo.com").build();
        final Person registryUserThree = new PersonBuilder().id(3).email("registryUserThree@registryUserThree.com").build();
        final RegisteredUser registryUserThree_ = new RegisteredUserBuilder().id(33).email("registryUserThree@registryUserThree.com").build();

        service = new ConfigurationService(stageDurationDAOMock, reminderIntervalDAOMock, notificationsDurationDAOMock, personDAOMock, userDAOMock,
                applicationFormUserRoleService, userFactory, roleDAOMock) {
            @Override
            public List<Person> getAllRegistryUsers() {
                return Arrays.asList(registryUserOne, registryUserThree);
            }
        };

        personDAOMock.save(registryUserOne);
        personDAOMock.save(registryUserTwo);
        personDAOMock.delete(registryUserThree);
        ReminderInterval reminderInterval = new ReminderInterval();
        reminderInterval.setId(1);

        reminderIntervalDAOMock.save(reminderInterval);

        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserOne.getEmail())).andReturn(
                new RegisteredUserBuilder().id(11).role(new RoleBuilder().id(Authority.VIEWER).build()).build());
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserTwo.getEmail())).andReturn(
                new RegisteredUserBuilder().id(22).role(new RoleBuilder().id(Authority.VIEWER).build()).build());

        Capture<RegisteredUser> captureRegistryUserOne = new Capture<RegisteredUser>();
        Capture<RegisteredUser> captureRegistryUserTwo = new Capture<RegisteredUser>();

        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserThree.getEmail())).andReturn(registryUserThree_);
        userDAOMock.save(EasyMock.eq(registryUserThree_));

        userDAOMock.save(EasyMock.capture(captureRegistryUserOne));
        userDAOMock.save(EasyMock.capture(captureRegistryUserTwo));

        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMITTER)).andReturn(new RoleBuilder().id(Authority.ADMITTER).build()).anyTimes();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(new RoleBuilder().id(Authority.VIEWER).build()).anyTimes();

        EasyMock.replay(personDAOMock, userDAOMock, roleServiceMock, roleDAOMock);

        service.saveRegistryUsers(Arrays.asList(registryUserOne, registryUserTwo), new RegisteredUser());

        EasyMock.verify(personDAOMock, userDAOMock, roleServiceMock, roleDAOMock);

        assertTrue(captureRegistryUserOne.getValue().isInRole(Authority.ADMITTER));
        assertTrue(captureRegistryUserOne.getValue().isInRole(Authority.VIEWER));
        assertTrue(captureRegistryUserTwo.getValue().isInRole(Authority.ADMITTER));
        assertTrue(captureRegistryUserTwo.getValue().isInRole(Authority.VIEWER));
    }

    @Test
    public void shouldReturnMapOfStageDurations() {
        StageDuration stageDurationOne = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).build();
        StageDuration stageDurationTwo = new StageDurationBuilder().stage(ApplicationFormStatus.REVIEW).build();
        StageDuration stageDurationThree = new StageDurationBuilder().stage(ApplicationFormStatus.INTERVIEW).build();
        StageDuration stageDurationFour = new StageDurationBuilder().stage(ApplicationFormStatus.APPROVAL).build();
        EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.VALIDATION)).andReturn(stageDurationOne);
        EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.REVIEW)).andReturn(stageDurationTwo);
        EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.INTERVIEW)).andReturn(stageDurationThree);
        EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(stageDurationFour);
        EasyMock.replay(stageDurationDAOMock);
        Map<ApplicationFormStatus, StageDuration> durations = service.getStageDurations();
        assertEquals(stageDurationOne, durations.get(ApplicationFormStatus.VALIDATION));
        assertEquals(stageDurationTwo, durations.get(ApplicationFormStatus.REVIEW));
        assertEquals(stageDurationThree, durations.get(ApplicationFormStatus.INTERVIEW));
        assertEquals(stageDurationFour, durations.get(ApplicationFormStatus.APPROVAL));
    }

    @Test
    public void shouldGetReminderInterval() {
        ArrayList<ReminderInterval> intervals = Lists.newArrayList();

        EasyMock.expect(reminderIntervalDAOMock.getReminderIntervals()).andReturn(intervals);
        EasyMock.replay(reminderIntervalDAOMock);
        assertSame(intervals, service.getReminderIntervals());
    }

    @Test
    public void shouldGetConfigurableStages() {
        ApplicationFormStatus[] configurableStages = service.getConfigurableStages();
        assertArrayEquals(new ApplicationFormStatus[] { ApplicationFormStatus.VALIDATION, ApplicationFormStatus.REVIEW, ApplicationFormStatus.INTERVIEW,
                ApplicationFormStatus.APPROVAL, }, configurableStages);
    }

    @Before
    public void setUp() {
        stageDurationDAOMock = EasyMock.createMock(StageDurationDAO.class);
        reminderIntervalDAOMock = EasyMock.createMock(ReminderIntervalDAO.class);
        notificationsDurationDAOMock = EasyMock.createMock(NotificationsDurationDAO.class);
        personDAOMock = EasyMock.createMock(PersonDAO.class);
        userFactoryMock = EasyMock.createMock(UserFactory.class);
        roleServiceMock = EasyMock.createMock(RoleService.class);
        userDAOMock = EasyMock.createMock(UserDAO.class);
        userFactory = new UserFactory(roleServiceMock, new EncryptionUtils());
        roleDAOMock = EasyMock.createMock(RoleDAO.class);
        applicationFormUserRoleService = EasyMock.createMock(ApplicationFormUserRoleService.class);
        
        service = new ConfigurationService(stageDurationDAOMock, reminderIntervalDAOMock, notificationsDurationDAOMock, personDAOMock, userDAOMock,
                applicationFormUserRoleService, userFactoryMock, roleDAOMock);
    }
}