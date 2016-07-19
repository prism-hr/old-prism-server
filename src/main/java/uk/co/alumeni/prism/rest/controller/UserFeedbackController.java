package uk.co.alumeni.prism.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserFeedback;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.dto.user.UserContactDTO;
import uk.co.alumeni.prism.rest.dto.user.UserFeedbackDTO;
import uk.co.alumeni.prism.rest.representation.user.UserFeedbackRepresentation;
import uk.co.alumeni.prism.services.UserFeedbackService;
import uk.co.alumeni.prism.services.UserService;

@RestController
@RequestMapping("/api")
public class UserFeedbackController {

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    @Inject
    private UserFeedbackService userFeedbackService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/feedback", method = RequestMethod.POST)
    public void createFeedback(@RequestBody @Valid UserFeedbackDTO userFeedbackDTO) {
        userFeedbackService.createFeedback(userFeedbackDTO);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/feedback", method = RequestMethod.GET)
    public List<UserFeedbackRepresentation> getUserFeedback(@RequestParam("ratingThreshold") Integer ratingThreshold,
            @RequestParam("lastSequenceIdentifier") String lastSequenceIdentifier) {
        List<UserFeedback> userFeedbacks = userFeedbackService.getUserFeedback(ratingThreshold, lastSequenceIdentifier);

        User currentUser = userService.getCurrentUser();
        return userFeedbacks.stream().map(userFeedback -> userMapper.getUserFeedbackRepresentation(userFeedback, currentUser)).collect(Collectors.toList());
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public void postMessage(@RequestBody @Valid UserContactDTO userContactDTO) {
        userFeedbackService.postContactMessage(userContactDTO);
    }

}
