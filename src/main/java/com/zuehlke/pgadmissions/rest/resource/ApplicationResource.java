package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.application.*;
import com.zuehlke.pgadmissions.rest.representation.ActionOutcomeRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.comment.CommentDTOValidator;
import com.zuehlke.pgadmissions.services.ApplicationSectionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(value = {"api/applications"})
public class ApplicationResource {

    @Autowired
    private ApplicationSectionService applicationSectionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private Mapper dozerBeanMapper;

    @Autowired
    private CommentDTOValidator commentDTOValidator;

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/{applicationId}/programDetail", method = RequestMethod.PUT)
    public void saveProgramDetail(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationProgramDetailDTO programDetailDTO) {
        try {
            applicationSectionService.saveProgramDetail(applicationId, programDetailDTO);
        } catch (DeduplicationException e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/{applicationId}/supervisors", method = RequestMethod.POST)
    public Map<String, Object> createRsupervisor(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationSupervisorDTO supervisorDTO) {
        try {
            ApplicationSupervisor supervisor = applicationSectionService.saveSupervisor(applicationId, null, supervisorDTO);
            return ImmutableMap.of("id", (Object) supervisor.getId());
        } catch (DeduplicationException e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/{applicationId}/supervisors/{supervisorId}", method = RequestMethod.PUT)
    public void deleteSupervisor(@PathVariable Integer applicationId, @PathVariable Integer supervisorId, @Valid @RequestBody ApplicationSupervisorDTO supervisorDTO) {
        try {
            applicationSectionService.saveSupervisor(applicationId, supervisorId, supervisorDTO);
        } catch (DeduplicationException e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/{applicationId}/supervisors/{supervisorId}", method = RequestMethod.DELETE)
    public void updateSupervisor(@PathVariable Integer applicationId, @PathVariable Integer supervisorId) {
        applicationSectionService.deleteSupervisor(applicationId, supervisorId);
    }


    @RequestMapping(value = "/{applicationId}/personalDetail", method = RequestMethod.PUT)
    public void savePersonalDetail(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationPersonalDetailDTO personalDetailDTO) {
        applicationSectionService.savePersonalDetail(applicationId, personalDetailDTO);
    }

    @RequestMapping(value = "/{applicationId}/address", method = RequestMethod.PUT)
    public void saveAddress(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationAddressDTO addressDTO) {
        applicationSectionService.saveAddress(applicationId, addressDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications", method = RequestMethod.POST)
    public Map<String, Object> createQualification(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationQualificationDTO qualificationDTO) {
        ApplicationQualification qualification = applicationSectionService.saveQualification(applicationId, null, qualificationDTO);
        return ImmutableMap.of("id", (Object) qualification.getId());
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.PUT)
    public void updateQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId,
                                    @Valid @RequestBody ApplicationQualificationDTO qualificationDTO) {
        applicationSectionService.saveQualification(applicationId, qualificationId, qualificationDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.DELETE)
    public void deleteQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId) {
        applicationSectionService.deleteQualification(applicationId, qualificationId);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions", method = RequestMethod.POST)
    public Map<String, Object> createEmploymentPosition(@PathVariable Integer applicationId,
                                                        @Valid @RequestBody ApplicationEmploymentPositionDTO employmentPositionDTO) {
        ApplicationEmploymentPosition employmentPosition = applicationSectionService.saveEmploymentPosition(applicationId, null, employmentPositionDTO);
        return ImmutableMap.of("id", (Object) employmentPosition.getId());
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.PUT)
    public void updateEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId,
                                         @Valid @RequestBody ApplicationEmploymentPositionDTO employmentPositionDTO) {
        applicationSectionService.saveEmploymentPosition(applicationId, employmentPositionId, employmentPositionDTO);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.DELETE)
    public void deleteEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId) {
        applicationSectionService.deleteEmploymentPosition(applicationId, employmentPositionId);
    }

    @RequestMapping(value = "/{applicationId}/fundings", method = RequestMethod.POST)
    public Map<String, Object> createFunding(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationFundingDTO fundingDTO) {
        ApplicationFunding funding = applicationSectionService.saveFunding(applicationId, null, fundingDTO);
        return ImmutableMap.of("id", (Object) funding.getId());
    }

    @RequestMapping(value = "/{applicationId}/fundings/{fundingId}", method = RequestMethod.PUT)
    public void updateFunding(@PathVariable Integer applicationId, @PathVariable Integer fundingId, @Valid @RequestBody ApplicationFundingDTO fundingDTO) {
        applicationSectionService.saveFunding(applicationId, fundingId, fundingDTO);
    }

    @RequestMapping(value = "/{applicationId}/fundings/{fundingId}", method = RequestMethod.DELETE)
    public void deleteFunding(@PathVariable Integer applicationId, @PathVariable Integer fundingId) {
        applicationSectionService.deleteFunding(applicationId, fundingId);
    }

    @RequestMapping(value = "/{applicationId}/referees", method = RequestMethod.POST)
    public Map<String, Object> createRreferee(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationRefereeDTO refereeDTO) {
        try {
            ApplicationReferee referee = applicationSectionService.saveReferee(applicationId, null, refereeDTO);
            return ImmutableMap.of("id", (Object) referee.getId());
        } catch (DeduplicationException e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.PUT)
    public void deleteReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId, @Valid @RequestBody ApplicationRefereeDTO refereeDTO) {
        try {
            applicationSectionService.saveReferee(applicationId, refereeId, refereeDTO);
        } catch (DeduplicationException e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.DELETE)
    public void updateReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId) {
        applicationSectionService.deleteReferee(applicationId, refereeId);
    }

    @RequestMapping(value = "/{applicationId}/additionalInformation", method = RequestMethod.PUT)
    public void saveAdditionalInformation(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationAdditionalInformationDTO additionalInformationDTO) {
        applicationSectionService.saveAdditionalInformation(applicationId, additionalInformationDTO);
    }


    @RequestMapping(value = "/{applicationId}/comments", method = RequestMethod.POST)
    public ActionOutcomeRepresentation performAction(@PathVariable Integer applicationId, @Valid @RequestBody CommentDTO commentDTO) {
        try {
            ActionOutcomeDTO actionOutcome = applicationService.performAction(applicationId, commentDTO);
            return dozerBeanMapper.map(actionOutcome, ActionOutcomeRepresentation.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "/{applicationId}/comments/{commentId}", method = RequestMethod.PUT)
    public void updateComment(@PathVariable Integer applicationId, @PathVariable Integer commentId, @RequestBody CommentDTO commentDTO) {
        Comment comment = commentService.getById(commentId);
        Preconditions.checkArgument(comment.getApplication().getId().equals(applicationId));
        commentService.updateComment(commentId, commentDTO);
    }

    @InitBinder(value = "commentDTO")
    public void configureCommentBinding(WebDataBinder binder) {
        binder.setValidator(commentDTOValidator);
    }
}
