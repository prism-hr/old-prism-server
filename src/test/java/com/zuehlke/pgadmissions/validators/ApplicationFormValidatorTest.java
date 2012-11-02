package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.AdditionalInformationBuilder;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class ApplicationFormValidatorTest {

    @Autowired
	private ApplicationFormValidator validator;
    
	private ApplicationForm applicationForm;
	
	@Autowired
	private ProgramInstanceDAO programInstanceDAOMock;
	
	private ProgramInstance programInstance;
	
	private ProgrammeDetails programmeDetails;
	
	private Program program;

	@Test
	@DirtiesContext
	public void shouldSupportAppForm() {
		assertTrue(validator.supports(ApplicationForm.class));
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfProgrammeDetailsSectionMissing() {
		applicationForm.setProgrammeDetails(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");

		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.programmeDetails.incomplete", mappingResult.getFieldError("programmeDetails").getCode());

	}

	@Test
	@DirtiesContext
	public void shouldRejectIfProgrammeDetailsSectionNotSaved() {
		ProgrammeDetails unsavedProgramDetails = new ProgrammeDetailsBuilder().studyOption(1, "Full-time").toProgrammeDetails();
		applicationForm.setProgrammeDetails(unsavedProgramDetails);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, unsavedProgramDetails.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.programmeDetails.incomplete", mappingResult.getFieldError("programmeDetails").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfPersonalDetailsSectionMissing() {
		applicationForm.setPersonalDetails(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.personalDetails.incomplete", mappingResult.getFieldError("personalDetails").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfAdditionalInfoSectionMissing() {
		applicationForm.setAdditionalInformation(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.additionalInformation.incomplete", mappingResult.getFieldError("additionalInformation").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfPersonalDetailsSectionNotSaved() {
		applicationForm.setPersonalDetails(new PersonalDetails());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.personalDetails.incomplete", mappingResult.getFieldError("personalDetails").getCode());

	}

	@Test
	@DirtiesContext
	public void shouldRejectIfCurrentAddressIsMissing() {
		applicationForm.setCurrentAddress(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm.*");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.addresses.notempty", mappingResult.getFieldError("currentAddress").getCode());

	}

	@Test
	@DirtiesContext
	public void shouldRejectIfContactAddressIsMissing() {
		applicationForm.setContactAddress(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.addresses.notempty", mappingResult.getFieldError("contactAddress").getCode());

	}

	@Test
	@DirtiesContext
	public void shouldRejectIfFewerThanThreeReferees() {
		applicationForm.getReferees().remove(2);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.referees.notvalid", mappingResult.getFieldError("referees").getCode());

	}

	@Test
	@DirtiesContext
	public void shouldRejectIfPersonalStatementNotProvided() {
		applicationForm.setPersonalStatement(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("documents.section.invalid", mappingResult.getFieldError("personalStatement").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfStudyOptionDoesNotExistInTheProgrammeInstances() {
		ProgrammeDetails programmeDetail = applicationForm.getProgrammeDetails();
		programmeDetail.setStudyOption("Part-time");
		programmeDetail.setStudyOptionCode(31);
		BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(applicationForm, "programmeDetails.studyOption");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetail.getStudyOption())).andReturn(
				null);
		EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		EasyMock.verify(programInstanceDAOMock);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("programmeDetails.studyOption.invalid", mappingResult.getFieldError("programmeDetails.studyOption").getCode());

	}

	@SuppressWarnings("unchecked")
	@Test
	@DirtiesContext
	public void shouldRejectIfNoCurrentProgrammeInstancesExist() {
		ProgrammeDetails programmeDetail = applicationForm.getProgrammeDetails();
		programmeDetail.setStudyOption("Part-time");
        programmeDetail.setStudyOptionCode(31);
		BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(applicationForm, "program");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetail.getStudyOption())).andReturn(
				Collections.EMPTY_LIST);
		EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		EasyMock.verify(programInstanceDAOMock);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("application.program.invalid", mappingResult.getFieldError("program").getCode());

	}
	
	@Test
	@DirtiesContext
	public void shouldRejectIfNotAcceptedTheTerms() {
		applicationForm.setAcceptedTermsOnSubmission(CheckedStatus.NO);
		BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(applicationForm, "acceptedTermsOnSubmission");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		EasyMock.verify(programInstanceDAOMock);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("acceptedTermsOnSubmission").getCode());
		
	}
	
	@Before
	public void setup() throws ParseException {
		program = new ProgramBuilder().id(1).title("Program 1").toProgram();
		programInstance = new ProgramInstanceBuilder().id(1).studyOption(1, "Full-time")
				.applicationDeadline(new SimpleDateFormat("yyyy/MM/dd").parse("2030/08/06")).toProgramInstance();
		program.setInstances(Arrays.asList(programInstance));
		programmeDetails = new ProgrammeDetailsBuilder().studyOption(1, "Full-time").id(2).toProgrammeDetails();
		applicationForm = new ApplicationFormBuilder().program(program).programmeDetails(programmeDetails)
				.acceptedTerms(CheckedStatus.YES).personalDetails(new PersonalDetailsBuilder().id(1).toPersonalDetails())
				.additionalInformation(new AdditionalInformationBuilder().id(3).toAdditionalInformation())//
				.currentAddress(new AddressBuilder().address1("address").toAddress()).contactAddress(new AddressBuilder().address1("address").toAddress())//
				.referees(new Referee(), new Referee(), new Referee())//
				.personalStatement(new Document()).toApplicationForm();
	}
}