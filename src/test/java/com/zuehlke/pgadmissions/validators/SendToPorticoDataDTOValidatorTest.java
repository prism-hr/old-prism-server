package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;
import org.unitils.inject.util.InjectionUtils;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.PorticoService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.RefereeService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class SendToPorticoDataDTOValidatorTest {

    @Autowired
    private Validator validator;

    private SendToPorticoDataDTOValidator sendToPorticoDataValidator;

    private SendToPorticoDataDTO sendToPorticoDataDTO;

    private DirectFieldBindingResult mappingResult;

    private ApplicationService applicationsServiceMock;

    private QualificationService qualificationServiceMock;

    private RefereeService refereeServiceMock;

    private Qualification qualification1;

    private Qualification qualification2;

    private Referee referee1;

    private Referee referee2;

    private Application applicationForm;

    private PorticoService porticoServiceMock;

    @Test
    public void shouldValidateCorrectData() {
        EasyMock.replay(qualificationServiceMock, refereeServiceMock);
        sendToPorticoDataValidator.validate(sendToPorticoDataDTO, mappingResult);
        assertFalse(mappingResult.hasErrors());
    }

    @Test
    public void shouldValidateCorrectlyIfNoQualificationAndExplanationExistent() {
        sendToPorticoDataDTO.setQualificationsSendToPortico(new ArrayList<Integer>());
        sendToPorticoDataDTO.setEmptyQualificationsExplanation("I've got a reason");
        EasyMock.replay(qualificationServiceMock, refereeServiceMock);
        sendToPorticoDataValidator.validate(sendToPorticoDataDTO, mappingResult);
        assertFalse(mappingResult.hasErrors());
    }

    @Test
    public void shouldRejectIfNoQualificationAndEmptyExplanation() {
        sendToPorticoDataDTO.setQualificationsSendToPortico(Collections.<Integer> emptyList());
        sendToPorticoDataDTO.setEmptyQualificationsExplanation("");

        EasyMock.replay(qualificationServiceMock, refereeServiceMock);
        sendToPorticoDataValidator.validate(sendToPorticoDataDTO, mappingResult);
        assertEquals(1, mappingResult.getErrorCount());
        assertEquals("portico.submit.no.qualification.or.explanation", mappingResult.getFieldError("qualificationsSendToPortico").getCode());
    }

    @Test
    public void shouldRejectIfNoQualificationAndNullExplanation() {
        sendToPorticoDataDTO.setQualificationsSendToPortico(Collections.<Integer> emptyList());
        sendToPorticoDataDTO.setEmptyQualificationsExplanation(null);

        EasyMock.replay(qualificationServiceMock, refereeServiceMock);
        sendToPorticoDataValidator.validate(sendToPorticoDataDTO, mappingResult);
        assertEquals(1, mappingResult.getErrorCount());
        assertEquals("portico.submit.no.qualification.or.explanation", mappingResult.getFieldError("qualificationsSendToPortico").getCode());
    }

    @Test
    public void shouldRejectIfNumberOfRefereesIsInvalid() {
        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 11 }));

        EasyMock.replay(qualificationServiceMock, refereeServiceMock);
        sendToPorticoDataValidator.validate(sendToPorticoDataDTO, mappingResult);
        assertEquals(1, mappingResult.getErrorCount());
        assertEquals("portico.submit.referees.invalid", mappingResult.getFieldError("refereesSendToPortico").getCode());
    }

    @Test
    public void shouldRejectIfExplanationIsKLongerThan500Characters() {
        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 11, 12 }));
        sendToPorticoDataDTO.setEmptyQualificationsExplanation(RandomStringUtils.randomAscii(50001));

        EasyMock.replay(qualificationServiceMock, refereeServiceMock);
        sendToPorticoDataValidator.validate(sendToPorticoDataDTO, mappingResult);
        assertEquals(2, mappingResult.getErrorCount());
        assertEquals("A maximum of 50000 characters are allowed.", mappingResult.getFieldError("emptyQualificationsExplanation").getDefaultMessage());
        assertEquals("portico.submit.no.qualification.or.explanation", mappingResult.getFieldError("qualificationsSendToPortico").getCode());
    }

    @Before
    public void setup() {
        sendToPorticoDataDTO = new SendToPorticoDataDTO();
        sendToPorticoDataDTO.setQualificationsSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));
        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 11, 12 }));
        sendToPorticoDataDTO.setApplicationNumber("84");

        Document proofOfAward = new Document();
        qualification1 = new Qualification().withDocument(proofOfAward);
        qualification2 = new Qualification().withDocument(proofOfAward);
        ReferenceComment referenceComment = new ReferenceComment();
        referee1 = new Referee().withComment(referenceComment);
        referee2 = new Referee().withComment(referenceComment);

        applicationForm = new Application();
        applicationForm.getQualifications().addAll(Arrays.asList(qualification1, qualification2));
        applicationForm.getReferees().addAll(Arrays.asList(referee1, referee2));

        mappingResult = new DirectFieldBindingResult(sendToPorticoDataDTO, "sendToPorticoData");

        applicationsServiceMock = EasyMock.createMock(ApplicationService.class);
        qualificationServiceMock = EasyMock.createMock(QualificationService.class);
        refereeServiceMock = EasyMock.createMock(RefereeService.class);
        porticoServiceMock = EasyMock.createMock(PorticoService.class);

        EasyMock.expect(qualificationServiceMock.getById(1)).andReturn(qualification1).anyTimes();
        EasyMock.expect(qualificationServiceMock.getById(2)).andReturn(qualification2).anyTimes();
        EasyMock.expect(refereeServiceMock.getById(11)).andReturn(referee1).anyTimes();
        EasyMock.expect(refereeServiceMock.getById(12)).andReturn(referee2).anyTimes();
        EasyMock.expect(applicationsServiceMock.getByApplicationNumber("84")).andReturn(applicationForm);

        sendToPorticoDataValidator = new SendToPorticoDataDTOValidator();
        InjectionUtils.injectInto(applicationsServiceMock, sendToPorticoDataValidator, "applicationFormService");
        InjectionUtils.injectInto(qualificationServiceMock, sendToPorticoDataValidator, "qualificationService");
        InjectionUtils.injectInto(refereeServiceMock, sendToPorticoDataValidator, "refereeService");
        InjectionUtils.injectInto(porticoServiceMock, sendToPorticoDataValidator, "porticoService");

        sendToPorticoDataValidator.setValidator((javax.validation.Validator) validator);

        EasyMock.replay(applicationsServiceMock);
    }

    @After
    public void cleanUp() {
        EasyMock.verify(qualificationServiceMock, refereeServiceMock);
    }
}
