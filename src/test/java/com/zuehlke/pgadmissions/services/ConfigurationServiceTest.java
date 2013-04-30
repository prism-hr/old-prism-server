package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.PersonDAO;
import com.zuehlke.pgadmissions.dao.ReminderIntervalDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class ConfigurationServiceTest {

	private StageDurationDAO stageDurationDAOMock;
	private ReminderIntervalDAO reminderIntervalDAOMock;
	private PersonDAO personDAOMock;
	private ConfigurationService service;
	
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
		final Person registryUserOne = new PersonBuilder().id(1).build();
		final Person registryUserTwo = new PersonBuilder().id(2).build();
		final Person registryUserThree  = new PersonBuilder().id(3).build();

		service = new ConfigurationService(stageDurationDAOMock, reminderIntervalDAOMock, personDAOMock){

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
		EasyMock.replay(stageDurationDAOMock, personDAOMock, reminderIntervalDAOMock);
		
		service.saveConfigurations(Arrays.asList(validationDuration, interviewDuration), Arrays.asList(registryUserOne, registryUserTwo), reminderInterval);	
		
		EasyMock.verify(stageDurationDAOMock, personDAOMock, reminderIntervalDAOMock);
		assertEquals((Integer) 1, oldValidationDuration.getDuration());
		assertEquals(DurationUnitEnum.HOURS, oldValidationDuration.getUnit());
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
		service = new ConfigurationService(stageDurationDAOMock, reminderIntervalDAOMock, personDAOMock);
	}
}
