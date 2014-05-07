package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class CreateNewApplicationUserControllerTest {

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationsServiceMock;

    @TestedObject
    private CreateNewApplicationUserController controller;

    @Test
    public void shouldCreateNewUserIfUserDoesNotExists() {
        UserDTO userDTO = new UserDTO().withFirstName("bob").withLastName("bobson").withEmail("bobson@bob.com");
        User user = new User().withId(6).withFirstName("bob").withLastName("bobson").withEmail("bobson@bob.com");
        ModelMap modelMap = new ModelMap();
        BindingResult bindingResult = new BeanPropertyBindingResult(user, "user");

        String result = controller.createNewSupervisorUser(userDTO, bindingResult, modelMap);

        Assert.assertEquals("/private/staff/admin/application_user_json", result);
        assertEquals(user, modelMap.get("user"));
        assertTrue((Boolean) modelMap.get("isNew"));
    }

    @Test
    public void shouldReturnToViewIfValidationErrors() {
        UserDTO userDTO = new UserDTO().withFirstName("bob").withLastName("bobson").withEmail("bobson@bob.com");
        BindingResult bindingResult = new BeanPropertyBindingResult(userDTO, "user");

        String result = controller.createNewSupervisorUser(userDTO, bindingResult, null);
        Assert.assertEquals("/private/staff/supervisors/create_supervisor_section", result);

    }

    @Test
    public void shouldGetCreateSupervisorsSection() {
        Assert.assertEquals("/private/staff/supervisors/create_supervisor_section", controller.getCreateSupervisorSection());
    }

    @Test
    public void shouldGetNewUserAsSupervisor() {
        UserDTO userDTO = controller.getSuggestedUser();
        assertNotNull(userDTO);
    }

}