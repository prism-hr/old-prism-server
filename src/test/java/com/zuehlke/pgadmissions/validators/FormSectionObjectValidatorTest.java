package com.zuehlke.pgadmissions.validators;

import java.text.ParseException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.FormSectionObject;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class FormSectionObjectValidatorTest {
    private ApplicationForm applicationForm;
    private MyObject formSectionObject;
    private FormSectionObjectValidator formSectionObjectValidator;

    @Test
    public void shouldRejectIfApplicationSubmittedAndTermsAcceptedIsFalse() {
        applicationForm.setStatus(new State().withId(ApplicationFormStatus.VALIDATION));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(formSectionObject, "acceptedTerms");
        formSectionObjectValidator.addExtraValidation(formSectionObject, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("acceptedTerms").getCode());
    }

    @Test
    public void shouldNotRejectIfApplicationsubmittedAndTermsAcceptedIsTrue() {
        formSectionObject.setAcceptedTerms(true);
        applicationForm.setStatus(new State().withId(ApplicationFormStatus.VALIDATION));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(formSectionObject, "acceptedTerms");
        formSectionObjectValidator.addExtraValidation(formSectionObject, mappingResult);

        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldNotRejectIfApplicationUnsubmittedAndTermsAcceptedIsFalse() {
        applicationForm.setStatus(new State().withId(ApplicationFormStatus.UNSUBMITTED));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(formSectionObject, "acceptedTerms");
        formSectionObjectValidator.addExtraValidation(formSectionObject, mappingResult);

        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Before
    public void setup() throws ParseException {
        applicationForm = new ApplicationFormBuilder().id(2).status(new State().withId(ApplicationFormStatus.UNSUBMITTED)).build();
        formSectionObject = new MyObject();
        formSectionObjectValidator = new FormSectionObjectValidator() {
        };
    }

    private class MyObject implements FormSectionObject {
        private ApplicationForm application = applicationForm;
        private boolean acceptedTerms;

        public ApplicationForm getApplication() {
            return application;
        }

        public boolean isAcceptedTerms() {
            return acceptedTerms;
        }

        public void setAcceptedTerms(boolean acceptedTerms) {
            this.acceptedTerms = acceptedTerms;
        }
    }
}
