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
import org.unitils.inject.util.InjectionUtils;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.integration.providers.ApplicationTestDataProvider;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ApplicationCopyHelperTest {

    @Autowired
    private Validator validator;

    @Autowired
    private ApplicationTestDataProvider applicationTestDataProvider;

    private ApplicationCopyHelper applicationFormCopyHelper;

    @Test
    public void shouldCopyApplication() throws Exception {
        Application application = new Application();
        applicationTestDataProvider.fillWithData(application);
        State state = new State();
        application.setState(state);

        Application newApplicationForm = new Application();
        applicationFormCopyHelper.copyApplicationFormData(newApplicationForm, application);

    }

    @Before
    public void setup() {

        DocumentService documentService = EasyMock.createMock(DocumentService.class);
        UserService userServiceMock = EasyMock.createMock(UserService.class);

        applicationFormCopyHelper = new ApplicationCopyHelper();
        InjectionUtils.injectInto(documentService, applicationFormCopyHelper, "documentService");

        expect(userServiceMock.getCurrentUser()).andReturn(new User().withEmail("jfi@zuhlke.pl")).anyTimes();
        replay(userServiceMock);
    }

}
