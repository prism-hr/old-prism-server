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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormDocument;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ApplicationFormDocumentValidatorTest {

    @Autowired
    private Validator validator;

    private ApplicationFormDocumentValidator documentSectionValidator;

    private ApplicationFormDocument documentsSectionDTO;

    @Test
    public void shouldSupportApplicationForm() {
        assertTrue(documentSectionValidator.supports(ApplicationFormDocument.class));
    }

    @Test
    public void shoulRejectIfPersonalStatementNotUploaded() {
        documentsSectionDTO.setPersonalStatement(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(documentsSectionDTO, "documentsSectionDTO");

        documentSectionValidator.validate(documentsSectionDTO, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("file.upload.empty", mappingResult.getFieldError("personalStatement").getCode());
    }

    @Before
    public void setup() {
        ApplicationForm application = new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.UNSUBMITTED)).build();
        documentsSectionDTO = new ApplicationFormDocument();
        documentsSectionDTO.setCv(new DocumentBuilder().type(DocumentType.CV).build());
        documentsSectionDTO.setPersonalStatement(new DocumentBuilder().type(DocumentType.PERSONAL_STATEMENT).build());
        documentsSectionDTO.setApplication(application);

        documentSectionValidator = new ApplicationFormDocumentValidator();
        documentSectionValidator.setValidator((javax.validation.Validator) validator);
    }
}
