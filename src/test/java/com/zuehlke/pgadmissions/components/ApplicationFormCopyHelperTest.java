package com.zuehlke.pgadmissions.components;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import javax.validation.Validator;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.unitils.inject.util.InjectionUtils;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.integration.providers.ApplicationTestDataProvider;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.EmploymentPositionValidator;
import com.zuehlke.pgadmissions.validators.FundingValidator;
import com.zuehlke.pgadmissions.validators.LanguageQualificationValidator;
import com.zuehlke.pgadmissions.validators.PassportValidator;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;
import com.zuehlke.pgadmissions.validators.QualificationValidator;
import com.zuehlke.pgadmissions.validators.RefereeValidator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ApplicationFormCopyHelperTest {

    @Autowired
    private Validator validator;
    
    @Autowired
    private ApplicationTestDataProvider applicationTestDataProvider;

    private PersonalDetailsValidator personalDetailsValidator;
    private LanguageQualificationValidator languageQualificationValidator;
    private PassportValidator passportInformationValidator;
    private QualificationValidator qualificationValidator;
    private EmploymentPositionValidator employmentPositionValidator;
    private FundingValidator fundingValidator;
    private RefereeValidator refereeValidator;
    private ApplicationCopyHelper applicationFormCopyHelper;

    @Test
    public void shouldCopyApplication() throws Exception {
        Application application = new Application();
        applicationTestDataProvider.fillWithData(application);
        State state = new State();
        application.setState(state);
        validateApplication(application);

        Application newApplicationForm = new Application();
        applicationFormCopyHelper.copyApplicationFormData(newApplicationForm, application);

        validateApplication(newApplicationForm);
    }

    private void validateApplication(Application applicationForm) {
        BindingResult bindingResult = new BeanPropertyBindingResult(applicationForm, "applicationForm");

        bindingResult.pushNestedPath("personalDetails");
        ValidationUtils.invokeValidator(personalDetailsValidator, applicationForm.getPersonalDetails(), bindingResult);
        bindingResult.popNestedPath();

        int i = 0;
        for (ApplicationQualification qualification : applicationForm.getQualifications()) {
            bindingResult.pushNestedPath("qualifications[" + (i++) + "]");
            ValidationUtils.invokeValidator(qualificationValidator, qualification, bindingResult);
            bindingResult.popNestedPath();
        }

        i = 0;
        for (ApplicationEmploymentPosition employmentPosition : applicationForm.getEmploymentPositions()) {
            bindingResult.pushNestedPath("employmentPositions[" + (i++) + "]");
            ValidationUtils.invokeValidator(employmentPositionValidator, employmentPosition, bindingResult);
            bindingResult.popNestedPath();
        }

        i = 0;
        for (ApplicationFunding funding : applicationForm.getFundings()) {
            bindingResult.pushNestedPath("fundings[" + (i++) + "]");
            ValidationUtils.invokeValidator(fundingValidator, funding, bindingResult);
            bindingResult.popNestedPath();
        }

        i = 0;
        for (ApplicationReferee referee : applicationForm.getReferees()) {
            referee.setId(i); // hack for validator
            bindingResult.pushNestedPath("referees[" + (i++) + "]");
            ValidationUtils.invokeValidator(refereeValidator, referee, bindingResult);
            bindingResult.popNestedPath();
        }
    }

    @Before
    public void setup() {

        DocumentService documentService = EasyMock.createMock(DocumentService.class);
        UserService userServiceMock = EasyMock.createMock(UserService.class);

        languageQualificationValidator = new LanguageQualificationValidator();
        languageQualificationValidator.setValidator(validator);

        passportInformationValidator = new PassportValidator();
        passportInformationValidator.setValidator(validator);

        personalDetailsValidator = new PersonalDetailsValidator(passportInformationValidator, languageQualificationValidator);
        personalDetailsValidator.setValidator(validator);

        qualificationValidator = new QualificationValidator();
        qualificationValidator.setValidator(validator);

        employmentPositionValidator = new EmploymentPositionValidator();
        employmentPositionValidator.setValidator(validator);

        fundingValidator = new FundingValidator();
        fundingValidator.setValidator(validator);

        refereeValidator = new RefereeValidator(userServiceMock);
        refereeValidator.setValidator(validator);

        applicationFormCopyHelper = new ApplicationCopyHelper();
        InjectionUtils.injectInto(documentService, applicationFormCopyHelper, "documentService");

        expect(userServiceMock.getCurrentUser()).andReturn(new User().withEmail("jfi@zuhlke.pl")).anyTimes();
        replay(userServiceMock);
    }

}
