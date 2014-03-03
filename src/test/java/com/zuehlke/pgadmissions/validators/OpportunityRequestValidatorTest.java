package com.zuehlke.pgadmissions.validators;

import static com.zuehlke.pgadmissions.validators.AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.assertFalse;

import javax.validation.Validator;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Errors;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramFeed;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class OpportunityRequestValidatorTest {

    @Autowired
    private Validator validator;

    private RegisterFormValidator registerFormValidatorMock;

    private ProgramInstanceService programInstanceServiceMock;

    private FullTextSearchService fullTextSearchServiceMock;

    private OpportunityRequestValidator opportunityRequestValidator;

    private OpportunityRequest opportunityRequest;

    private RegisteredUser author;

    @Before
    public void setUp() {
        Domicile institutionCountry = new DomicileBuilder().code("PL").build();
        author = new RegisteredUser();
        opportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(author, institutionCountry).build();

        registerFormValidatorMock = EasyMock.createMock(RegisterFormValidator.class);
        programInstanceServiceMock = EasyMock.createMock(ProgramInstanceService.class);
        fullTextSearchServiceMock = EasyMock.createMock(FullTextSearchService.class);

        expect(programInstanceServiceMock.getFirstProgramInstanceStartYear(isA(DateTime.class))).andReturn(2013);
        replay(programInstanceServiceMock);

        opportunityRequestValidator = new OpportunityRequestValidator();
        opportunityRequestValidator.setValidator(validator);
        opportunityRequestValidator.setRegisterFormValidator(registerFormValidatorMock);
        opportunityRequestValidator.setProgramInstanceService(programInstanceServiceMock);
        opportunityRequestValidator.setFullTextSearchService(fullTextSearchServiceMock);
    }

    @Test
    public void shouldSupportProgramAdvertClass() {
        assertTrue(opportunityRequestValidator.supports(OpportunityRequest.class));
    }

    @Test
    public void shouldRejectIfInstitutionCountryIsNull() {
        opportunityRequest.setInstitutionCountry(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals(EMPTY_DROPDOWN_ERROR_MESSAGE, mappingResult.getFieldError("institutionCountry").getCode());
    }

    @Test
    public void shouldRejectIfInstitutionCodeIsNull() {
        opportunityRequest.setInstitutionCode(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals(AbstractValidator.EMPTY_FIELD_ERROR_MESSAGE, mappingResult.getFieldError("institutionCode").getCode());
    }

    @Test
    public void shouldRejectIfProgramDescriptionIsNull() {
        opportunityRequest.setProgramDescription(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals(AbstractValidator.EMPTY_FIELD_ERROR_MESSAGE, mappingResult.getFieldError("programDescription").getCode());
    }

    @Test
    public void shouldRejectIfAtasRequiredIsEmpty() {
        opportunityRequest.setAtasRequired(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals(AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE, mappingResult.getFieldError("atasRequired").getCode());
    }

    @Test
    public void shouldRejectIfOtherInstitutionIsNullWhenOtherInstitutionChosen() {
        opportunityRequest.setInstitutionCode("OTHER");
        opportunityRequest.setOtherInstitution(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals(AbstractValidator.EMPTY_FIELD_ERROR_MESSAGE, mappingResult.getFieldError("otherInstitution").getCode());
    }

    @Test
    public void shouldRejectIfOtherInstitutionIsSpecifiedAndSimilarOneExist() {
        opportunityRequest.setInstitutionCode("OTHER");
        opportunityRequest.setOtherInstitution("instytucja");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);

        expect(fullTextSearchServiceMock.getMatchingInstitutions("instytucja", "PL")).andReturn(Lists.newArrayList("similar", "similar2"));

        EasyMock.replay(fullTextSearchServiceMock);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);
        EasyMock.verify(fullTextSearchServiceMock);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals("institution.did.you.mean", mappingResult.getFieldError("otherInstitution").getCode());
        assertEquals("similar::similar2", mappingResult.getFieldError("otherInstitution").getDefaultMessage());
    }

    @Test
    public void shouldNotRejectIfSimilarInstitutionExistButForceFlagIsSetToTrue() {
        opportunityRequest.setInstitutionCode("OTHER");
        opportunityRequest.setOtherInstitution("instytucja");
        opportunityRequest.setForceCreatingNewInstitution(true);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldNotRejectIfStudyDurationIfFieldHasAlreadyAnError() {
        opportunityRequest.setStudyDurationNumber(null);
        opportunityRequest.setStudyDurationUnit(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");
        mappingResult.rejectValue("studyDurationNumber", "number.format.error");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals("number.format.error", mappingResult.getFieldError("studyDurationNumber").getCode());
    }

    @Test
    public void shouldRejectIfStudyDurationNumberIsEmpty() {
        opportunityRequest.setStudyDurationNumber(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals(AbstractValidator.EMPTY_FIELD_ERROR_MESSAGE, mappingResult.getFieldError("studyDurationNumber").getCode());
    }

    @Test
    public void shouldRejectIfStudyDurationNumberIsLessThan1() {
        opportunityRequest.setStudyDurationNumber(0);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals("Min", mappingResult.getFieldError("studyDurationNumber").getCode());
    }

    @Test
    public void shouldRejectIfStudyDurationUnitIsEmpty() {
        opportunityRequest.setStudyDurationUnit(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals(AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE, mappingResult.getFieldError("studyDurationUnit").getCode());
    }

    @Test
    public void shouldRejectIfStudyOptionsAreEmpty() {
        opportunityRequest.setStudyOptions(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals(AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE, mappingResult.getFieldError("studyOptions").getCode());
    }

    @Test
    public void shouldRejectIfProgramTypeIsEmpty() {
        opportunityRequest.setProgramType(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals(AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE, mappingResult.getFieldError("programType").getCode());
    }

    @Test
    public void shouldRejectIfProgramTitleIsNull() {
        opportunityRequest.setProgramTitle(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals(AbstractValidator.EMPTY_FIELD_ERROR_MESSAGE, mappingResult.getFieldError("programTitle").getCode());
    }

    @Test
    public void shouldRejectIfAdvertisingDeadlineYearIsEmpty() {
        opportunityRequest.setAdvertisingDeadlineYear(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals(AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE, mappingResult.getFieldError("advertisingDeadlineYear").getCode());
    }

    @Test
    public void shouldRejectIfAdvertisingDeadlineYearIsInThePast() {
        opportunityRequest.setAdvertisingDeadlineYear(new DateTime().minusYears(1).getYear());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals("Min", mappingResult.getFieldError("advertisingDeadlineYear").getCode());
    }

    @Test
    public void shouldRejectIfAdvertisingDurationIsLessThan5() {
        opportunityRequest.setAdvertisingDeadlineYear(new DateTime().plusYears(11).getYear());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertEquals(1, mappingResult.getErrorCount());
        assertEquals("Max", mappingResult.getFieldError("advertisingDeadlineYear").getCode());
    }

    @Test
    public void shouldIgnoreSomeFieldsIfValidatingRequestForBuiltInProgram() {
        opportunityRequest.setSourceProgram(new ProgramBuilder().programFeed(new ProgramFeed()).build());
        opportunityRequest.setProgramTitle(null);
        opportunityRequest.setAuthor(null);
        opportunityRequest.setStudyOptions(null);
        opportunityRequest.setAdvertisingDeadlineYear(null);

        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertFalse(mappingResult.hasErrors());
    }

    @Test
    public void shouldIgnoreSomeFieldsIfValidatingRequestForNewProgram() {
        opportunityRequest.setSourceProgram(new Program());
        opportunityRequest.setProgramTitle(null);
        opportunityRequest.setAuthor(null);

        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        assertFalse(mappingResult.hasErrors());
    }

    @Test
    public void shouldFindNoErrorsAndInvokeRegisterFormValidator() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        configureAndReplayRegisterFormValidator(mappingResult);
        opportunityRequestValidator.validate(opportunityRequest, mappingResult);

        verify(registerFormValidatorMock);
        assertEquals(0, mappingResult.getErrorCount());
    }

    private void configureAndReplayRegisterFormValidator(Errors errors) {
        expect(registerFormValidatorMock.supports(RegisteredUser.class)).andReturn(true);
        registerFormValidatorMock.validate(author, errors);

        replay(registerFormValidatorMock);
    }

}
