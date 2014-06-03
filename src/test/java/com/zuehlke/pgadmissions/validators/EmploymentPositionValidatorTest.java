package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class EmploymentPositionValidatorTest {

    private ApplicationEmploymentPosition position;

    @Autowired
    private Validator validator;

    private EmploymentPositionValidator positionValidator;

    @Test
    public void shouldSupportEmploymentPosition() {
        assertTrue(positionValidator.supports(ApplicationEmploymentPosition.class));
    }

    @Test
    public void shouldRejectIfEmployerAddressIsEmpty() {
        position.setEmployerAddress(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
        positionValidator.validate(position, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("employerAddress").getCode());
    }

    @Test
    public void shouldRejectIfEmployerNameIsEmpty() {
        position.setEmployerName(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
        positionValidator.validate(position, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("employerName").getCode());
    }

    @Test
    public void shouldRejectIfStartDateAndEndDateAreFutureDates() {
        position.setStartDate(new LocalDate().plusDays(1));
        position.setEndDate(new LocalDate().plusDays(2));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
        positionValidator.validate(position, mappingResult);
        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notpast", mappingResult.getFieldError("startDate").getCode());
        Assert.assertEquals("date.field.notpast", mappingResult.getFieldError("endDate").getCode());
    }

    @Test
    public void shouldRejectIfStartDateIsEmpty() {
        position.setStartDate(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
        positionValidator.validate(position, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfEndDateIsNotSetForCompletedEmploymentPosition() {
        position.setEndDate(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
        positionValidator.validate(position, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("endDate").getCode());
    }

    @Test
    public void shouldRejectIfRemitIsEmpty() {
        position.setRemit(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
        positionValidator.validate(position, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("remit").getCode());
    }

    @Test
    public void shouldRejectIfPositionIsEmpty() {
        position.setPosition(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
        positionValidator.validate(position, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("position").getCode());
    }

    @Test
    public void shouldRejectIfStartDateIsAfterEndDate() throws ParseException {
        position.setStartDate(new LocalDate(2010, 8, 6));
        position.setEndDate(new LocalDate(2009, 8, 6));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
        positionValidator.validate(position, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("position.position_startDate.notvalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfJobDescriptionTooLong() {
        StringBuilder jobDescription = new StringBuilder();
        for (int i = 0; i <= 2000; i++) {
            jobDescription.append("a");
        }

        position.setRemit(jobDescription.toString());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(position, "position");
        positionValidator.validate(position, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
    }

    @Before
    public void setup() throws ParseException {
        position = new ApplicationEmploymentPosition();
        position.setApplication(new ApplicationFormBuilder().id(4).build());
        position.setEmployerName("Mark");
        position.setEndDate(new LocalDate(2010, 8, 6));
        position.setRemit("cashier");
        position.setCurrent(false);
        position.setStartDate(new LocalDate(2010, 8, 6));
        position.setPosition("head of department");
        position.setEmployerAddress(new Address().withLine1("address").withTown("address3").withDomicile(new Domicile().withId(1)));

        positionValidator = new EmploymentPositionValidator();
        positionValidator.setValidator((javax.validation.Validator) validator);
    }
}
