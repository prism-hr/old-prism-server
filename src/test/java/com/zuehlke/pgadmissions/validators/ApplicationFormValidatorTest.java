package com.zuehlke.pgadmissions.validators;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
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

    private ProgramDetailsValidator programmeDetailsValidatorMock;

    private PersonalDetailsValidator personalDetailsValidatorMock;

    private AddressValidator addressValidatorMock;

    private AdditionalInformationValidator additionalInformationValidatorMock;

    private ProgramInstance programInstance;

    private ProgramDetails programmeDetails;

    private Program program;

    private PersonalDetails personalDetails;

    private AdditionalInformation additionalInformation;

    private Address contactAddress;

    private Address currentAddress;

    @Test
    public void shouldSupportAppForm() {
        reset(programmeDetailsValidatorMock, personalDetailsValidatorMock, addressValidatorMock, additionalInformationValidatorMock, programInstanceDAOMock);
        replay(programmeDetailsValidatorMock, personalDetailsValidatorMock, addressValidatorMock, additionalInformationValidatorMock, programInstanceDAOMock);
        
        assertTrue(applicationFormValidator.supports(ApplicationForm.class));
    }

    @Test
    public void shouldRejectIfProgrammeDetailsSectionNotSaved() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");

        EasyMock.reset(programmeDetailsValidatorMock);
        expect(programmeDetailsValidatorMock.isValid(programmeDetails)).andReturn(false);

        replay(programmeDetailsValidatorMock);
        applicationFormValidator.validate(applicationForm, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.programmeDetails.incomplete", mappingResult.getFieldError("programmeDetails").getCode());
    }

    @Test
    public void shouldRejectIfPersonalDetailsSectionMissing() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");

        EasyMock.reset(personalDetailsValidatorMock);
        expect(personalDetailsValidatorMock.isValid(personalDetails)).andReturn(false);

        replay(personalDetailsValidatorMock);
        applicationFormValidator.validate(applicationForm, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.personalDetails.incomplete", mappingResult.getFieldError("personalDetails").getCode());
    }

    @Test
    public void shouldRejectIfAdditionalInfoSectionMissing() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");

        EasyMock.reset(additionalInformationValidatorMock);
        expect(additionalInformationValidatorMock.isValid(additionalInformation)).andReturn(false);

        replay(additionalInformationValidatorMock);
        applicationFormValidator.validate(applicationForm, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.additionalInformation.incomplete", mappingResult.getFieldError("additionalInformation").getCode());
    }

    @Test
    public void shouldRejectIfCurrentAddressIsMissing() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm.*");

        EasyMock.reset(addressValidatorMock);
        expect(addressValidatorMock.isValid(currentAddress)).andReturn(false);
        expect(addressValidatorMock.isValid(contactAddress)).andReturn(true);

        replay(addressValidatorMock);
        applicationFormValidator.validate(applicationForm, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.addresses.notempty", mappingResult.getFieldError("currentAddress").getCode());

    }

    @Test
    public void shouldRejectIfContactAddressIsMissing() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");

        EasyMock.reset(addressValidatorMock);
        expect(addressValidatorMock.isValid(currentAddress)).andReturn(true);
        expect(addressValidatorMock.isValid(contactAddress)).andReturn(false);

        replay(addressValidatorMock);
        applicationFormValidator.validate(applicationForm, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.addresses.notempty", mappingResult.getFieldError("contactAddress").getCode());

    }

    @Test
    public void shouldRejectIfFewerThanThreeReferees() {
        applicationForm.getReferees().remove(2);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");

        applicationFormValidator.validate(applicationForm, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.referees.notvalid", mappingResult.getFieldError("referees").getCode());

    }

    @Test
    public void shouldRejectIfPersonalStatementNotProvided() {
        applicationForm.setPersonalStatement(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
        applicationFormValidator.validate(applicationForm, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("documents.section.invalid", mappingResult.getFieldError("personalStatement").getCode());
    }

    @Test
    public void shouldRejectIfStudyOptionDoesNotExistInTheProgrammeInstances() {
        ProgramDetails programmeDetail = applicationForm.getProgramDetails();
        programmeDetail.setStudyOption("Part-time");
        programmeDetail.setStudyOption("31");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(applicationForm, "programmeDetails.studyOption");

        EasyMock.reset(programInstanceDAOMock);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstancesByStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                null);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance));

        EasyMock.replay(programInstanceDAOMock);
        applicationFormValidator.validate(applicationForm, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("programmeDetails.studyOption.invalid", mappingResult.getFieldError("programmeDetails.studyOption").getCode());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRejectIfNoCurrentProgrammeInstancesExist() {
        ProgramDetails programmeDetail = applicationForm.getProgramDetails();
        programmeDetail.setStudyOption("Part-time");
        programmeDetail.setStudyOption("31");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(applicationForm, "program");

        EasyMock.reset(programInstanceDAOMock);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstancesByStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Collections.EMPTY_LIST);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Collections.EMPTY_LIST);

        EasyMock.replay(programInstanceDAOMock);
        applicationFormValidator.validate(applicationForm, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("application.program.invalid", mappingResult.getFieldError("program").getCode());

    }

    @Test
    public void shouldRejectIfNotAcceptedTheTerms() {
        applicationForm.setAcceptedTermsOnSubmission(false);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(applicationForm, "acceptedTermsOnSubmission");
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
        personalDetails = new PersonalDetailsBuilder().id(1).build();
        programmeDetails = new ProgrammeDetailsBuilder().studyOption("1", "Full-time").id(2).build();
        currentAddress = new AddressBuilder().address1("address").build();
        contactAddress = new AddressBuilder().address1("address").build();
        additionalInformation = new AdditionalInformationBuilder().id(3).build();
        applicationForm = new ApplicationFormBuilder().advert(program).programmeDetails(programmeDetails).acceptedTerms(true).personalDetails(personalDetails)
                .additionalInformation(additionalInformation)//
                .currentAddress(currentAddress).contactAddress(contactAddress)//
                .referees(new Referee(), new Referee(), new Referee())//
                .personalStatement(new Document()).build();

        programInstanceDAOMock = EasyMock.createMock(ProgramInstanceDAO.class);
        programmeDetailsValidatorMock = EasyMock.createMock(ProgramDetailsValidator.class);
        personalDetailsValidatorMock = EasyMock.createMock(PersonalDetailsValidator.class);
        addressValidatorMock = EasyMock.createMock(AddressValidator.class);
        additionalInformationValidatorMock = EasyMock.createMock(AdditionalInformationValidator.class);

        expect(programmeDetailsValidatorMock.isValid(programmeDetails)).andReturn(true);
        expect(personalDetailsValidatorMock.isValid(personalDetails)).andReturn(true);
        expect(addressValidatorMock.isValid(currentAddress)).andReturn(true);
        expect(addressValidatorMock.isValid(contactAddress)).andReturn(true);
        expect(additionalInformationValidatorMock.isValid(additionalInformation)).andReturn(true);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstancesByStudyOption(program, programmeDetails.getStudyOption())).andReturn(
                Arrays.asList(programInstance));

        applicationFormValidator = new ApplicationFormValidator(programInstanceDAOMock, programmeDetailsValidatorMock, personalDetailsValidatorMock,
                addressValidatorMock, additionalInformationValidatorMock);
        applicationFormValidator.setValidator((javax.validation.Validator) validator);

        replay(programmeDetailsValidatorMock, personalDetailsValidatorMock, addressValidatorMock, additionalInformationValidatorMock, programInstanceDAOMock);
    }

    @After
    public void verify() {
        EasyMock.verify(programmeDetailsValidatorMock, personalDetailsValidatorMock, addressValidatorMock, additionalInformationValidatorMock,
                programInstanceDAOMock);
    }
}