package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.RejectionReason;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.ActionOutcome;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.CommentAssignedUserDTO;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.UserDTO;
import com.zuehlke.pgadmissions.rest.dto.application.*;
import com.zuehlke.pgadmissions.rest.representation.ActionOutcomeRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.CommentDTOValidator;
import com.zuehlke.pgadmissions.services.*;
import org.apache.commons.lang.BooleanUtils;
import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = { "api/applications" })
public class ApplicationResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private ImportedEntityService importedEntitytService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private UserService userService;

    @Autowired
    private Mapper dozerBeanMapper;

    @Autowired
    private CommentDTOValidator commentDTOValidator;

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/{applicationId}/programDetails", method = RequestMethod.PUT)
    public void saveProgramDetails(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationProgramDetailsDTO programDetailsDTO) {
        applicationService.saveProgramDetails(applicationId, programDetailsDTO);
    }

    @RequestMapping(value = "/{applicationId}/personalDetails", method = RequestMethod.PUT)
    public void savePersonalDetails(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationPersonalDetailsDTO personalDetailsDTO) {
        applicationService.savePersonalDetails(applicationId, personalDetailsDTO);
    }

    @RequestMapping(value = "/{applicationId}/address", method = RequestMethod.PUT)
    public void saveAddress(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationAddressDTO addressDTO) {
        applicationService.saveAddress(applicationId, addressDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications", method = RequestMethod.POST)
    public Map<String, Object> createQualification(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationQualificationDTO qualificationDTO) {
        ApplicationQualification qualification = applicationService.saveQualification(applicationId, null, qualificationDTO);
        return ImmutableMap.of("id", (Object) qualification.getId());
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.PUT)
    public void updateQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId,
            @Valid @RequestBody ApplicationQualificationDTO qualificationDTO) {
        applicationService.saveQualification(applicationId, qualificationId, qualificationDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.DELETE)
    public void updateQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId) {
        applicationService.deleteQualification(applicationId, qualificationId);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions", method = RequestMethod.POST)
    public Map<String, Object> createEmploymentPosition(@PathVariable Integer applicationId,
            @Valid @RequestBody ApplicationEmploymentPositionDTO employmentPositionDTO) {
        ApplicationEmploymentPosition employmentPosition = applicationService.saveEmploymentPosition(applicationId, null, employmentPositionDTO);
        return ImmutableMap.of("id", (Object) employmentPosition.getId());
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.PUT)
    public void updateEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId,
            @Valid @RequestBody ApplicationEmploymentPositionDTO employmentPositionDTO) {
        applicationService.saveEmploymentPosition(applicationId, employmentPositionId, employmentPositionDTO);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.DELETE)
    public void updateEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId) {
        applicationService.deleteEmploymentPosition(applicationId, employmentPositionId);
    }

    @RequestMapping(value = "/{applicationId}/fundings", method = RequestMethod.POST)
    public Map<String, Object> createFunding(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationFundingDTO fundingDTO) {
        ApplicationFunding funding = applicationService.saveFunding(applicationId, null, fundingDTO);
        return ImmutableMap.of("id", (Object) funding.getId());
    }

    @RequestMapping(value = "/{applicationId}/fundings/{fundingId}", method = RequestMethod.PUT)
    public void updateFunding(@PathVariable Integer applicationId, @PathVariable Integer fundingId, @Valid @RequestBody ApplicationFundingDTO fundingDTO) {
        applicationService.saveFunding(applicationId, fundingId, fundingDTO);
    }

    @RequestMapping(value = "/{applicationId}/fundings/{fundingId}", method = RequestMethod.DELETE)
    public void updateFunding(@PathVariable Integer applicationId, @PathVariable Integer fundingId) {
        applicationService.deleteFunding(applicationId, fundingId);
    }

    @RequestMapping(value = "/{applicationId}/referees", method = RequestMethod.POST)
    public Map<String, Object> createRreferee(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationRefereeDTO refereeDTO) {
        ApplicationReferee referee = applicationService.saveReferee(applicationId, null, refereeDTO);
        return ImmutableMap.of("id", (Object) referee.getId());
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.PUT)
    public void updateReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId, @Valid @RequestBody ApplicationRefereeDTO refereeDTO) {
        applicationService.saveReferee(applicationId, refereeId, refereeDTO);
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.DELETE)
    public void updateReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId) {
        applicationService.deleteReferee(applicationId, refereeId);
    }

    @RequestMapping(value = "/{applicationId}/additionalInformation", method = RequestMethod.PUT)
    public void saveAdditionalInformation(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationAdditionalInformationDTO additionalInformationDTO) {
        applicationService.saveAdditionalInformation(applicationId, additionalInformationDTO);
    }

    @RequestMapping(value = "/{applicationId}/comments", method = RequestMethod.POST)
    public ActionOutcomeRepresentation performAction(@PathVariable Integer applicationId, @Valid @RequestBody CommentDTO commentDTO)
            throws WorkflowEngineException {
        Application application = entityService.getById(Application.class, applicationId);
        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());
        Institution institution = application.getInstitution();
        ResidenceState residenceState = importedEntitytService.getByCode(ResidenceState.class, institution, commentDTO.getResidenceState());
        RejectionReason rejectionReason = importedEntitytService.getByCode(RejectionReason.class, institution, commentDTO.getRejectionReason());
        LocalDate positionProvisionalStartDate = commentDTO.getPositionProvisionalStartDate() == null ? null : commentDTO.getPositionProvisionalStartDate()
                .withAction(action).withTransitionState(transitionState).withCreatedTimestamp(new DateTime()).withDeclinedResponse(BooleanUtils.isTrue(commentDTO.getDeclinedResponse()))
        Comment comment = new Comment().withContent(commentDTO.getContent()).withUser(userService.getCurrentUser()).withAction(action)
                .withCreatedTimestamp(new DateTime()).withDeclinedResponse(BooleanUtils.isTrue(commentDTO.getDeclinedResponse()))
                .withQualified(commentDTO.getQualified()).withCompetentInWorkLanguage(commentDTO.getCompetentInWorkLanguage())
                .withResidenceState(residenceState).withInterviewDateTime(commentDTO.getInterviewDateTime())
                .withInterviewTimeZone(commentDTO.getInterviewTimeZone()).withInterviewDuration(commentDTO.getInterviewDuration())
                .withInterviewerInstructions(commentDTO.getInterviewerInstructions()).withIntervieweeInstructions(commentDTO.getIntervieweeInstructions())
                .withInterviewLocation(commentDTO.getInterviewLocation()).withSuitableForInstitution(commentDTO.getSuitableForInstitution())
                .withSuitableForOpportunity(commentDTO.getSuitableForOpportunity()).withDesireToInterview(commentDTO.getDesireToInterview())
                .withDesireToRecruit(commentDTO.getDesireToRecruit()).withPositionTitle(commentDTO.getPositionTitle())
                .withPositionDescription(commentDTO.getPositionDescription()).withPositionProvisionalStartDate(positionProvisionalStartDate)
                .withAppointmentConditions(commentDTO.getAppointmentConditions());

        if (commentDTO.getRejectionReason() != null) {
            RejectionReason rejectionReason = entityService.getById(RejectionReason.class, commentDTO.getRejectionReason());
            comment.setContent(rejectionReason.getName());
        }
        if (commentDTO.getAppointmentTimeslots() != null) {
            for (DateTime dateTime : commentDTO.getAppointmentTimeslots()) {
                CommentAppointmentTimeslot timeslot = new CommentAppointmentTimeslot();
                timeslot.setDateTime(dateTime);
                comment.getAppointmentTimeslots().add(timeslot);
            }
        }
        if (commentDTO.getAppointmentPreferences() != null) {
            for (Integer timeslotId : commentDTO.getAppointmentPreferences()) {
                CommentAppointmentTimeslot timeslot = entityService.getById(CommentAppointmentTimeslot.class, timeslotId);
                comment.getAppointmentPreferences().add(new CommentAppointmentPreference().withAppointmentTimeslot(timeslot));
            }
        }

        List<CommentAssignedUser> assignedUsers = Lists.newLinkedList();
        if (actionId.equals(PrismAction.APPLICATION_COMPLETE)) {
            Role refereeRole = entityService.getById(Role.class, PrismRole.APPLICATION_REFEREE);
            for (ApplicationReferee referee : application.getReferees()) {
                assignedUsers.add(new CommentAssignedUser().withComment(comment).withUser(referee.getUser()).withRole(refereeRole));
            }
            Role supervisorRole = entityService.getById(Role.class, PrismRole.APPLICATION_SUGGESTED_SUPERVISOR);
            for (ApplicationSupervisor supervisor : application.getProgramDetails().getSupervisors()) {
                assignedUsers.add(new CommentAssignedUser().withComment(comment).withUser(supervisor.getUser()).withRole(supervisorRole));
            }
        } else if (commentDTO.getAssignedUsers() != null) {
            for (CommentAssignedUserDTO assignedUserDTO : commentDTO.getAssignedUsers()) {
                UserDTO userDTO = assignedUserDTO.getUser();
                User user = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
                assignedUsers.add(new CommentAssignedUser().withUser(user).withRole(entityService.getById(Role.class, assignedUserDTO.getRole())));
            }
        }

        comment.getAssignedUsers().addAll(assignedUsers);

        ActionOutcome actionOutcome = actionService.executeUserAction(application, action, comment);
        return dozerBeanMapper.map(actionOutcome, ActionOutcomeRepresentation.class);
    }

    @InitBinder(value = "commentDTO")
    public void configureCommentBinding(WebDataBinder binder) {
        binder.setValidator(commentDTOValidator);
    }
}
