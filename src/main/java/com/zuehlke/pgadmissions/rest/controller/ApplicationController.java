package com.zuehlke.pgadmissions.rest.controller;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.Valid;

import org.joda.time.LocalDate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.mapping.ApplicationMapper;
import com.zuehlke.pgadmissions.mapping.ProfileMapper;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationProgramDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileAdditionalInformationDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileDocumentDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileEmploymentPositionDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfilePersonalDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileRefereeDTO;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileEmploymentPositionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileQualificationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileRefereeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationStartDateRepresentation;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ProfileService;

@RestController
@RequestMapping(value = {"api/applications"})
@PreAuthorize("isAuthenticated()")
public class ApplicationController {

    @Inject
    private ApplicationMapper applicationMapper;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ProfileService profileService;

    @Inject
    private ProfileMapper profileMapper;

    @RequestMapping(value = "/{applicationId}/startDate", method = RequestMethod.GET)
    public ApplicationStartDateRepresentation getStartDateRepresentation() {
        return applicationMapper.getApplicationStartDateRepresentation(new LocalDate());
    }

    @RequestMapping(value = "/{applicationId}/programDetail", method = RequestMethod.PUT)
    public void saveProgramDetail(@PathVariable Integer applicationId, @Valid @RequestBody ApplicationProgramDetailDTO programDetailDTO) {
        applicationService.updateProgramDetail(applicationId, programDetailDTO);
    }

    @RequestMapping(value = "/{applicationId}/personalDetail", method = RequestMethod.PUT)
    public void savePersonalDetail(@PathVariable Integer applicationId, @Valid @RequestBody ProfilePersonalDetailDTO personalDetailDTO) {
        profileService.updatePersonalDetailApplication(applicationId, personalDetailDTO);
    }

    @RequestMapping(value = "/{applicationId}/address", method = RequestMethod.PUT)
    public void saveAddress(@PathVariable Integer applicationId, @Valid @RequestBody ProfileAddressDTO addressDTO) {
        profileService.updateAddressApplication(applicationId, addressDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications", method = RequestMethod.GET)
    public List<ProfileQualificationRepresentation> getQualifications(@PathVariable Integer applicationId) {
        Application application = applicationService.getById(applicationId);
        return profileMapper.getQualificationRepresentations(application.getQualifications());
    }

    @RequestMapping(value = "/{applicationId}/qualifications", method = RequestMethod.POST)
    public Map<String, Object> createQualification(@PathVariable Integer applicationId, @Valid @RequestBody ProfileQualificationDTO qualificationDTO) {
        ApplicationQualification qualification = profileService.updateQualificationApplication(applicationId, null, qualificationDTO);
        return ImmutableMap.of("id", (Object) qualification.getId());
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.PUT)
    public void updateQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId,
                                    @Valid @RequestBody ProfileQualificationDTO qualificationDTO) {
        profileService.updateQualificationApplication(applicationId, qualificationId, qualificationDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.DELETE)
    public void deleteQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId) {
        profileService.deleteQualificationApplication(applicationId, qualificationId);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions", method = RequestMethod.GET)
    public List<ProfileEmploymentPositionRepresentation> getEmploymentPositions(@PathVariable Integer applicationId) {
        Application application = applicationService.getById(applicationId);
        return profileMapper.getEmploymentPositionRepresentations(application.getEmploymentPositions());
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions", method = RequestMethod.POST)
    public Map<String, Object> createEmploymentPosition(
            @PathVariable Integer applicationId, @Valid @RequestBody ProfileEmploymentPositionDTO employmentPositionDTO) {
        ApplicationEmploymentPosition employmentPosition = profileService.updateEmploymentPositionApplication(applicationId, null, employmentPositionDTO);
        return ImmutableMap.of("id", (Object) employmentPosition.getId());
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.PUT)
    public void updateEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId,
                                         @Valid @RequestBody ProfileEmploymentPositionDTO employmentPositionDTO) {
        profileService.updateEmploymentPositionApplication(applicationId, employmentPositionId, employmentPositionDTO);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.DELETE)
    public void deleteEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId) {
        profileService.deleteEmploymentPositionApplication(applicationId, employmentPositionId);
    }

    @RequestMapping(value = "/{applicationId}/referees", method = RequestMethod.GET)
    public List<ProfileRefereeRepresentation> getReferees(@PathVariable Integer applicationId) {
        Application application = applicationService.getById(applicationId);
        return profileMapper.getRefereeRepresentations(application.getReferees());
    }

    @RequestMapping(value = "/{applicationId}/referees", method = RequestMethod.POST)
    public Map<String, Object> createReferee(@PathVariable Integer applicationId, @Valid @RequestBody ProfileRefereeDTO refereeDTO) {
        ApplicationReferee referee = profileService.updateRefereeApplication(applicationId, null, refereeDTO);
        return ImmutableMap.of("id", (Object) referee.getId());
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.PUT)
    public void deleteReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId, @Valid @RequestBody ProfileRefereeDTO refereeDTO) {
        profileService.updateRefereeApplication(applicationId, refereeId, refereeDTO);
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.DELETE)
    public void updateReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId) {
        profileService.deleteRefereeApplication(applicationId, refereeId);
    }

    @RequestMapping(value = "/{applicationId}/document", method = RequestMethod.PUT)
    public void saveDocument(@PathVariable Integer applicationId, @Valid @RequestBody ProfileDocumentDTO documentDTO) {
        profileService.updateDocumentApplication(applicationId, documentDTO);
    }

    @RequestMapping(value = "/{applicationId}/additionalInformation", method = RequestMethod.PUT)
    public void saveAdditionalInformation(@PathVariable Integer applicationId, @Valid @RequestBody ProfileAdditionalInformationDTO additionalInformationDTO) {
        profileService.updateAdditionalInformationApplication(applicationId, additionalInformationDTO);
    }

}
