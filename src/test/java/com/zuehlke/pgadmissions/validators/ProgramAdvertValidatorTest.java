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

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ProgramAdvertValidatorTest {

    @Autowired
    private Validator validator;

    private ProgramOpportunityDTOValidator programAdvertValidator;
    private Advert programAdvert;

    @Before
    public void setUp() {
        programAdvert = new Advert();
        programAdvert.setDescription("abc");
        programAdvert.setStudyDuration(11);
        programAdvert.setFunding(null);
        programAdvert.setActive(true);
        programAdvertValidator = new ProgramOpportunityDTOValidator();
        programAdvertValidator.setValidator(validator);
    }

    @Test
    public void shouldSupportProgramAdvertClass() {
        assertTrue(programAdvertValidator.supports(Advert.class));
    }

    @Test
    public void shouldRejectIfDescriptionIsNull() {
        programAdvert.setDescription(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programAdvert, "advert");
        programAdvertValidator.validate(programAdvert, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("description").getCode());
    }

    @Test
    public void shouldRejectIfDescriptionIsEmptyString() {
        programAdvert.setDescription("");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programAdvert, "advert");
        programAdvertValidator.validate(programAdvert, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("description").getCode());
    }

    @Test
    public void shouldRejectIfDurationOfStudyHasErrorValue() {
        programAdvert.setStudyDuration(DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programAdvert, "advert");
        programAdvertValidator.validate(programAdvert, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("prospectus.durationOfStudy.emptyOrNotInteger", mappingResult.getFieldError("studyDuration").getCode());
    }

    @Test
    public void shouldRejectIfDurationOfStudyIsNull() {
        programAdvert.setStudyDuration(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programAdvert, "advert");
        programAdvertValidator.validate(programAdvert, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("prospectus.durationOfStudy.emptyOrNotInteger", mappingResult.getFieldError("studyDuration").getCode());
    }

    @Test
    public void shouldRejectIfIsCurrentlyAcceptingApplicationsIsNull() {
        programAdvert.setActive(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programAdvert, "advert");
        programAdvertValidator.validate(programAdvert, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("active").getCode());
    }

}
