package com.zuehlke.pgadmissions.controllers.workflow.approval;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/approval")
public class CreateNewApplicationUserController {

    private static final String CREATE_SUPERVISOR_SECTION = "/private/staff/supervisors/create_supervisor_section";

    private static final String CREATE_INTERVIEWER_SECTION = "/private/staff/interviewers/create_interviewer_section";

    private static final String CREATE_REVIEWER_SECTION = "/private/staff/reviewer/create_reviewer_section";

    private static final String JSON_VIEW = "/private/staff/admin/application_user_json";

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationFormService applicationsService;

    @RequestMapping(value = "/createSupervisor", method = RequestMethod.POST)
    public String createNewSupervisorUser(@Valid @ModelAttribute("suggestedUser") UserDTO userDTO, BindingResult bindingResult, ModelMap modelMap) {
        return createNewUser(Authority.SUPERVISOR, userDTO, bindingResult, modelMap);
    }

    @RequestMapping(value = "/createInterviewer", method = RequestMethod.POST)
    public String createNewInterviewerUser(@Valid @ModelAttribute("suggestedUser") UserDTO userDTO, BindingResult bindingResult, ModelMap modelMap) {
        return createNewUser(Authority.APPLICATION_INTERVIEWER, userDTO, bindingResult, modelMap);
    }

    @RequestMapping(value = "/createReviewer", method = RequestMethod.POST)
    public String createNewReviewerUser(@Valid @ModelAttribute("suggestedUser") UserDTO userDTO, BindingResult bindingResult, ModelMap modelMap) {
        return createNewUser(Authority.APPLICATION_REVIEWER, userDTO, bindingResult, modelMap);
    }

    private String createNewUser(Authority userType, UserDTO userDTO, BindingResult bindingResult, ModelMap modelMap) {
        if (bindingResult.hasErrors()) {
            switch (userType) {
            case SUPERVISOR:
                return CREATE_SUPERVISOR_SECTION;
            case APPLICATION_INTERVIEWER:
                return CREATE_INTERVIEWER_SECTION;
            case APPLICATION_REVIEWER:
                return CREATE_REVIEWER_SECTION;
            default:
                throw new RuntimeException();
            }
        }

        User existingUser = userService.getUserByEmailIncludingDisabledAccounts(userDTO.getEmail());
        User user = userService.getUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), true);

        modelMap.put("isNew", existingUser == null);
        modelMap.put("user", user);

        return JSON_VIEW;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/create_supervisor_section")
    public String getCreateSupervisorSection() {
        return CREATE_SUPERVISOR_SECTION;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/create_interviewer_section")
    public String getCreateInterviewerSection() {
        return CREATE_INTERVIEWER_SECTION;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/create_reviewer_section")
    public String getCreateReviewerSection() {
        return CREATE_REVIEWER_SECTION;
    }

    @ModelAttribute("suggestedUser")
    public UserDTO getSuggestedUser() {
        return new UserDTO();
    }

}