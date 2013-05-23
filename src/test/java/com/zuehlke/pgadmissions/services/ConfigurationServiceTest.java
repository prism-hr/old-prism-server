package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.PersonDAO;
import com.zuehlke.pgadmissions.dao.ReminderIntervalDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class ConfigurationServiceTest {

	private StageDurationDAO stageDurationDAOMock;
	
	private ReminderIntervalDAO reminderIntervalDAOMock;
	
	private PersonDAO personDAOMock;
	
	private ConfigurationService service;
	
	private UserFactory userFactoryMock;
	
	private UserFactory userFactory;
	
	private UserDAO userDAOMock;

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
	public void shouldSaveConfigurationObjects(){
		final Person registryUserOne = new PersonBuilder().id(1).email("registryUserOne@registryUserOne.com").build();
		final Person registryUserTwo = new PersonBuilder().id(2).email("registryUserTwo@registryUserTwo.com").build();
		final Person registryUserThree  = new PersonBuilder().id(3).email("registryUserThree@registryUserThree.com").build();

		service = new ConfigurationService(stageDurationDAOMock, reminderIntervalDAOMock, personDAOMock, userDAOMock, userFactoryMock, roleDAOMock) {
			@Override
			public List<Person> getAllRegistryUsers() {
				return Arrays.asList(registryUserOne, registryUserThree);
			}
		};
		
		StageDuration validationDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).duration(1).unit(DurationUnitEnum.HOURS).build();
		StageDuration oldValidationDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).duration(5).unit(DurationUnitEnum.WEEKS).build();
		StageDuration interviewDuration = new StageDurationBuilder().stage(ApplicationFormStatus.INTERVIEW).duration(3).unit(DurationUnitEnum.WEEKS).build();
		
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.VALIDATION)).andReturn(oldValidationDuration);
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.INTERVIEW)).andReturn(null);
		
		stageDurationDAOMock.save(oldValidationDuration);
		stageDurationDAOMock.save(interviewDuration);
		
		personDAOMock.save(registryUserOne);
		personDAOMock.save(registryUserTwo);
		personDAOMock.delete(registryUserThree);
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
	
		reminderIntervalDAOMock.save(reminderInterval);
		
		expect(roleDAOMock.getRoleByAuthority(Authority.ADMITTER)).andReturn(new Role()).anyTimes();
		expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(new Role()).anyTimes();
		
		EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserOne.getEmail())).andReturn(new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.ADMITTER).build()).build());
		EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserTwo.getEmail())).andReturn(new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.ADMITTER).build()).build());
		
		EasyMock.replay(roleDAOMock, stageDurationDAOMock, personDAOMock, reminderIntervalDAOMock, userDAOMock);
		
		service.saveConfigurations(Arrays.asList(validationDuration, interviewDuration), Arrays.asList(registryUserOne, registryUserTwo), reminderInterval, new RegisteredUser());	
		
		EasyMock.verify(roleDAOMock, stageDurationDAOMock, personDAOMock, reminderIntervalDAOMock, userDAOMock);
		assertEquals((Integer) 1, oldValidationDuration.getDuration());
		assertEquals(DurationUnitEnum.HOURS, oldValidationDuration.getUnit());
	}
	
	@Test
	public void shouldCreateNewRegisteredUsersForAdmissionContactsThatDoNotExistYet() {
	    final Person registryUserOne = new PersonBuilder().id(1).email("registryUserOne@registryUserOne.com").build();
        final Person registryUserTwo = new PersonBuilder().id(2).email("registryUserTwo@registryUserTwo.com").build();
        final Person registryUserThree  = new PersonBuilder().id(3).email("registryUserThree@registryUserThree.com").build();

        service = new ConfigurationService(stageDurationDAOMock, reminderIntervalDAOMock, personDAOMock, userDAOMock, userFactory, roleDAOMock) {
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
        
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserOne.getEmail())).andReturn(null);
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserTwo.getEmail())).andReturn(null);
        
        Capture<RegisteredUser> captureRegistryUserOne = new Capture<RegisteredUser>();
        Capture<RegisteredUser> captureRegistryUserTwo = new Capture<RegisteredUser>();
        
        EasyMock.expect(roleServiceMock.getRoleByAuthority(Authority.VIEWER)).andReturn(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).times(2);
        EasyMock.expect(roleServiceMock.getRoleByAuthority(Authority.ADMITTER)).andReturn(new RoleBuilder().authorityEnum(Authority.ADMITTER).build()).times(2);
        
        expect(roleDAOMock.getRoleByAuthority(Authority.ADMITTER)).andReturn(new Role()).anyTimes();
        expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(new Role()).anyTimes();
        
        userDAOMock.save(EasyMock.capture(captureRegistryUserOne));
        userDAOMock.save(EasyMock.capture(captureRegistryUserTwo));
        
        EasyMock.replay(stageDurationDAOMock, roleDAOMock,  personDAOMock, reminderIntervalDAOMock, userDAOMock, roleServiceMock);
        
        service.saveConfigurations(Collections.<StageDuration>emptyList(), Arrays.asList(registryUserOne, registryUserTwo), reminderInterval, new RegisteredUser());    
        
        EasyMock.verify(roleDAOMock, stageDurationDAOMock, personDAOMock, reminderIntervalDAOMock, userDAOMock, roleServiceMock);
        
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
        final Person registryUserThree  = new PersonBuilder().id(3).email("registryUserThree@registryUserThree.com").build();

        service = new ConfigurationService(stageDurationDAOMock, reminderIntervalDAOMock, personDAOMock, userDAOMock, userFactory, roleDAOMock) {
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
        
        
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserOne.getEmail())).andReturn(new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).build());
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts(registryUserTwo.getEmail())).andReturn(new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).build());
        
        Capture<RegisteredUser> captureRegistryUserOne = new Capture<RegisteredUser>();
        Capture<RegisteredUser> captureRegistryUserTwo = new Capture<RegisteredUser>();
        
        userDAOMock.save(EasyMock.capture(captureRegistryUserOne));
        userDAOMock.save(EasyMock.capture(captureRegistryUserTwo));
        
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMITTER)).andReturn(new RoleBuilder().authorityEnum(Authority.ADMITTER).build()).anyTimes();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).anyTimes();
        
        EasyMock.replay(stageDurationDAOMock, personDAOMock, reminderIntervalDAOMock, userDAOMock, roleServiceMock,roleDAOMock);
        
        service.saveConfigurations(Collections.<StageDuration>emptyList(), Arrays.asList(registryUserOne, registryUserTwo), reminderInterval, new RegisteredUser());    
        
        EasyMock.verify(stageDurationDAOMock, personDAOMock, reminderIntervalDAOMock, userDAOMock, roleServiceMock, roleDAOMock);
        
        assertTrue(captureRegistryUserOne.getValue().isInRole(Authority.ADMITTER));
        assertTrue(captureRegistryUserOne.getValue().isInRole(Authority.VIEWER));
        assertTrue(captureRegistryUserTwo.getValue().isInRole(Authority.ADMITTER));
        assertTrue(captureRegistryUserTwo.getValue().isInRole(Authority.VIEWER));
    }

	@Test
	public void shouldReturnMapOfStageDurations(){
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
	public void shouldGetReminderInterval(){
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		
		EasyMock.expect(reminderIntervalDAOMock.getReminderInterval()).andReturn(reminderInterval);
		EasyMock.replay(reminderIntervalDAOMock);
		assertEquals(reminderInterval, service.getReminderInterval());
	}
	
    @Test
    public void shouldGetConfigurableStages() {
        ApplicationFormStatus[] configurableStages = service.getConfigurableStages();
        assertArrayEquals(new ApplicationFormStatus[]{ApplicationFormStatus.VALIDATION, ApplicationFormStatus.REVIEW,ApplicationFormStatus.INTERVIEW,  ApplicationFormStatus.APPROVAL, },configurableStages);
    }
	
	@Before
	public void setUp(){
		stageDurationDAOMock = EasyMock.createMock(StageDurationDAO.class);		
		reminderIntervalDAOMock = EasyMock.createMock(ReminderIntervalDAO.class);
		personDAOMock = EasyMock.createMock(PersonDAO.class);
		userFactoryMock = EasyMock.createMock(UserFactory.class);
		roleServiceMock = EasyMock.createMock(RoleService.class);
		userFactory = new UserFactory(roleServiceMock, new EncryptionUtils());
		userDAOMock = EasyMock.createMock(UserDAO.class);
		roleDAOMock = EasyMock.createMock(RoleDAO.class);
		service = new ConfigurationService(stageDurationDAOMock, reminderIntervalDAOMock, personDAOMock, userDAOMock, userFactoryMock, roleDAOMock);
	}
}
