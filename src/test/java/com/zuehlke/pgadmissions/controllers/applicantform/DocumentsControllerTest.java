package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.unitils.easymock.EasyMockUnitils.replay;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.dto.DocumentsSectionDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.DocumentSectionValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class DocumentsControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationsService applicationsServiceMock;

    @Mock
    @InjectIntoByType
    private DocumentSectionValidator documentSectionValidatorMock;

    @Mock
    @InjectIntoByType
    private DocumentPropertyEditor documentPropertyEditorMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;

    @TestedObject
    private DocumentsController controller;

    @Test
    public void shouldReturnApplicationFormView() {
        ModelMap modelMap = new ModelMap();
        ApplicationForm application = new ApplicationFormBuilder().cv(new Document()).personalStatement(new Document()).build();
        modelMap.put("applicationForm", application);

        assertEquals("/private/pgStudents/form/components/documents", controller.getDocumentsView(modelMap));

        assertNotNull(modelMap.get("documentsSectionDTO"));
    }

    @Test
    public void shouldReturnApplicationForm() {
        ApplicationForm applicationForm = new ApplicationForm();

        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);

        replay();
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");

        assertSame(applicationForm, returnedApplicationForm);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);

        replay();
        controller.getApplicationForm("1");
    }

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);

        binderMock.setValidator(documentSectionValidatorMock);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);

        replay();
        controller.registerPropertyEditors(binderMock);

    }

    @Test
    public void shouldSaveAppplicationFormAndRedirectIfNoErrors() {
        RegisteredUser currentUser = new RegisteredUser();
        DocumentsSectionDTO documentsSectionDTO = new DocumentsSectionDTO();
        BindingResult bindingResult = new BeanPropertyBindingResult(documentsSectionDTO, "documentsSectionDTO");
        ApplicationForm application = new ApplicationFormBuilder().id(666).applicationNumber("ABC").build();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", application);

        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        applicationsServiceMock.saveDocumentsSection(666, documentsSectionDTO);
        applicationFormUserRoleServiceMock.insertApplicationUpdate(application, currentUser, ApplicationUpdateScope.ALL_USERS);

        replay();
        String view = controller.editDocuments(documentsSectionDTO, bindingResult, modelMap);

        assertEquals("redirect:/update/getDocuments?applicationId=ABC", view);
    }

    @Test
    public void shouldNotSaveAndReturnToViewIfErrors() {
        DocumentsSectionDTO documentsSectionDTO = new DocumentsSectionDTO();
        BindingResult bindingResult = new BeanPropertyBindingResult(documentsSectionDTO, "documentsSectionDTO");
        ModelMap modelMap = new ModelMap();
        
        bindingResult.rejectValue("personalStatement", "file.upload.empty");

        replay();
        String view = controller.editDocuments(documentsSectionDTO, bindingResult, modelMap);

        assertEquals("/private/pgStudents/form/components/documents", view);
        assertSame(documentsSectionDTO, modelMap.get("documentsSectionDTO"));
    }
}