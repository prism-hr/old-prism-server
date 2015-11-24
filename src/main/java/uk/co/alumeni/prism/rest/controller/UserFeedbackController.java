package uk.co.alumeni.prism.rest.controller;

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
import uk.co.alumeni.prism.domain.user.UserFeedback;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.dto.user.UserFeedbackDTO;
import uk.co.alumeni.prism.rest.representation.user.UserFeedbackRepresentation;
import uk.co.alumeni.prism.services.UserFeedbackService;

@RestController
@RequestMapping("/api/feedback")
public class UserFeedbackController {

    @Inject
    private UserMapper userMapper;

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
            representations.add(userMapper.getUserFeedbackRepresentation(userFeedback));
        }
        return representations;
    }

}
