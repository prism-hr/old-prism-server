package com.zuehlke.pgadmissions.validators;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertTrue;

import javax.validation.Validator;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.dto.ProgramOpportunityDTO;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramsService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ProgramOpportunityDTOValidatorTest {

    @Autowired
    private Validator validator;

    private ProgramsService programsServiceMock;

    private ProgramInstanceService programInstanceServiceMock;

    private ProgramOpportunityDTOValidator programOpportunityDTOValidator;

    private ProgramOpportunityDTO programOpportunityDTO;

    private Program program;

    @Before
    public void setUp() {
        programOpportunityDTO = new ProgramOpportunityDTO();
        programOpportunityDTO.setProgramCode("code");
        programOpportunityDTO.setDescription("desc");
        programOpportunityDTO.setStudyDuration(23);
        programOpportunityDTO.setFunding("funding");
        programOpportunityDTO.setActive(true);
        programOpportunityDTO.setStudyOptions(Lists.newArrayList("F", "P"));
        programOpportunityDTO.setAdvertiseDeadlineYear(2084);
        program = new Program();

        programsServiceMock = EasyMock.createMock(ProgramsService.class);
        programInstanceServiceMock = EasyMock.createMock(ProgramInstanceService.class);

        expect(programsServiceMock.getProgramByCode("code")).andReturn(program);
        expect(programInstanceServiceMock.getPossibleAdvertisingDeadlineYears()).andReturn(Lists.newArrayList(2083, 2084, 2085));

        replay(programsServiceMock, programInstanceServiceMock);

        programOpportunityDTOValidator = new ProgramOpportunityDTOValidator();
        programOpportunityDTOValidator.setValidator(validator);
        programOpportunityDTOValidator.setProgramsService(programsServiceMock);
        programOpportunityDTOValidator.setProgramInstanceService(programInstanceServiceMock);
    }

    @Test
    public void shouldSupportprogramOpportunityDTOClass() {
        assertTrue(programOpportunityDTOValidator.supports(ProgramOpportunityDTO.class));
    }

    @Test
    public void shouldRejectIfDescriptionIsNull() {
        programOpportunityDTO.setDescription(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programOpportunityDTO, "advert");
        programOpportunityDTOValidator.validate(programOpportunityDTO, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("description").getCode());
    }

    @Test
    public void shouldRejectIfDescriptionIsEmptyString() {
        programOpportunityDTO.setDescription("");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programOpportunityDTO, "advert");
        programOpportunityDTOValidator.validate(programOpportunityDTO, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("description").getCode());
    }

    @Test
    public void shouldRejectIfDurationOfStudyHasErrorValue() {
        programOpportunityDTO.setStudyDuration(DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programOpportunityDTO, "advert");
        programOpportunityDTOValidator.validate(programOpportunityDTO, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("prospectus.durationOfStudy.emptyOrNotInteger", mappingResult.getFieldError("studyDuration").getCode());
    }

    @Test
    public void shouldRejectIfDurationOfStudyIsNull() {
        programOpportunityDTO.setStudyDuration(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programOpportunityDTO, "advert");
        programOpportunityDTOValidator.validate(programOpportunityDTO, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("prospectus.durationOfStudy.emptyOrNotInteger", mappingResult.getFieldError("studyDuration").getCode());
    }

    @Test
    public void shouldRejectIfIsCurrentlyAcceptingApplicationsIsNull() {
        programOpportunityDTO.setActive(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programOpportunityDTO, "advert");
        programOpportunityDTOValidator.validate(programOpportunityDTO, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("active").getCode());
    }

    @Test
    public void shouldRejectIfNoStudyOptionsSelected() {
        programOpportunityDTO.getStudyOptions().clear();
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programOpportunityDTO, "advert");
        programOpportunityDTOValidator.validate(programOpportunityDTO, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("studyOptions").getCode());
    }

    @Test
    public void shouldRejectIfNoDeadlineYear() {
        programOpportunityDTO.setAdvertiseDeadlineYear(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programOpportunityDTO, "advert");
        programOpportunityDTOValidator.validate(programOpportunityDTO, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("advertiseDeadlineYear").getCode());
    }

    @Test
    public void shouldRejectIfIncorrectDeadlineYear() {
        programOpportunityDTO.setAdvertiseDeadlineYear(2195);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programOpportunityDTO, "advert");
        programOpportunityDTOValidator.validate(programOpportunityDTO, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("advertiseDeadlineYear").getCode());
    }

}
