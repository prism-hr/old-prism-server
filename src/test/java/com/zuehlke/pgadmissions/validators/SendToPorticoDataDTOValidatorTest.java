package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collections;

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

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
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

    private QualificationService qualificationServiceMock;

    private RefereeService refereeServiceMock;

    private Qualification qualification1;

    private Qualification qualification2;

    private Referee referee1;

    private Referee referee2;

    @Test
    public void shouldValidateCorrectData() {
        EasyMock.replay(qualificationServiceMock, refereeServiceMock);
        sendToPorticoDataValidator.validate(sendToPorticoDataDTO, mappingResult);
        assertFalse(mappingResult.hasErrors());
    }

    @Test
    public void shouldRejectIfNoQualificationAndEmptyComment() {
        sendToPorticoDataDTO.setQualificationsSendToPortico(Collections.<Integer> emptyList());

        EasyMock.replay(qualificationServiceMock, refereeServiceMock);
        sendToPorticoDataValidator.validate(sendToPorticoDataDTO, mappingResult);
        assertEquals(1, mappingResult.getErrorCount());
        assertEquals("portico.submit.explanation.empty", mappingResult.getFieldError("emptyQualificationsExplanation").getCode());
    }
    
    @Test
    public void shouldRejectIfExceededNumberOfQualifications() {
        Document proofOfAward = new Document();
        Qualification qualification3 = new QualificationBuilder().proofOfAward(proofOfAward).build();
        
        sendToPorticoDataDTO.setQualificationsSendToPortico(Arrays.asList(new Integer[] { 1, 2, 3 }));

        EasyMock.expect(qualificationServiceMock.getQualificationById(3)).andReturn(qualification3).anyTimes();

        EasyMock.replay(qualificationServiceMock, refereeServiceMock);
        sendToPorticoDataValidator.validate(sendToPorticoDataDTO, mappingResult);
        assertEquals(1, mappingResult.getErrorCount());
        assertEquals("portico.submit.qualifications.exceed", mappingResult.getFieldError("qualificationsSendToPortico").getCode());
    }
    
    @Test
    public void shouldRejectIfQualificationHasNoProofOfAward() {
        qualification1.setProofOfAward(null);

        EasyMock.replay(qualificationServiceMock, refereeServiceMock);
        sendToPorticoDataValidator.validate(sendToPorticoDataDTO, mappingResult);
        assertEquals(1, mappingResult.getErrorCount());
        assertEquals("portico.submit.qualifications.noProofOfAward", mappingResult.getFieldError("qualificationsSendToPortico").getCode());
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
    public void shouldRejectIfRefereesHasNotResponded() {
        referee1.setReference(null);

        EasyMock.replay(qualificationServiceMock, refereeServiceMock);
        sendToPorticoDataValidator.validate(sendToPorticoDataDTO, mappingResult);
        assertEquals(1, mappingResult.getErrorCount());
        assertEquals("portico.submit.referees.hasNotResponded", mappingResult.getFieldError("refereesSendToPortico").getCode());
    }

    @Before
    public void setup() {
        sendToPorticoDataDTO = new SendToPorticoDataDTO();
        sendToPorticoDataDTO.setQualificationsSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));
        sendToPorticoDataDTO.setRefereesSendToPortico(Arrays.asList(new Integer[] { 11, 12 }));

        Document proofOfAward = new Document();
        qualification1 = new QualificationBuilder().proofOfAward(proofOfAward).build();
        qualification2 = new QualificationBuilder().proofOfAward(proofOfAward).build();
        ReferenceComment referenceComment = new ReferenceComment();
        referee1 = new RefereeBuilder().reference(referenceComment).toReferee();
        referee2 = new RefereeBuilder().reference(referenceComment).toReferee();

        mappingResult = new DirectFieldBindingResult(sendToPorticoDataDTO, "sendToPorticoData");

        qualificationServiceMock = EasyMock.createMock(QualificationService.class);
        refereeServiceMock = EasyMock.createMock(RefereeService.class);

        EasyMock.expect(qualificationServiceMock.getQualificationById(1)).andReturn(qualification1).anyTimes();
        EasyMock.expect(qualificationServiceMock.getQualificationById(2)).andReturn(qualification2).anyTimes();
        EasyMock.expect(refereeServiceMock.getRefereeById(11)).andReturn(referee1).anyTimes();
        EasyMock.expect(refereeServiceMock.getRefereeById(12)).andReturn(referee2).anyTimes();

        sendToPorticoDataValidator = new SendToPorticoDataDTOValidator(qualificationServiceMock, refereeServiceMock);
        sendToPorticoDataValidator.setValidator((javax.validation.Validator) validator);
    }

    @After
    public void cleanUp() {
        EasyMock.verify(qualificationServiceMock, refereeServiceMock);
    }

}
