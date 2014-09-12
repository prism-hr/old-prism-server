package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.dto.CommentAssignedUserDTO;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.UserDTO;
import com.zuehlke.pgadmissions.rest.dto.application.*;
import com.zuehlke.pgadmissions.rest.representation.ActionOutcomeRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.CompleteApplicationValidator;
import com.zuehlke.pgadmissions.rest.validation.validator.comment.CommentDTOValidator;
import com.zuehlke.pgadmissions.services.*;
import org.apache.commons.lang.BooleanUtils;
import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = {"api/applications"})
public class ApplicationResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private ImportedEntityService importedEntityService;

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
    private CommentService commentService;

    @Autowired
    private CompleteApplicationValidator completeApplicationValidator;

    @RequestMapping(value = "/{applicationId}/programDetail", method = RequestMethod.PUT)
    public void saveProgramDetail(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationProgramDetailDTO programDetailDTO) {
        try {
            applicationService.saveProgramDetail(applicationId, programDetailDTO);
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/{applicationId}/personalDetail", method = RequestMethod.PUT)
    public void savePersonalDetail(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationPersonalDetailDTO personalDetailDTO) {
        applicationService.savePersonalDetail(applicationId, personalDetailDTO);
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
    public void deleteQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId) {
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
    public void deleteEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId) {
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
    public void deleteFunding(@PathVariable Integer applicationId, @PathVariable Integer fundingId) {
        applicationService.deleteFunding(applicationId, fundingId);
    }

    @RequestMapping(value = "/{applicationId}/referees", method = RequestMethod.POST)
    public Map<String, Object> createRreferee(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationRefereeDTO refereeDTO) {
        try {
            ApplicationReferee referee = applicationService.saveReferee(applicationId, null, refereeDTO);
            return ImmutableMap.of("id", (Object) referee.getId());
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.PUT)
    public void deleteReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId, @Valid @RequestBody ApplicationRefereeDTO refereeDTO) {
        try {
            applicationService.saveReferee(applicationId, refereeId, refereeDTO);
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.DELETE)
    public void updateReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId) {
        applicationService.deleteReferee(applicationId, refereeId);
    }

    @RequestMapping(value = "/{applicationId}/additionalInformation", method = RequestMethod.PUT)
    public void saveAdditionalInformation(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationAdditionalInformationDTO additionalInformationDTO) {
        applicationService.saveAdditionalInformation(applicationId, additionalInformationDTO);
    }

    // TODO: set values for "doRetain" (application) and "sendRecommendationEmail" (user account)
    @RequestMapping(value = "/{applicationId}/comments", method = RequestMethod.POST)
    public ActionOutcomeRepresentation performAction(@PathVariable Integer applicationId, @Valid @RequestBody CommentDTO commentDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        PrismAction actionId = commentDTO.getAction();

        if (actionId == PrismAction.APPLICATION_COMPLETE) {
            applicationService.validateApplicationCompleteness(applicationId);
        }

        Action action = actionService.getById(actionId);
        User user = userService.getById(commentDTO.getUser());
        User delegateUser = userService.getById(commentDTO.getDelegateUser());
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());
        Institution institution = application.getInstitution();
        LocalDate positionProvisionalStartDate = commentDTO.getPositionProvisionalStartDate();
        Comment comment = new Comment().withContent(commentDTO.getContent()).withUser(user).withDelegateUser(delegateUser).withAction(action)
                .withTransitionState(transitionState).withCreatedTimestamp(new DateTime())
                .withDeclinedResponse(BooleanUtils.isTrue(commentDTO.getDeclinedResponse())).withQualified(commentDTO.getQualified())
                .withCompetentInWorkLanguage(commentDTO.getCompetentInWorkLanguage())
                .withInterviewDateTime(commentDTO.getInterviewDateTime()).withInterviewTimeZone(commentDTO.getInterviewTimeZone())
                .withInterviewDuration(commentDTO.getInterviewDuration()).withInterviewerInstructions(commentDTO.getInterviewerInstructions())
                .withIntervieweeInstructions(commentDTO.getIntervieweeInstructions()).withInterviewLocation(commentDTO.getInterviewLocation())
                .withSuitableForInstitution(commentDTO.getSuitableForInstitution()).withSuitableForOpportunity(commentDTO.getSuitableForOpportunity())
                .withDesireToInterview(commentDTO.getDesireToInterview()).withDesireToRecruit(commentDTO.getDesireToRecruit())
                .withPositionTitle(commentDTO.getPositionTitle()).withPositionDescription(commentDTO.getPositionDescription())
                .withPositionProvisionalStartDate(positionProvisionalStartDate).withAppointmentConditions(commentDTO.getAppointmentConditions());

        if (commentDTO.getResidenceState() != null) {
            ResidenceState residenceState = entityService.getById(ResidenceState.class, commentDTO.getResidenceState());
            comment.setResidenceState(residenceState);
        }

        if (commentDTO.getDocuments() != null) {
            for (FileDTO fileDTO : commentDTO.getDocuments()) {
                Document document = entityService.getById(Document.class, fileDTO.getId());
                comment.getDocuments().add(document);
            }
        }
        if (commentDTO.getRejectionReason() != null) {
            RejectionReason rejectionReason = entityService.getById(RejectionReason.class, commentDTO.getRejectionReason());
            comment.setContent(rejectionReason.getName());
        }
        if (commentDTO.getAppointmentTimeslots() != null) {
            for (LocalDateTime dateTime : commentDTO.getAppointmentTimeslots()) {
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
            for (ApplicationSupervisor supervisor : application.getProgramDetail().getSupervisors()) {
                assignedUsers.add(new CommentAssignedUser().withComment(comment).withUser(supervisor.getUser()).withRole(supervisorRole));
            }
        } else if (commentDTO.getAssignedUsers() != null) {
            for (CommentAssignedUserDTO assignedUserDTO : commentDTO.getAssignedUsers()) {
                UserDTO commentUserDTO = assignedUserDTO.getUser();

                try {
                    User commentUser = userService.getOrCreateUser(commentUserDTO.getFirstName(), commentUserDTO.getLastName(), commentUserDTO.getEmail());
                    assignedUsers.add(new CommentAssignedUser().withUser(commentUser).withRole(entityService.getById(Role.class, assignedUserDTO.getRole())));
                } catch (Exception e) {
                    throw new ResourceNotFoundException();
                }
            }
        }

        comment.getAssignedUsers().addAll(assignedUsers);

        try {
            ActionOutcomeDTO actionOutcome = actionService.executeUserAction(application, action, comment);
            return dozerBeanMapper.map(actionOutcome, ActionOutcomeRepresentation.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/{applicationId}/comments/{commentId}", method = RequestMethod.PUT)
    public void updateComment(@PathVariable Integer applicationId, @PathVariable Integer commentId, @RequestBody CommentDTO commentDTO) {
        Comment comment = commentService.getById(commentId);
        Preconditions.checkArgument(comment.getApplication().getId() == applicationId);
        commentService.updateComment(commentId, commentDTO);
    }

    @InitBinder(value = "commentDTO")
    public void configureCommentBinding(WebDataBinder binder) {
        binder.setValidator(commentDTOValidator);
    }
}
