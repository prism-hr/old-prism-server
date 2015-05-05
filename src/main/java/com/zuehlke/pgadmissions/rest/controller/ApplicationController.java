package com.zuehlke.pgadmissions.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.Valid;

import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.application.ApplicationPrize;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyLocation;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOption;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAdditionalInformationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDocumentDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationEmploymentPositionDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationFundingDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPersonalDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPrizeDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationProgramDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationRefereeDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationStudyDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationSupervisorDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationStartDateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.RefereeRepresentation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationSectionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping(value = { "api/applications" })
@PreAuthorize("isAuthenticated()")
public class ApplicationController {

    @Autowired
    private ActionService actionService;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationSectionService applicationSectionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private OpportunityController opportunityController;

    @Inject
    private ResourceService resourceService;

    @Autowired
    private Mapper beanMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/{applicationId}/startDate", method = RequestMethod.GET)
    public ApplicationStartDateRepresentation getStartDateRepresentation(@PathVariable Integer applicationId, @RequestParam PrismStudyOption studyOptionId) {
        return applicationService.getStartDateRepresentation(applicationId, studyOptionId);
    }

    @RequestMapping(value = "/{applicationId}/programDetail", method = RequestMethod.PUT)
    public void saveProgramDetail(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationProgramDetailDTO programDetailDTO) throws Exception {
        applicationSectionService.updateProgramDetail(applicationId, programDetailDTO);
    }

    @RequestMapping(value = "/{applicationId}/studyDetail", method = RequestMethod.PUT)
    public void saveStudyDetail(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationStudyDetailDTO studyDetailDTO) throws Exception {
        applicationSectionService.updateStudyDetail(applicationId, studyDetailDTO);
    }

    @RequestMapping(value = "/{applicationId}/supervisors", method = RequestMethod.POST)
    public Map<String, Object> createSupervisor(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationSupervisorDTO supervisorDTO)
            throws Exception {
        ApplicationSupervisor supervisor = applicationSectionService.updateSupervisor(applicationId, null, supervisorDTO);
        return ImmutableMap.of("id", (Object) supervisor.getId());
    }

    @RequestMapping(value = "/{applicationId}/supervisors/{supervisorId}", method = RequestMethod.PUT)
    public void deleteSupervisor(@PathVariable Integer applicationId, @PathVariable Integer supervisorId,
            @Valid @RequestBody ApplicationSupervisorDTO supervisorDTO) throws Exception {
        applicationSectionService.updateSupervisor(applicationId, supervisorId, supervisorDTO);
    }

    @RequestMapping(value = "/{applicationId}/supervisors/{supervisorId}", method = RequestMethod.DELETE)
    public void updateSupervisor(@PathVariable Integer applicationId, @PathVariable Integer supervisorId) throws Exception {
        applicationSectionService.deleteSupervisor(applicationId, supervisorId);
    }

    @RequestMapping(value = "/{applicationId}/personalDetail", method = RequestMethod.PUT)
    public void savePersonalDetail(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationPersonalDetailDTO personalDetailDTO) throws Exception {
        applicationSectionService.updatePersonalDetail(applicationId, personalDetailDTO);
    }

    @RequestMapping(value = "/{applicationId}/address", method = RequestMethod.PUT)
    public void saveAddress(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationAddressDTO addressDTO) throws Exception {
        applicationSectionService.updateAddress(applicationId, addressDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications", method = RequestMethod.POST)
    public Map<String, Object> createQualification(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationQualificationDTO qualificationDTO)
            throws Exception {
        ApplicationQualification qualification = applicationSectionService.updateQualification(applicationId, null, qualificationDTO);
        return ImmutableMap.of("id", (Object) qualification.getId());
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.PUT)
    public void updateQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId,
            @Valid @RequestBody ApplicationQualificationDTO qualificationDTO) throws Exception {
        applicationSectionService.updateQualification(applicationId, qualificationId, qualificationDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.DELETE)
    public void deleteQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId) throws Exception {
        applicationSectionService.deleteQualification(applicationId, qualificationId);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions", method = RequestMethod.POST)
    public Map<String, Object> createEmploymentPosition(@PathVariable Integer applicationId,
            @Valid @RequestBody ApplicationEmploymentPositionDTO employmentPositionDTO) throws Exception {
        ApplicationEmploymentPosition employmentPosition = applicationSectionService.updateEmploymentPosition(applicationId, null, employmentPositionDTO);
        return ImmutableMap.of("id", (Object) employmentPosition.getId());
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.PUT)
    public void updateEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId,
            @Valid @RequestBody ApplicationEmploymentPositionDTO employmentPositionDTO) throws Exception {
        applicationSectionService.updateEmploymentPosition(applicationId, employmentPositionId, employmentPositionDTO);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.DELETE)
    public void deleteEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId) throws Exception {
        applicationSectionService.deleteEmploymentPosition(applicationId, employmentPositionId);
    }

    @RequestMapping(value = "/{applicationId}/fundings", method = RequestMethod.POST)
    public Map<String, Object> createFunding(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationFundingDTO fundingDTO) throws Exception {
        ApplicationFunding funding = applicationSectionService.updateFunding(applicationId, null, fundingDTO);
        return ImmutableMap.of("id", (Object) funding.getId());
    }

    @RequestMapping(value = "/{applicationId}/fundings/{fundingId}", method = RequestMethod.PUT)
    public void updateFunding(@PathVariable Integer applicationId, @PathVariable Integer fundingId, @Valid @RequestBody ApplicationFundingDTO fundingDTO)
            throws Exception {
        applicationSectionService.updateFunding(applicationId, fundingId, fundingDTO);
    }

    @RequestMapping(value = "/{applicationId}/fundings/{fundingId}", method = RequestMethod.DELETE)
    public void deleteFunding(@PathVariable Integer applicationId, @PathVariable Integer fundingId) throws Exception {
        applicationSectionService.deleteFunding(applicationId, fundingId);
    }

    @RequestMapping(value = "/{applicationId}/prizes", method = RequestMethod.POST)
    public Map<String, Object> createPrize(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationPrizeDTO prizeDTO) throws Exception {
        ApplicationPrize prize = applicationSectionService.updatePrize(applicationId, null, prizeDTO);
        return ImmutableMap.of("id", (Object) prize.getId());
    }

    @RequestMapping(value = "/{applicationId}/prizes/{prizeId}", method = RequestMethod.PUT)
    public void updatePrize(@PathVariable Integer applicationId, @PathVariable Integer prizeId, @Valid @RequestBody ApplicationPrizeDTO prizeDTO)
            throws Exception {
        applicationSectionService.updatePrize(applicationId, prizeId, prizeDTO);
    }

    @RequestMapping(value = "/{applicationId}/prizes/{prizeId}", method = RequestMethod.DELETE)
    public void deletePrize(@PathVariable Integer applicationId, @PathVariable Integer prizeId) throws Exception {
        applicationSectionService.deletePrize(applicationId, prizeId);
    }

    @RequestMapping(value = "/{applicationId}/referees", method = RequestMethod.POST)
    public Map<String, Object> createReferee(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationRefereeDTO refereeDTO) throws Exception {
        ApplicationReferee referee = applicationSectionService.updateReferee(applicationId, null, refereeDTO);
        return ImmutableMap.of("id", (Object) referee.getId());
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.PUT)
    public void deleteReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId, @Valid @RequestBody ApplicationRefereeDTO refereeDTO)
            throws Exception {
        applicationSectionService.updateReferee(applicationId, refereeId, refereeDTO);
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.DELETE)
    public void updateReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId) throws Exception {
        applicationSectionService.deleteReferee(applicationId, refereeId);
    }

    @RequestMapping(value = "/{applicationId}/document", method = RequestMethod.PUT)
    public void saveDocument(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationDocumentDTO documentDTO) throws Exception {
        applicationSectionService.updateDocument(applicationId, documentDTO);
    }

    @RequestMapping(value = "/{applicationId}/additionalInformation", method = RequestMethod.PUT)
    public void saveAdditionalInformation(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationAdditionalInformationDTO additionalInformationDTO)
            throws Exception {
        applicationSectionService.updateAdditionalInformation(applicationId, additionalInformationDTO);
    }

    @RequestMapping(value = "/{applicationId}/comments/{commentId}", method = RequestMethod.PUT)
    public void updateComment(@PathVariable Integer applicationId, @PathVariable Integer commentId, @Valid @RequestBody CommentDTO commentDTO) {
        Comment comment = commentService.getById(commentId);
        Preconditions.checkArgument(comment.getApplication().getId().equals(applicationId));
        commentService.update(commentId, commentDTO);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{applicationId}", params = "type=summary")
    public ApplicationSummaryRepresentation getSummary(@PathVariable Integer applicationId) throws Exception {
        return applicationService.getApplicationSummary(applicationId);
    }

	public void enrichApplicationRepresentation(Application application, ApplicationExtendedRepresentation representation) throws Exception {
        HashMap<Integer, RefereeRepresentation> refereeRepresentations = Maps.newHashMap();
        for (RefereeRepresentation refereeRepresentation : representation.getReferees()) {
            refereeRepresentations.put(refereeRepresentation.getId(), refereeRepresentation);
        }

        for (ApplicationReferee referee : application.getReferees()) {
            Comment reference = referee.getComment();
            refereeRepresentations.get(referee.getId()).setCommentId(reference == null ? null : reference.getId());
        }

        List<UserSelectionDTO> interested = userService.getUsersInterestedInApplication(application);
        List<UserSelectionDTO> potentiallyInterested = userService.getUsersPotentiallyInterestedInApplication(application, interested);
        List<UserRepresentation> interestedRepresentations = Lists.newArrayListWithCapacity(interested.size());
        List<UserRepresentation> potentiallyInterestedRepresentations = Lists.newArrayListWithCapacity(potentiallyInterested.size());

        for (UserSelectionDTO user : interested) {
            interestedRepresentations.add(beanMapper.map(user, UserRepresentation.class));
        }

        for (UserSelectionDTO user : potentiallyInterested) {
            potentiallyInterestedRepresentations.add(beanMapper.map(user, UserRepresentation.class));
        }

        representation.setUsersInterestedInApplication(interestedRepresentations);
        representation.setUsersPotentiallyInterestedInApplication(potentiallyInterestedRepresentations);

        representation.setInterview(commentService.getInterview(application));

        representation.setOfferRecommendation(commentService.getOfferRecommendation(application));
        representation.setAssignedSupervisors(commentService.getApplicationSupervisors(application));

        representation.setPossibleThemes(advertService.getAdvertThemes(application));

        ResourceParent parent = (ResourceParent) application.getParentResource();

        List<ResourceStudyLocation> studyLocations = resourceService.getStudyLocations(parent);
        List<String> availableStudyLocations = Lists.newArrayListWithCapacity(studyLocations.size());
        for (ResourceStudyLocation studyLocation : studyLocations) {
            availableStudyLocations.add(studyLocation.getStudyLocation());
        }
        representation.setPossibleLocations(availableStudyLocations);

        if(ResourceOpportunity.class.isAssignableFrom(parent.getResourceScope().getResourceClass())){
            representation.setAvailableStudyOptions(resourceService.getStudyOptions((ResourceOpportunity) parent));
        }

        if (!actionService.hasRedactions(application, userService.getCurrentUser())) {
            representation.setApplicationRatingAverage(application.getApplicationRatingAverage());
        }

        representation.setResourceSummary(applicationService.getApplicationSummary(application.getId()));
        representation.setRecommendedAdverts(opportunityController.getRecommendedAdverts(application.getId()));
    }

}
