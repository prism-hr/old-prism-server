package com.zuehlke.pgadmissions.validators;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.joda.time.LocalDate;
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
import org.unitils.inject.util.InjectionUtils;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.builders.AdditionalInformationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.services.ProgramService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ApplicationFormValidatorTest {

    @Autowired
    private Validator validator;

    private ApplicationFormValidator applicationFormValidator;

    private Application application;

    private ProgramService programService;

    private ProgramDetailsValidator programDetailsValidatorMock;

    private PersonalDetailsValidator personalDetailsValidatorMock;

    private ApplicationAddressValidator applicationFormAddressValidatorMock;

    private AdditionalInformationValidator additionalInformationValidatorMock;

    private ApplicationFormDocumentValidator applicationFormDocumentValidatorMock;

    private ProgramInstance programInstance;

    private ProgramDetails programmeDetails;

    private Program program;

    private PersonalDetails personalDetails;

    private AdditionalInformation additionalInformation;

    private ApplicationAddress address;

    private ApplicationDocument document;

    @Test
    public void shouldSupportAppForm() {
        reset(programService, programDetailsValidatorMock, personalDetailsValidatorMock, applicationFormAddressValidatorMock,
                additionalInformationValidatorMock, applicationFormDocumentValidatorMock);
        replay(programService, programDetailsValidatorMock, personalDetailsValidatorMock, applicationFormAddressValidatorMock,
                additionalInformationValidatorMock, applicationFormDocumentValidatorMock);

        assertTrue(applicationFormValidator.supports(Application.class));
    }

    @Test
    public void shouldRejectIfProgrammeDetailsSectionNotSaved() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(application, "applicationForm");

        EasyMock.reset(programDetailsValidatorMock);
        expect(programDetailsValidatorMock.isValid(programmeDetails)).andReturn(false);

        replay(programDetailsValidatorMock);
        applicationFormValidator.validate(application, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.programDetails.incomplete", mappingResult.getFieldError("programDetails").getCode());
    }

    @Test
    public void shouldRejectIfPersonalDetailsSectionMissing() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(application, "applicationForm");

        EasyMock.reset(personalDetailsValidatorMock);
        expect(personalDetailsValidatorMock.isValid(personalDetails)).andReturn(false);

        replay(personalDetailsValidatorMock);
        applicationFormValidator.validate(application, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.personalDetails.incomplete", mappingResult.getFieldError("personalDetails").getCode());
    }

    @Test
    public void shouldRejectIfAdditionalInfoSectionMissing() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(application, "applicationForm");

        EasyMock.reset(additionalInformationValidatorMock);
        expect(additionalInformationValidatorMock.isValid(additionalInformation)).andReturn(false);

        replay(additionalInformationValidatorMock);
        applicationFormValidator.validate(application, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.additionalInformation.incomplete", mappingResult.getFieldError("additionalInformation").getCode());
    }

    @Test
    public void shouldRejectIfAddressIsMissing() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(application, "applicationForm.*");

        EasyMock.reset(applicationFormAddressValidatorMock);
        expect(applicationFormAddressValidatorMock.isValid(address)).andReturn(false);

        replay(applicationFormAddressValidatorMock);
        applicationFormValidator.validate(application, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.addresses.notempty", mappingResult.getFieldError("applicationAddress").getCode());

    }

    @Test
    public void shouldRejectIfFewerThanThreeReferees() {
        application.getReferees().remove(2);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(application, "applicationForm");

        applicationFormValidator.validate(application, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.referees.notvalid", mappingResult.getFieldError("referees").getCode());

    }

    @Test
    public void shouldRejectIfStudyOptionDoesNotExistInTheProgrammeInstances() {
        StudyOption studyOption = new StudyOption("dupa", "jasia");
        ProgramDetails programmeDetail = application.getProgramDetails();
        programmeDetail.setStudyOption(studyOption);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(application, "application");

        EasyMock.reset(programService);
        EasyMock.expect(programService.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Collections.<ProgramInstance> emptyList());

        EasyMock.replay(programService);
        applicationFormValidator.validate(application, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("programDetails.studyOption.invalid", mappingResult.getFieldError("programDetails.studyOption").getCode());

    }

    @Test
    public void shouldRejectIfNotAcceptedTheTerms() {
        application.setAcceptedTerms(false);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(application, "application");
        applicationFormValidator.validate(application, mappingResult);
        EasyMock.verify(programService);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("acceptedTerms").getCode());

    }

    @Before
    public void setup() throws ParseException {
        program = new Program().withState(new State().withId(PrismState.PROGRAM_APPROVED));
        programInstance = new ProgramInstance().withStudyOption("1", "Full-time").withApplicationDeadline(new LocalDate(2030, 8, 6));
        program.getInstances().addAll(Arrays.asList(programInstance));
        personalDetails = new PersonalDetails();
        programmeDetails = new ProgrammeDetailsBuilder().studyOption(new StudyOption("1", "Full-time")).build();
        address = new ApplicationAddress();
        additionalInformation = new AdditionalInformationBuilder().build();
        document = new ApplicationDocument().withPersonalStatement(new Document());

        application = new Application() //
                .withProgram(program) //
                .withPersonalDetails(personalDetails) //
                .withProgramDetails(programmeDetails) //
                .withAddress(address) //
                .withAdditionalInformation(additionalInformation)//
                .withReferees(new Referee(), new Referee(), new Referee())//
                .withDocument(document) //
                .withAcceptedTerms(true);

        programService = EasyMock.createMock(ProgramService.class);
        programDetailsValidatorMock = EasyMock.createMock(ProgramDetailsValidator.class);
        personalDetailsValidatorMock = EasyMock.createMock(PersonalDetailsValidator.class);
        applicationFormAddressValidatorMock = EasyMock.createMock(ApplicationAddressValidator.class);
        additionalInformationValidatorMock = EasyMock.createMock(AdditionalInformationValidator.class);
        applicationFormDocumentValidatorMock = EasyMock.createMock(ApplicationFormDocumentValidator.class);

        expect(programDetailsValidatorMock.isValid(programmeDetails)).andReturn(true);
        expect(personalDetailsValidatorMock.isValid(personalDetails)).andReturn(true);
        expect(applicationFormAddressValidatorMock.isValid(address)).andReturn(true);
        expect(additionalInformationValidatorMock.isValid(additionalInformation)).andReturn(true);
        expect(applicationFormDocumentValidatorMock.isValid(document)).andReturn(true);
        EasyMock.expect(programService.getActiveProgramInstancesForStudyOption(program, programmeDetails.getStudyOption())).andReturn(
                Arrays.asList(programInstance));

        applicationFormValidator = new ApplicationFormValidator();
        InjectionUtils.injectInto(programService, applicationFormValidator, "programService");
        InjectionUtils.injectInto(programDetailsValidatorMock, applicationFormValidator, "programDetailsValidator");
        InjectionUtils.injectInto(personalDetailsValidatorMock, applicationFormValidator, "personalDetailsValidator");
        InjectionUtils.injectInto(applicationFormAddressValidatorMock, applicationFormValidator, "applicationFormAddressValidator");
        InjectionUtils.injectInto(additionalInformationValidatorMock, applicationFormValidator, "additionalInformationValidator");
        InjectionUtils.injectInto(applicationFormDocumentValidatorMock, applicationFormValidator, "applicationFormDocumentValidator");
        applicationFormValidator.setValidator((javax.validation.Validator) validator);

        replay(programService, programDetailsValidatorMock, personalDetailsValidatorMock, applicationFormAddressValidatorMock,
                additionalInformationValidatorMock, applicationFormDocumentValidatorMock);
    }

    @After
    public void verify() {
        EasyMock.verify(programService, programDetailsValidatorMock, personalDetailsValidatorMock, applicationFormAddressValidatorMock,
                additionalInformationValidatorMock, applicationFormDocumentValidatorMock);
    }
}