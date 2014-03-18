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

import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.dto.DocumentsSectionDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class DocumentSectionValidatorTest {

    @Autowired
    private Validator validator;

    private DocumentSectionValidator documentSectionValidator;

    private DocumentsSectionDTO documentsSectionDTO;

    @Test
    public void shouldSupportApplicationForm() {
        assertTrue(documentSectionValidator.supports(DocumentsSectionDTO.class));
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
        documentsSectionDTO = new DocumentsSectionDTO();
        documentsSectionDTO.setCv(new DocumentBuilder().type(DocumentType.CV).build());
        documentsSectionDTO.setPersonalStatement(new DocumentBuilder().type(DocumentType.PERSONAL_STATEMENT).build());

        documentSectionValidator = new DocumentSectionValidator();
        documentSectionValidator.setValidator((javax.validation.Validator) validator);
    }
}
