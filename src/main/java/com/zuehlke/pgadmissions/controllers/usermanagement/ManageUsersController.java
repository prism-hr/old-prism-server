package com.zuehlke.pgadmissions.controllers.usermanagement;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.RegistryUserDTO;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.SuperadminUserDTOValidator;
import com.zuehlke.pgadmissions.validators.UserDTOValidator;

@Controller
@RequestMapping("/manageUsers")
public class ManageUsersController {

    private static final String NEW_USER_VIEW_NAME = "private/staff/superAdmin/create_new_user_in_role_page";

    private final ProgramService programsService;

    private final UserService userService;

    private final ProgramPropertyEditor programPropertyEditor;

    private final UserDTOValidator newUserDTOValidator;

    private final EncryptionHelper encryptionHelper;

    private final SuperadminUserDTOValidator userDTOValidator;

    private final PersonPropertyEditor registryPropertyEditor;

    private final ConfigurationService configurationService;

    public ManageUsersController() {
        this(null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ManageUsersController(ProgramService programsService, UserService userService, ProgramPropertyEditor programPropertyEditor,
            UserDTOValidator newUserDTOValidator, EncryptionHelper encryptionHelper, final SuperadminUserDTOValidator userDTOValidator,
            final PersonPropertyEditor registryPropertyEditor, final ConfigurationService configurationService) {
        this.programsService = programsService;
        this.userService = userService;
        this.programPropertyEditor = programPropertyEditor;
        this.newUserDTOValidator = newUserDTOValidator;
        this.encryptionHelper = encryptionHelper;
        this.userDTOValidator = userDTOValidator;
        this.registryPropertyEditor = registryPropertyEditor;
        this.configurationService = configurationService;
    }

    @InitBinder(value = "registryUserDTO")
    public void registerValidatorsAndPropertyEditorsForRegistryUsers(WebDataBinder binder) {
        binder.registerCustomEditor(Person.class, registryPropertyEditor);
    }

    @InitBinder(value = "userDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(newUserDTOValidator);
        binder.registerCustomEditor(Program.class, programPropertyEditor);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }

    @InitBinder(value = "adminDTO")
    public void registerValidator(WebDataBinder binder) {
        binder.setValidator(userDTOValidator);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }

    @ModelAttribute("programs")
    public List<Program> getPrograms() {
        RegisteredUser currentUser = getCurrentUser();
        if (currentUser.isInRole(Authority.SUPERADMINISTRATOR)) {
            return programsService.getAllEnabledPrograms();
        }
        return currentUser.getProgramsOfWhichAdministrator();
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("authorities")
    public List<Authority> getAuthorities() {
        return Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.VIEWER);
    }

    @ModelAttribute("adminDTO")
    public UserDTO getUserDTO() {
        return new UserDTO();
    }

    @ModelAttribute("userDTO")
    public UserDTO getNewUserDTO(@RequestParam(required = false) String user, @RequestParam(required = false) String programCode) {
        if (user == null) {
            return newUserDTO(programCode);
        }
        return createUserDTOFromExistingUser(user, programCode);
    }

    @ModelAttribute("superadmins")
    public List<RegisteredUser> getSuperadmins() {
        List<RegisteredUser> superadmins = userService.getUsersInRole(Authority.SUPERADMINISTRATOR);

        Collections.sort(superadmins, new Comparator<RegisteredUser>() {

            @Override
            public int compare(RegisteredUser o1, RegisteredUser o2) {
                if (!o1.getLastName().equals(o2.getLastName())) {
                    return o1.getLastName().compareTo(o2.getLastName());
                }
                return o1.getFirstName().compareTo(o2.getFirstName());
            }
        });
        return superadmins;
    }

    @ModelAttribute("allRegistryUsers")
    public List<Person> getAllRegistryContacts() {
        return configurationService.getAllRegistryUsers();
    }

    @RequestMapping(method = RequestMethod.GET, value = { "/edit", "/edit/saveSuperadmin", "/edit/saveUser", "/edit/saveRegistryContact" })
    public String getAddUsersView() {
        if (!isCurrentUserAdministrator() && getCurrentUser().isNotInRole(Authority.ADMITTER)) {
            throw new ResourceNotFoundException();
        }
        return NEW_USER_VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/edit/saveSuperadmin")
    public String handleAddSuperadmin(@Valid @ModelAttribute("adminDTO") UserDTO userDTO, BindingResult result) {
        if (!userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
            throw new ResourceNotFoundException();
        }
        if (result.hasErrors()) {
            return NEW_USER_VIEW_NAME;
        }
        RegisteredUser existingUser = userService.getUserByEmailIncludingDisabledAccounts(userDTO.getEmail());
        if (existingUser != null) {
            userService.updateUserWithNewRoles(existingUser, null, Authority.SUPERADMINISTRATOR);
        } else {
            existingUser = userService.createNewUserForProgramme(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), null,
                    Authority.SUPERADMINISTRATOR);
        }

        return "redirect:/manageUsers/edit";

    }

    @RequestMapping(method = RequestMethod.POST, value = "/edit/saveUser")
    public String handleEditUserRoles(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result) {
        if (!isCurrentUserAdministrator()) {
            throw new ResourceNotFoundException();
        }

        if (result.hasErrors()) {
            return NEW_USER_VIEW_NAME;
        }

        RegisteredUser existingUser = userService.getUserByEmailIncludingDisabledAccounts(userDTO.getEmail());
        if (existingUser != null) {
            userService.updateUserWithNewRoles(existingUser, userDTO.getSelectedProgram(), userDTO.getSelectedAuthorities());
        } else {
            existingUser = userService.createNewUserForProgramme(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                    userDTO.getSelectedProgram(), userDTO.getSelectedAuthorities());
        }

        if (userDTO.getSelectedProgram() == null) {
            return "redirect:/manageUsers/edit";
        }

        return "redirect:/manageUsers/edit?programCode=" + userDTO.getSelectedProgram().getCode();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/edit/saveRegistryUsers")
    public String handleEditRegistryUsers(@ModelAttribute RegistryUserDTO registryUserDTO) {
        if (!getUser().isInRole(Authority.SUPERADMINISTRATOR) && !getUser().isInRole(Authority.ADMITTER)) {
            throw new ResourceNotFoundException();
        }
        configurationService.saveRegistryUsers(registryUserDTO.getRegistryUsers(), getUser());
        if (!getUser().isInRole(Authority.SUPERADMINISTRATOR) && !getUser().isInRole(Authority.ADMITTER)) {
            return "redirect:/applications";
        }
        return "redirect:/manageUsers/edit";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/remove")
    public String handleRemoveUserFromProgram(@ModelAttribute("userDTO") UserDTO userDTO) {
        if (!isCurrentUserAdministrator()) {
            throw new ResourceNotFoundException();
        }
        RegisteredUser userToRemove = userService.getUserByEmailIncludingDisabledAccounts(userDTO.getEmail());
        userService.deleteUserFromProgramme(userToRemove, userDTO.getSelectedProgram());
        if (userToRemove.getId().equals(getCurrentUser().getId()) && userToRemove.getProgramsOfWhichAdministrator().isEmpty()) {
            return "redirect:/applications";
        }
        return "redirect:/manageUsers/edit?programCode=" + userDTO.getSelectedProgram().getCode();
    }

    private UserDTO createUserDTOFromExistingUser(final String user, final String programCode) {
        RegisteredUser selectedUser = userService.getUser(encryptionHelper.decryptToInteger(user));
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(selectedUser.getFirstName());
        userDTO.setLastName(selectedUser.getLastName());
        userDTO.setEmail(selectedUser.getEmail());
        Program selectedProgram = getSelectedProgram(programCode);
        userDTO.setSelectedProgram(selectedProgram);
        List<Authority> authoritiesForProgram = selectedUser.getAuthoritiesForProgram(selectedProgram);
        Authority[] selectedAuthorities = authoritiesForProgram.toArray(new Authority[] {});
        userDTO.setSelectedAuthorities(selectedAuthorities);
        userDTO.setNewUser(false);
        return userDTO;
    }

    private UserDTO newUserDTO(final String programCode) {
        UserDTO userDTO = new UserDTO();
        userDTO.setSelectedProgram(getSelectedProgram(programCode));
        return userDTO;
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    protected Program getSelectedProgram(final String programCode) {
        if (programCode == null) {
            return null;
        }
        Program selectedProgram = programsService.getProgramByCode(programCode);
        if (selectedProgram == null) {
            throw new ResourceNotFoundException();
        }
        return selectedProgram;
    }

    private boolean isCurrentUserAdministrator() {
        RegisteredUser currentUser = getCurrentUser();
        return currentUser.isInRole(Authority.SUPERADMINISTRATOR) || currentUser.isInRole(Authority.ADMINISTRATOR);
    }

    private RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }
}
