package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import javax.validation.Validator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.ProgramAdvert;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ProgramAdvertValidatorTest {

    @Autowired
    private Validator validator;

    private ProgramAdvertValidator programAdvertValidator;
    private ProgramAdvert programAdvert;

    @Before
    public void setUp() {
        programAdvert = new ProgramAdvert();
        programAdvert.setDescription("abc");
        programAdvert.setDurationOfStudyInMonth(11);
        programAdvert.setFundingInformation("");
        programAdvert.setIsCurrentlyAcceptingApplications(true);
        programAdvertValidator = new ProgramAdvertValidator();
        programAdvertValidator.setValidator(validator);
    }

    @Test
    public void shouldSupportProgramAdvertClass() {
        assertTrue(programAdvertValidator.supports(ProgramAdvert.class));
    }

    @Test
    public void shouldRejectIfDescriptionIsNull() {
        programAdvert.setDescription(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programAdvert, "description");
        programAdvertValidator.validate(programAdvert, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("description").getCode());
    }

    @Test
    public void shouldRejectIfDescriptionIsEmptyString() {
        programAdvert.setDescription("");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programAdvert, "description");
        programAdvertValidator.validate(programAdvert, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("description").getCode());
    }

    @Test
    public void shouldRejectIfDurationOfStudyHasErrorValue() {
        programAdvert.setDurationOfStudyInMonth(DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programAdvert, "durationOfStudyInMonth");
        programAdvertValidator.validate(programAdvert, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("prospectus.durationOfStudy.emptyOrNotInteger", mappingResult.getFieldError("durationOfStudyInMonth").getCode());
    }

    @Test
    public void shouldRejectIfDurationOfStudyIsNull() {
        programAdvert.setDurationOfStudyInMonth(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programAdvert, "durationOfStudyInMonth");
        programAdvertValidator.validate(programAdvert, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("prospectus.durationOfStudy.emptyOrNotInteger", mappingResult.getFieldError("durationOfStudyInMonth").getCode());
    }

    @Test
    public void shouldRejectIfIsCurrentlyAcceptingApplicationsIsNull() {
        programAdvert.setIsCurrentlyAcceptingApplications(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programAdvert, "isCurrentlyAcceptingApplications");
        programAdvertValidator.validate(programAdvert, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("isCurrentlyAcceptingApplications").getCode());
    }

}
