package com.zuehlke.pgadmissions.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.user.UserFeedback;
import com.zuehlke.pgadmissions.rest.dto.user.UserFeedbackDTO;
import com.zuehlke.pgadmissions.rest.representation.user.UserFeedbackRepresentation;
import com.zuehlke.pgadmissions.services.UserFeedbackService;
import com.zuehlke.pgadmissions.services.integration.IntegrationUserService;

@RestController
@RequestMapping("/api/feedback")
public class UserFeedbackController {

    @Inject
    private IntegrationUserService integrationUserService;

    @Inject
    private UserFeedbackService userFeedbackService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.POST)
    public void createFeedback(@RequestBody @Valid UserFeedbackDTO userFeedbackDTO) {
        userFeedbackService.createFeedback(userFeedbackDTO);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(method = RequestMethod.GET)
    public List<UserFeedbackRepresentation> getUserFeedback(@RequestParam("ratingThreshold") Integer ratingThreshold,
            @RequestParam("lastSequenceIdentifier") String lastSequenceIdentifier) {
        List<UserFeedback> userFeedbacks = userFeedbackService.getUserFeedback(ratingThreshold, lastSequenceIdentifier);
        List<UserFeedbackRepresentation> representations = Lists.newArrayListWithCapacity(userFeedbacks.size());
        for (UserFeedback userFeedback : userFeedbacks) {
            representations.add(integrationUserService.getUserFeedbackRepresentation(userFeedback));
        }
        return representations;
    }

}
