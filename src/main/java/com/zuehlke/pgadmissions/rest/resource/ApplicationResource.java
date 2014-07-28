package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.rest.dto.application.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.UserService;

import java.util.Map;

@RestController
@RequestMapping(value = {"api/applications"})
public class ApplicationResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{applicationId}/programDetails", method = RequestMethod.PUT)
    public void saveProgramDetails(@PathVariable Integer applicationId, @RequestBody ApplicationProgramDetailsDTO programDetailsDTO) {
        applicationService.saveProgramDetails(applicationId, programDetailsDTO);
    }

    @RequestMapping(value = "/{applicationId}/personalDetails", method = RequestMethod.PUT)
    public void savePersonalDetails(@PathVariable Integer applicationId, @RequestBody ApplicationPersonalDetailsDTO personalDetailsDTO) {
        applicationService.savePersonalDetails(applicationId, personalDetailsDTO);
    }

    @RequestMapping(value = "/{applicationId}/address", method = RequestMethod.PUT)
    public void saveAddress(@PathVariable Integer applicationId, @RequestBody ApplicationAddressDTO addressDTO) {
        applicationService.saveAddress(applicationId, addressDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications", method = RequestMethod.POST)
    public Map<String, Object> createQualification(@PathVariable Integer applicationId, @RequestBody ApplicationQualificationDTO qualificationDTO) {
        ApplicationQualification qualification = applicationService.saveQualification(applicationId, null, qualificationDTO);
        return ImmutableMap.of("id", (Object)qualification.getId());
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.PUT)
    public void updateQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId, @RequestBody ApplicationQualificationDTO qualificationDTO) {
        applicationService.saveQualification(applicationId, qualificationId, qualificationDTO);
    }

    @RequestMapping(value = "/{applicationId}/qualifications/{qualificationId}", method = RequestMethod.DELETE)
    public void updateQualification(@PathVariable Integer applicationId, @PathVariable Integer qualificationId) {
        applicationService.deleteQualification(applicationId, qualificationId);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions", method = RequestMethod.POST)
    public Map<String, Object> createEmploymentPosition(@PathVariable Integer applicationId, @RequestBody ApplicationEmploymentPositionDTO employmentPositionDTO) {
        ApplicationEmploymentPosition employmentPosition = applicationService.saveEmploymentPosition(applicationId, null, employmentPositionDTO);
        return ImmutableMap.of("id", (Object)employmentPosition.getId());
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.PUT)
    public void updateEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId, @RequestBody ApplicationEmploymentPositionDTO employmentPositionDTO) {
        applicationService.saveEmploymentPosition(applicationId, employmentPositionId, employmentPositionDTO);
    }

    @RequestMapping(value = "/{applicationId}/employmentPositions/{employmentPositionId}", method = RequestMethod.DELETE)
    public void updateEmploymentPosition(@PathVariable Integer applicationId, @PathVariable Integer employmentPositionId) {
        applicationService.deleteEmploymentPosition(applicationId, employmentPositionId);
    }

    @RequestMapping(value = "/{applicationId}/fundings", method = RequestMethod.POST)
    public Map<String, Object> createFunding(@PathVariable Integer applicationId, @RequestBody ApplicationFundingDTO fundingDTO) {
        ApplicationFunding funding = applicationService.saveFunding(applicationId, null, fundingDTO);
        return ImmutableMap.of("id", (Object)funding.getId());
    }

    @RequestMapping(value = "/{applicationId}/fundings/{fundingId}", method = RequestMethod.PUT)
    public void updateFunding(@PathVariable Integer applicationId, @PathVariable Integer fundingId, @RequestBody ApplicationFundingDTO fundingDTO) {
        applicationService.saveFunding(applicationId, fundingId, fundingDTO);
    }

    @RequestMapping(value = "/{applicationId}/fundings/{fundingId}", method = RequestMethod.DELETE)
    public void updateFunding(@PathVariable Integer applicationId, @PathVariable Integer fundingId) {
        applicationService.deleteFunding(applicationId, fundingId);
    }

    @RequestMapping(value = "/{applicationId}/referees", method = RequestMethod.POST)
    public Map<String, Object> createRreferee(@PathVariable Integer applicationId, @RequestBody ApplicationRefereeDTO refereeDTO) {
        ApplicationReferee referee = applicationService.saveReferee(applicationId, null, refereeDTO);
        return ImmutableMap.of("id", (Object)referee.getId());
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.PUT)
    public void updateReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId, @RequestBody ApplicationRefereeDTO refereeDTO) {
        applicationService.saveReferee(applicationId, refereeId, refereeDTO);
    }

    @RequestMapping(value = "/{applicationId}/referees/{refereeId}", method = RequestMethod.DELETE)
    public void updateReferee(@PathVariable Integer applicationId, @PathVariable Integer refereeId) {
        applicationService.deleteReferee(applicationId, refereeId);
    }
    
    @RequestMapping(value = "/{applicationId}/comments", method = RequestMethod.POST)
    public void performAction(@PathVariable Integer applicationId, @RequestParam PrismAction actionId, @RequestBody CommentDTO commentDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        Action action = actionService.getById(actionId);
        Comment comment = new Comment().withContent(commentDTO.getContent()).withUser(userService.getCurrentUser())
                .withAction(entityService.getById(Action.class, action)).withCreatedTimestamp(new DateTime())
                .withDeclinedResponse(commentDTO.getDeclinedResponse());
        actionService.executeUserAction(application, action, comment);
    }

}
