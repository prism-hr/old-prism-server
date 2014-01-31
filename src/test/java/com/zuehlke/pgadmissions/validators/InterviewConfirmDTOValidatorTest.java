package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.InterviewConfirmDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class InterviewConfirmDTOValidatorTest {

    @Autowired
    private Validator validator;

    private InterviewConfirmDTO dto;

    private InterviewConfirmDTOValidator dtoValidator;

    @Test
    public void shouldSupportInterviewConfirmDTO() {
        assertTrue(dtoValidator.supports(InterviewConfirmDTO.class));
    }

    @Test
    public void shouldAcceptDto() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(dto, "dto");

        dtoValidator.validate(dto, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldRejectDtoIfNotTimeslotIdProvided() {
        dto.setTimeslotId(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(dto, "dto");

        dtoValidator.validate(dto, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("timeslotId").getCode());
    }

    @Before
    public void setup() {
        dto = new InterviewConfirmDTO();
        dto.setFurtherDetails("aa");
        dto.setFurtherInterviewerDetails("bb");
        dto.setTimeslotId(2);
        dtoValidator = new InterviewConfirmDTOValidator();
        dtoValidator.setValidator((javax.validation.Validator) validator);
    }
}
