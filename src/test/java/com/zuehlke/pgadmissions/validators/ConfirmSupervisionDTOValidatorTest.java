package com.zuehlke.pgadmissions.validators;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ConfirmSupervisionDTOValidatorTest {

    @Autowired  
    private Validator validator;  
        
    private ConfirmSupervisionDTOValidator confirmSupervisionDTOValidator;

    private ConfirmSupervisionDTO dto;
    
    @Before
    public void prepare() {
        dto = new ConfirmSupervisionDTO();
        confirmSupervisionDTOValidator = new ConfirmSupervisionDTOValidator();
        confirmSupervisionDTOValidator.setValidator((javax.validation.Validator) validator);
    }
        
    @Test
    public void shouldRejectIfConfirmedOrDeclinedHasNotBeenSelected() {
        dto.setConfirmedSupervision(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(dto, "confirmSupervisionDTO");
        confirmSupervisionDTOValidator.validate(dto, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("confirmedSupervision").getCode());
    }
    
    @Test
    public void shouldRejectIfDeclinedAndReasonIsEmpty() {
        dto.setConfirmedSupervision(false);
        BindingResult mappingResult = new BeanPropertyBindingResult(dto, "confirmSupervisionDTO");
        confirmSupervisionDTOValidator.validate(dto, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("declinedSupervisionReason").getCode());
    }
    
    @Test
    public void shouldRejectIfConfirmedAndProjectTitleAndAbstractAreMissing() {
        dto.setConfirmedSupervision(true);
        dto.setRecommendedStartDate(new LocalDate().plusDays(5));
        dto.setRecommendedConditionsAvailable(false);
        
        BindingResult mappingResult = new BeanPropertyBindingResult(dto, "confirmSupervisionDTO");
        confirmSupervisionDTOValidator.validate(dto, mappingResult);
        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("projectTitle").getCode());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("projectAbstract").getCode());
    }
    
    @Test
    public void shouldRejectIfConfirmedAndRecommendedStartDateIsMissing() {
        dto.setConfirmedSupervision(true);
        dto.setProjectAbstract("foo");
        dto.setProjectTitle("bar");
        dto.setRecommendedConditionsAvailable(false);
        
        BindingResult mappingResult = new BeanPropertyBindingResult(dto, "confirmSupervisionDTO");
        confirmSupervisionDTOValidator.validate(dto, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("recommendedStartDate").getCode());
    }
    
    @Test
    public void shouldRejectIfConfirmedAndRecommendedStartDateIsInThePast() {
        dto.setConfirmedSupervision(true);
        dto.setProjectAbstract("foo");
        dto.setProjectTitle("bar");
        dto.setRecommendedConditionsAvailable(false);
        dto.setRecommendedStartDate(new LocalDate().minusDays(5));
        
        BindingResult mappingResult = new BeanPropertyBindingResult(dto, "confirmSupervisionDTO");
        confirmSupervisionDTOValidator.validate(dto, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notfuture", mappingResult.getFieldError("recommendedStartDate").getCode());
    }
    
    @Test
    public void shouldRejectIfConfirmedConditionsAvailableButEmpty() {
        dto.setConfirmedSupervision(true);
        dto.setProjectAbstract("foo");
        dto.setProjectTitle("bar");
        dto.setRecommendedStartDate(new LocalDate().plusDays(5));
        dto.setRecommendedConditionsAvailable(true);
        
        BindingResult mappingResult = new BeanPropertyBindingResult(dto, "confirmSupervisionDTO");
        confirmSupervisionDTOValidator.validate(dto, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("recommendedConditions").getCode());
    }
    
    @Test
    public void shouldAcceptIfMandatoryFieldsHaveBeenEntered() {
        dto.setConfirmedSupervision(true);
        dto.setProjectAbstract("foo");
        dto.setProjectTitle("bar");
        dto.setRecommendedStartDate(new LocalDate().plusDays(5));
        dto.setRecommendedConditionsAvailable(true);
        dto.setRecommendedConditions("fooBar");
        
        BindingResult mappingResult = new BeanPropertyBindingResult(dto, "confirmSupervisionDTO");
        confirmSupervisionDTOValidator.validate(dto, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }
}
