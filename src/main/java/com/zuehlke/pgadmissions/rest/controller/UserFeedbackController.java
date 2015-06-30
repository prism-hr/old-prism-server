package com.zuehlke.pgadmissions.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.dozer.Mapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.user.UserFeedback;
import com.zuehlke.pgadmissions.rest.dto.user.UserFeedbackDTO;
import com.zuehlke.pgadmissions.rest.representation.UserFeedbackRepresentation;
import com.zuehlke.pgadmissions.services.UserFeedbackService;

@RestController
@RequestMapping("/api/feedback")
public class UserFeedbackController {

    @Inject
    private UserFeedbackService userFeedbackService;

    @Inject
    private Mapper dozerBeanMapper;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.POST)
    public void createFeedback(@RequestBody @Valid UserFeedbackDTO userFeedbackDTO) {
        userFeedbackService.createFeedback(userFeedbackDTO);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(method = RequestMethod.GET)
    public List<UserFeedbackRepresentation> getUserFeedback(@RequestParam("ratingThreshold") Integer ratingThreshold, @RequestParam("lastSequenceIdentifier") String lastSequenceIdentifier) {
        List<UserFeedback> feedback = userFeedbackService.getUserFeedback(ratingThreshold, lastSequenceIdentifier);
        List<UserFeedbackRepresentation> feedbackRepresentations = Lists.newArrayListWithCapacity(feedback.size());
        for (UserFeedback userFeedback : feedback) {
            feedbackRepresentations.add(dozerBeanMapper.map(userFeedback, UserFeedbackRepresentation.class));
        }
        return feedbackRepresentations;
    }

}
