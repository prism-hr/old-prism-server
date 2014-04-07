package com.zuehlke.pgadmissions.controllers.usermanagement;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityScope;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.ManageUsersService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.SuperadminUserDTOValidator;
import com.zuehlke.pgadmissions.validators.UserDTOValidator;

@Controller
@RequestMapping("/manageUsers")
public class ManageUsersController {

    private static final String NEW_USER_VIEW_NAME = "private/staff/superAdmin/create_new_user_in_role_page";

    @Autowired
    private ProgramService programService;

    @Autowired
    private UserService userService;

    @Autowired
    private ManageUsersService manageUsersService;

    @Autowired
    private ProgramPropertyEditor programPropertyEditor;

    @Autowired
    private UserDTOValidator newUserDTOValidator;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private SuperadminUserDTOValidator userDTOValidator;

    @Autowired
    private PersonPropertyEditor registryPropertyEditor;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private RoleService roleService;

    @InitBinder(value = "registryUserDTO")
    public void registerValidatorsAndPropertyEditorsForRegistryUsers(WebDataBinder binder) {
        binder.registerCustomEditor(Person.class, registryPropertyEditor);
    }

    @InitBinder(value = "userDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(newUserDTOValidator);
        binder.registerCustomEditor(Program.class, programPropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @InitBinder(value = "adminDTO")
    public void registerValidator(WebDataBinder binder) {
        binder.setValidator(userDTOValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @ModelAttribute("programs")
    public List<Program> getPrograms() {
        RegisteredUser currentUser = userService.getCurrentUser();
        if (roleService.hasRole(currentUser, Authority.SUPERADMINISTRATOR)) {
            return programService.getAllEnabledPrograms();
        }
        return roleService.getProgramsByUserAndRole(currentUser, Authority.ADMINISTRATOR);
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("authorities")
    public List<Authority> getAuthorities() {
        return roleService.getAuthorities(AuthorityScope.PROGRAM);
    }

    @ModelAttribute("superadministrators")
    public List<RegisteredUser> getSuperadministrators() {
        List<RegisteredUser> superadmins = roleService.getUsersInRole(Authority.SUPERADMINISTRATOR);
        return superadmins;
    }

    @ModelAttribute("admitters")
    public List<RegisteredUser> getAdmitters() {
        return roleService.getUsersInRole(Authority.ADMITTER);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/edit")
    public String getAddUsersView() {
        RegisteredUser user = userService.getCurrentUser();
        if (!roleService.hasAnyRole(user, Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR, Authority.ADMITTER)) {
            throw new ResourceNotFoundException();
        }
        return NEW_USER_VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/edit/saveSuperadmin")
    public String handleAddSuperadmin(@Valid @ModelAttribute("adminDTO") UserDTO userDTO, BindingResult result) {
        RegisteredUser user = userService.getCurrentUser();
        if (Arrays.asList(userDTO.getSelectedAuthorities()).contains(Authority.SUPERADMINISTRATOR) && !roleService.hasRole(user, Authority.SUPERADMINISTRATOR)) {
            throw new ResourceNotFoundException();
        }

        if (roleService.hasRole(user, Authority.SUPERADMINISTRATOR)) {
            throw new ResourceNotFoundException();
        }
        if (result.hasErrors()) {
            return NEW_USER_VIEW_NAME;
        }
        manageUsersService.addUserSystemRoles(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), userDTO.getSelectedAuthorities());

        return "redirect:/manageUsers/edit";

    }

    @RequestMapping(method = RequestMethod.POST, value = "/edit/saveUser")
    public String handleEditUserRoles(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result) {
        RegisteredUser user = userService.getCurrentUser();
        if (!roleService.hasAnyRole(user, Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR)) {
            throw new ResourceNotFoundException();
        }

        if (result.hasErrors()) {
            return NEW_USER_VIEW_NAME;
        }

        manageUsersService.addUserProgramRoles(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), userDTO.getSelectedProgram(),
                userDTO.getSelectedAuthorities());

        return "redirect:/manageUsers/edit?programCode=" + userDTO.getSelectedProgram().getCode();
    }

}
