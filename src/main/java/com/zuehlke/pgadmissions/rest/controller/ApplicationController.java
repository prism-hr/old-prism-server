package com.zuehlke.pgadmissions.rest.controller;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.application.*;
import com.zuehlke.pgadmissions.rest.dto.application.*;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationStartDateRepresentation;
import com.zuehlke.pgadmissions.services.ApplicationSectionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(value = {"api/applications"})
@PreAuthorize("isAuthenticated()")
public class ApplicationController {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ApplicationSectionService applicationSectionService;

    @RequestMapping(value = "/{applicationId}/startDate", method = RequestMethod.GET)
    public ApplicationStartDateRepresentation getStartDateRepresentation(@PathVariable Integer applicationId, @RequestParam Integer studyOptionId) {
        return applicationService.getStartDateRepresentation(applicationId, studyOptionId);
    }

    @RequestMapping(value = "/{applicationId}/programDetail", method = RequestMethod.PUT)
    public void saveProgramDetail(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationProgramDetailDTO programDetailDTO) {
        applicationSectionService.updateProgramDetail(applicationId, programDetailDTO);
    }

    @RequestMapping(value = "/{applicationId}/studyDetail", method = RequestMethod.PUT)
    public void saveStudyDetail(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationStudyDetailDTO studyDetailDTO) {
        applicationSectionService.updateStudyDetail(applicationId, studyDetailDTO);
    }

    @RequestMapping(value = "/{applicationId}/supervisors", method = RequestMethod.POST)
    public Map<String, Object> createSupervisor(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationSupervisorDTO supervisorDTO) {
        ApplicationSupervisor supervisor = applicationSectionService.updateSupervisor(applicationId, null, supervisorDTO);
        return ImmutableMap.of("id", (Object) supervisor.getId());
    }

    @RequestMapping(value = "/{applicationId}/supervisors/{supervisorId}", method = RequestMethod.PUT)
    public void deleteSupervisor(@PathVariable Integer applicationId, @PathVariable Integer supervisorId,
                                 @Valid @RequestBody ApplicationSupervisorDTO supervisorDTO) {
        applicationSectionService.updateSupervisor(applicationId, supervisorId, supervisorDTO);
    }

    @RequestMapping(value = "/{applicationId}/supervisors/{supervisorId}", method = RequestMethod.DELETE)
    public void updateSupervisor(@PathVariable Integer applicationId, @PathVariable Integer supervisorId) {
        applicationSectionService.deleteSupervisor(applicationId, supervisorId);
    }

    @RequestMapping(value = "/{applicationId}/personalDetail", method = RequestMethod.PUT)
    public void savePersonalDetail(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationPersonalDetailDTO personalDetailDTO) {
        applicationSectionService.updatePersonalDetail(applicationId, personalDetailDTO);
    }

    @RequestMapping(value = "/{applicationId}/address", method = RequestMethod.PUT)
    public void saveAddress(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationAddressDTO addressDTO) {
        applicationSectionService.updateAddress(applicationId, addressDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications", method = RequestMethod.POST)
    public Map<String, Object> createQualification(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationQualificationDTO qualificationDTO) {
        ApplicationQualification qualification = applicationSectionService.updateQualification(applicationId, null, qualificationDTO);
        return ImmutableMap.of("id", (Object) qualification.getId());
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.PUT)
    public void updateQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId,
                                    @Valid @RequestBody ApplicationQualificationDTO qualificationDTO) {
        applicationSectionService.updateQualification(applicationId, qualificationId, qualificationDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.DELETE)
    public void deleteQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId) {
        applicationSectionService.deleteQualification(applicationId, qualificationId);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions", method = RequestMethod.POST)
    public Map<String, Object> createEmploymentPosition(
            @PathVariable Integer applicationId, @Valid @RequestBody ApplicationEmploymentPositionDTO employmentPositionDTO) {
        ApplicationEmploymentPosition employmentPosition = applicationSectionService.updateEmploymentPosition(applicationId, null, employmentPositionDTO);
        return ImmutableMap.of("id", (Object) employmentPosition.getId());
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.PUT)
    public void updateEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId,
                                         @Valid @RequestBody ApplicationEmploymentPositionDTO employmentPositionDTO) {
        applicationSectionService.updateEmploymentPosition(applicationId, employmentPositionId, employmentPositionDTO);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.DELETE)
    public void deleteEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId) {
        applicationSectionService.deleteEmploymentPosition(applicationId, employmentPositionId);
    }

    @RequestMapping(value = "/{applicationId}/fundings", method = RequestMethod.POST)
    public Map<String, Object> createFunding(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationFundingDTO fundingDTO) {
        ApplicationFunding funding = applicationSectionService.updateFunding(applicationId, null, fundingDTO);
        return ImmutableMap.of("id", (Object) funding.getId());
    }

    @RequestMapping(value = "/{applicationId}/fundings/{fundingId}", method = RequestMethod.PUT)
    public void updateFunding(@PathVariable Integer applicationId, @PathVariable Integer fundingId, @Valid @RequestBody ApplicationFundingDTO fundingDTO) {
        applicationSectionService.updateFunding(applicationId, fundingId, fundingDTO);
    }

    @RequestMapping(value = "/{applicationId}/fundings/{fundingId}", method = RequestMethod.DELETE)
    public void deleteFunding(@PathVariable Integer applicationId, @PathVariable Integer fundingId) {
        applicationSectionService.deleteFunding(applicationId, fundingId);
    }

    @RequestMapping(value = "/{applicationId}/prizes", method = RequestMethod.POST)
    public Map<String, Object> createPrize(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationPrizeDTO prizeDTO) {
        ApplicationPrize prize = applicationSectionService.updatePrize(applicationId, null, prizeDTO);
        return ImmutableMap.of("id", (Object) prize.getId());
    }

    @RequestMapping(value = "/{applicationId}/prizes/{prizeId}", method = RequestMethod.PUT)
    public void updatePrize(@PathVariable Integer applicationId, @PathVariable Integer prizeId, @Valid @RequestBody ApplicationPrizeDTO prizeDTO) {
        applicationSectionService.updatePrize(applicationId, prizeId, prizeDTO);
    }

    @RequestMapping(value = "/{applicationId}/prizes/{prizeId}", method = RequestMethod.DELETE)
    public void deletePrize(@PathVariable Integer applicationId, @PathVariable Integer prizeId) {
        applicationSectionService.deletePrize(applicationId, prizeId);
    }

    @RequestMapping(value = "/{applicationId}/referees", method = RequestMethod.POST)
    public Map<String, Object> createReferee(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationRefereeDTO refereeDTO) {
        ApplicationReferee referee = applicationSectionService.updateReferee(applicationId, null, refereeDTO);
        return ImmutableMap.of("id", (Object) referee.getId());
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.PUT)
    public void deleteReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId, @Valid @RequestBody ApplicationRefereeDTO refereeDTO) {
        applicationSectionService.updateReferee(applicationId, refereeId, refereeDTO);
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.DELETE)
    public void updateReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId) {
        applicationSectionService.deleteReferee(applicationId, refereeId);
    }

    @RequestMapping(value = "/{applicationId}/document", method = RequestMethod.PUT)
    public void saveDocument(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationDocumentDTO documentDTO) {
        applicationSectionService.updateDocument(applicationId, documentDTO);
    }

    @RequestMapping(value = "/{applicationId}/additionalInformation", method = RequestMethod.PUT)
    public void saveAdditionalInformation(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationAdditionalInformationDTO additionalInformationDTO) {
        applicationSectionService.updateAdditionalInformation(applicationId, additionalInformationDTO);
    }

}
