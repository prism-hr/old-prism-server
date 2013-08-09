package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class EmploymentPositionValidatorTest {

    private EmploymentPosition position;

    @Autowired
    private Validator validator;

    private EmploymentPositionValidator positionValidator;

    @Test
    public void shouldSupportEmploymentPosition() {
        assertTrue(positionValidator.supports(EmploymentPosition.class));
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
        Date tomorrow, dayAfterTomorrow;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        tomorrow = calendar.getTime();
        calendar.add(Calendar.DATE, 2);
        dayAfterTomorrow = calendar.getTime();
        position.setStartDate(tomorrow);
        position.setEndDate(dayAfterTomorrow);
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
        position.setStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
        position.setEndDate(new SimpleDateFormat("yyyy/MM/dd").parse("2009/08/06"));
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
        position = new EmploymentPosition();
        position.setApplication(new ApplicationFormBuilder().id(4).build());
        position.setEmployerName("Mark");
        position.setEndDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
        position.setRemit("cashier");
        position.setCurrent(false);
        position.setStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/06"));
        position.setPosition("head of department");
        position.setEmployerAddress(new AddressBuilder().address1("address").address3("address3").domicile(new DomicileBuilder().id(1).build()).build());

        positionValidator = new EmploymentPositionValidator();
        positionValidator.setValidator((javax.validation.Validator) validator);
    }
}
