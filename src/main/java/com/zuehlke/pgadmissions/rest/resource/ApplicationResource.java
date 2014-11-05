package com.zuehlke.pgadmissions.rest.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAdditionalInformationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationEmploymentPositionDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationFundingDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPersonalDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationProgramDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationRefereeDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationSupervisorDTO;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationStartDateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.RefereeRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.comment.CommentDTOValidator;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationSectionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping(value = { "api/applications" })
public class ApplicationResource {

    @Autowired
    private AdvertService advertService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationSectionService applicationSectionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private UserService userService;

    @Autowired
    private Mapper dozerBeanMapper;

    @Autowired
    private CommentDTOValidator commentDTOValidator;

    @RequestMapping(value = "/{applicationId}/startDate", method = RequestMethod.GET)
    private ApplicationStartDateRepresentation getStartDateRepresentation(@PathVariable Integer applicationId, @RequestParam PrismStudyOption studyOptionId) {
        return applicationService.getStartDateRepresentation(applicationId, studyOptionId);
    }

    @RequestMapping(value = "/{applicationId}/programDetail", method = RequestMethod.PUT)
    public void saveProgramDetail(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationProgramDetailDTO programDetailDTO)
            throws DeduplicationException {
        applicationSectionService.updateProgramDetail(applicationId, programDetailDTO);
    }

    @RequestMapping(value = "/{applicationId}/supervisors", method = RequestMethod.POST)
    public Map<String, Object> createSupervisor(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationSupervisorDTO supervisorDTO)
            throws DeduplicationException {
        ApplicationSupervisor supervisor = applicationSectionService.updateSupervisor(applicationId, null, supervisorDTO);
        return ImmutableMap.of("id", (Object) supervisor.getId());
    }

    @RequestMapping(value = "/{applicationId}/supervisors/{supervisorId}", method = RequestMethod.PUT)
    public void deleteSupervisor(@PathVariable Integer applicationId, @PathVariable Integer supervisorId,
            @Valid @RequestBody ApplicationSupervisorDTO supervisorDTO) throws DeduplicationException {
        applicationSectionService.updateSupervisor(applicationId, supervisorId, supervisorDTO);
    }

    @RequestMapping(value = "/{applicationId}/supervisors/{supervisorId}", method = RequestMethod.DELETE)
    public void updateSupervisor(@PathVariable Integer applicationId, @PathVariable Integer supervisorId) throws DeduplicationException {
        applicationSectionService.deleteSupervisor(applicationId, supervisorId);
    }

    @RequestMapping(value = "/{applicationId}/personalDetail", method = RequestMethod.PUT)
    public void savePersonalDetail(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationPersonalDetailDTO personalDetailDTO)
            throws DeduplicationException {
        applicationSectionService.updatePersonalDetail(applicationId, personalDetailDTO);
    }

    @RequestMapping(value = "/{applicationId}/address", method = RequestMethod.PUT)
    public void saveAddress(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationAddressDTO addressDTO) throws DeduplicationException {
        applicationSectionService.updateAddress(applicationId, addressDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications", method = RequestMethod.POST)
    public Map<String, Object> createQualification(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationQualificationDTO qualificationDTO)
            throws DeduplicationException {
        ApplicationQualification qualification = applicationSectionService.updateQualification(applicationId, null, qualificationDTO);
        return ImmutableMap.of("id", (Object) qualification.getId());
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.PUT)
    public void updateQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId,
            @Valid @RequestBody ApplicationQualificationDTO qualificationDTO) throws DeduplicationException {
        applicationSectionService.updateQualification(applicationId, qualificationId, qualificationDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.DELETE)
    public void deleteQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId) throws DeduplicationException {
        applicationSectionService.deleteQualification(applicationId, qualificationId);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions", method = RequestMethod.POST)
    public Map<String, Object> createEmploymentPosition(@PathVariable Integer applicationId,
            @Valid @RequestBody ApplicationEmploymentPositionDTO employmentPositionDTO) throws DeduplicationException {
        ApplicationEmploymentPosition employmentPosition = applicationSectionService.updateEmploymentPosition(applicationId, null, employmentPositionDTO);
        return ImmutableMap.of("id", (Object) employmentPosition.getId());
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.PUT)
    public void updateEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId,
            @Valid @RequestBody ApplicationEmploymentPositionDTO employmentPositionDTO) throws DeduplicationException {
        applicationSectionService.updateEmploymentPosition(applicationId, employmentPositionId, employmentPositionDTO);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.DELETE)
    public void deleteEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId) throws DeduplicationException {
        applicationSectionService.deleteEmploymentPosition(applicationId, employmentPositionId);
    }

    @RequestMapping(value = "/{applicationId}/fundings", method = RequestMethod.POST)
    public Map<String, Object> createFunding(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationFundingDTO fundingDTO)
            throws DeduplicationException {
        ApplicationFunding funding = applicationSectionService.updateFunding(applicationId, null, fundingDTO);
        return ImmutableMap.of("id", (Object) funding.getId());
    }

    @RequestMapping(value = "/{applicationId}/fundings/{fundingId}", method = RequestMethod.PUT)
    public void updateFunding(@PathVariable Integer applicationId, @PathVariable Integer fundingId, @Valid @RequestBody ApplicationFundingDTO fundingDTO)
            throws DeduplicationException {
        applicationSectionService.updateFunding(applicationId, fundingId, fundingDTO);
    }

    @RequestMapping(value = "/{applicationId}/fundings/{fundingId}", method = RequestMethod.DELETE)
    public void deleteFunding(@PathVariable Integer applicationId, @PathVariable Integer fundingId) throws DeduplicationException {
        applicationSectionService.deleteFunding(applicationId, fundingId);
    }

    @RequestMapping(value = "/{applicationId}/referees", method = RequestMethod.POST)
    public Map<String, Object> createReferee(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationRefereeDTO refereeDTO)
            throws DeduplicationException {
        ApplicationReferee referee = applicationSectionService.updateReferee(applicationId, null, refereeDTO);
        return ImmutableMap.of("id", (Object) referee.getId());
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.PUT)
    public void deleteReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId, @Valid @RequestBody ApplicationRefereeDTO refereeDTO)
            throws DeduplicationException {
        applicationSectionService.updateReferee(applicationId, refereeId, refereeDTO);
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.DELETE)
    public void updateReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId) throws DeduplicationException {
        applicationSectionService.deleteReferee(applicationId, refereeId);
    }

    @RequestMapping(value = "/{applicationId}/additionalInformation", method = RequestMethod.PUT)
    public void saveAdditionalInformation(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationAdditionalInformationDTO additionalInformationDTO)
            throws DeduplicationException {
        applicationSectionService.updateAdditionalInformation(applicationId, additionalInformationDTO);
    }

    @RequestMapping(value = "/{applicationId}/comments/{commentId}", method = RequestMethod.PUT)
    public void updateComment(@PathVariable Integer applicationId, @PathVariable Integer commentId, @RequestBody CommentDTO commentDTO) {
        Comment comment = commentService.getById(commentId);
        Preconditions.checkArgument(comment.getApplication().getId().equals(applicationId));
        commentService.update(commentId, commentDTO);
    }

    @InitBinder(value = "commentDTO")
    public void configureCommentBinding(WebDataBinder binder) {
        binder.setValidator(commentDTOValidator);
    }

    public void enrichApplicationRepresentation(Application application, ApplicationExtendedRepresentation representation) {
        HashMap<Integer, RefereeRepresentation> refereeRepresentations = Maps.newHashMap();
        for (RefereeRepresentation refereeRepresentation : representation.getReferees()) {
            refereeRepresentations.put(refereeRepresentation.getId(), refereeRepresentation);
        }

        for (ApplicationReferee referee : application.getReferees()) {
            Comment reference = referee.getComment();
            refereeRepresentations.get(referee.getId()).setCommentId(reference == null ? null : reference.getId());
        }

        List<User> interested = userService.getUsersInterestedInApplication(application);
        List<User> potentiallyInterested = userService.getUsersPotentiallyInterestedInApplication(application, interested);
        List<UserRepresentation> interestedRepresentations = Lists.newArrayListWithCapacity(interested.size());
        List<UserRepresentation> potentiallyInterestedRepresentations = Lists.newArrayListWithCapacity(potentiallyInterested.size());

        for (User user : interested) {
            interestedRepresentations.add(dozerBeanMapper.map(user, UserRepresentation.class));
        }

        for (User user : potentiallyInterested) {
            potentiallyInterestedRepresentations.add(dozerBeanMapper.map(user, UserRepresentation.class));
        }

        representation.setUsersInterestedInApplication(interestedRepresentations);
        representation.setUsersPotentiallyInterestedInApplication(potentiallyInterestedRepresentations);

        representation.setAppointmentTimeslots(commentService.getAppointmentTimeslots(application));
        representation.setAppointmentPreferences(commentService.getAppointmentPreferences(application));

        representation.setOfferRecommendation(commentService.getOfferRecommendation(application));
        representation.setAssignedSupervisors(commentService.getApplicationSupervisors(application));
        representation.setPossibleThemes(advertService.getLocalizedThemes(application));

        List<ProgramStudyOption> enabledProgramStudyOptions = programService.getEnabledProgramStudyOptions(application.getProgram());
        List<PrismStudyOption> availableStudyOptions = Lists.newArrayListWithCapacity(enabledProgramStudyOptions.size());
        for (ProgramStudyOption studyOption : enabledProgramStudyOptions) {
            availableStudyOptions.add(studyOption.getStudyOption().getPrismStudyOption());
        }
        representation.setAvailableStudyOptions(availableStudyOptions);
    }

}
