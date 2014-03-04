package com.zuehlke.pgadmissions.validators;

import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ApplicationFormValidatorTest {

    @Autowired  
    private Validator validator; 
    
	private ApplicationFormValidator applicationFormValidator;
    
	private ApplicationForm applicationForm;
	
	private ProgramInstanceDAO programInstanceDAOMock;
	
	private ProgrammeDetailsValidator programmeDetailsValidatorMock;
	
	private ProgramInstance programInstance;
	
	private ProgrammeDetails programmeDetails;
	
	private Program program;

	@Test
	public void shouldSupportAppForm() {
		assertTrue(applicationFormValidator.supports(ApplicationForm.class));
	}

	@Test
	@Ignore
	public void shouldRejectIfProgrammeDetailsSectionMissing() {
		applicationForm.setProgrammeDetails(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");

		applicationFormValidator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.programmeDetails.incomplete", mappingResult.getFieldError("programmeDetails").getCode());
	}

	@Test
	public void shouldRejectIfProgrammeDetailsSectionNotSaved() {
		ProgrammeDetails unsavedProgramDetails = new ProgrammeDetailsBuilder().studyOption("1", "Full-time").build();
		applicationForm.setProgrammeDetails(unsavedProgramDetails);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, unsavedProgramDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		applicationFormValidator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.programmeDetails.incomplete", mappingResult.getFieldError("programmeDetails").getCode());
	}

	@Test
	@Ignore
	public void shouldRejectIfPersonalDetailsSectionMissing() {
		applicationForm.setPersonalDetails(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		applicationFormValidator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.personalDetails.incomplete", mappingResult.getFieldError("personalDetails").getCode());
	}

	@Test
	@Ignore
	public void shouldRejectIfAdditionalInfoSectionMissing() {
		applicationForm.setAdditionalInformation(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		applicationFormValidator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.additionalInformation.incomplete", mappingResult.getFieldError("additionalInformation").getCode());
	}

	@Test
	public void shouldRejectIfPersonalDetailsSectionNotSaved() {
		applicationForm.setPersonalDetails(new PersonalDetails());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		applicationFormValidator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.personalDetails.incomplete", mappingResult.getFieldError("personalDetails").getCode());

	}

	@Test
	public void shouldRejectIfCurrentAddressIsMissing() {
		applicationForm.setCurrentAddress(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm.*");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		applicationFormValidator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.addresses.notempty", mappingResult.getFieldError("currentAddress").getCode());

	}

	@Test
	public void shouldRejectIfContactAddressIsMissing() {
		applicationForm.setContactAddress(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		applicationFormValidator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.addresses.notempty", mappingResult.getFieldError("contactAddress").getCode());

	}

	@Test
	public void shouldRejectIfFewerThanThreeReferees() {
		applicationForm.getReferees().remove(2);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		applicationFormValidator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.referees.notvalid", mappingResult.getFieldError("referees").getCode());

	}

	@Test
	public void shouldRejectIfPersonalStatementNotProvided() {
		applicationForm.setPersonalStatement(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		applicationFormValidator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("documents.section.invalid", mappingResult.getFieldError("personalStatement").getCode());
	}

	@Test
	public void shouldRejectIfStudyOptionDoesNotExistInTheProgrammeInstances() {
		ProgrammeDetails programmeDetail = applicationForm.getProgrammeDetails();
		programmeDetail.setStudyOption("Part-time");
		programmeDetail.setStudyOptionCode("31");
		BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(applicationForm, "programmeDetails.studyOption");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetail.getStudyOption())).andReturn(null);
		EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		applicationFormValidator.validate(applicationForm, mappingResult);
		EasyMock.verify(programInstanceDAOMock);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("programmeDetails.studyOption.invalid", mappingResult.getFieldError("programmeDetails.studyOption").getCode());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldRejectIfNoCurrentProgrammeInstancesExist() {
		ProgrammeDetails programmeDetail = applicationForm.getProgrammeDetails();
		programmeDetail.setStudyOption("Part-time");
        programmeDetail.setStudyOptionCode("31");
		BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(applicationForm, "program");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetail.getStudyOption())).andReturn(Collections.EMPTY_LIST);
		EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(programInstanceDAOMock);
		applicationFormValidator.validate(applicationForm, mappingResult);
		EasyMock.verify(programInstanceDAOMock);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("application.program.invalid", mappingResult.getFieldError("program").getCode());

	}
	
	@Test
	public void shouldRejectIfNotAcceptedTheTerms() {
		applicationForm.setAcceptedTermsOnSubmission(false);
		BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(applicationForm, "acceptedTermsOnSubmission");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		applicationFormValidator.validate(applicationForm, mappingResult);
		EasyMock.verify(programInstanceDAOMock);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("acceptedTermsOnSubmission").getCode());
		
	}
	
	@Before
	public void setup() throws ParseException {
		program = new ProgramBuilder().id(1).title("Program 1").build();
		programInstance = new ProgramInstanceBuilder().id(1).studyOption("1", "Full-time")
				.applicationDeadline(new SimpleDateFormat("yyyy/MM/dd").parse("2030/08/06")).build();
		program.setInstances(Arrays.asList(programInstance));
		programmeDetails = new ProgrammeDetailsBuilder().studyOption("1", "Full-time").id(2).build();
		applicationForm = new ApplicationFormBuilder().advert(program).programmeDetails(programmeDetails)
				.acceptedTerms(true).personalDetails(new PersonalDetailsBuilder().id(1).build())
				.additionalInformation(new AdditionalInformationBuilder().id(3).build())//
				.currentAddress(new AddressBuilder().address1("address").build()).contactAddress(new AddressBuilder().address1("address").build())//
				.referees(new Referee(), new Referee(), new Referee())//
				.personalStatement(new Document()).build();
		
		programInstanceDAOMock = EasyMock.createMock(ProgramInstanceDAO.class);
		programmeDetailsValidatorMock = EasyMock.createMock(ProgrammeDetailsValidator.class);

		programmeDetailsValidatorMock.validate(isA(Object.class), isA(Errors.class));
		
		applicationFormValidator = new ApplicationFormValidator(programInstanceDAOMock, programmeDetailsValidatorMock);
		applicationFormValidator.setValidator((javax.validation.Validator) validator);
	}
}