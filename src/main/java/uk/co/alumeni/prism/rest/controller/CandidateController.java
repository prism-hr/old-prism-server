package uk.co.alumeni.prism.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.mapping.ProfileMapper;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.dto.profile.ProfileListFilterDTO;
import uk.co.alumeni.prism.rest.representation.profile.ProfileListRowRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileRepresentationSummary;
import uk.co.alumeni.prism.rest.representation.user.UserProfileRepresentation;
import uk.co.alumeni.prism.services.UserService;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @Inject
    private UserService userService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private ProfileMapper profileMapper;

    @Inject
    private ObjectMapper objectMapper;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET)
    public List<ProfileListRowRepresentation> getUserProfiles(@RequestParam(required = false) String filter) throws IOException {
        ProfileListFilterDTO filterDTO = filter != null ? objectMapper.readValue(filter, ProfileListFilterDTO.class) : null;
        return profileMapper.getProfileListRowRepresentations(filterDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{userId}", method = RequestMethod.GET, params = "type=summary")
    public ProfileRepresentationSummary getUserProfileSummary(@PathVariable Integer userId) {
        return profileMapper.getProfileRepresentationSummary(userId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{userId}", method = RequestMethod.GET)
    public UserProfileRepresentation getUserProfile(@PathVariable Integer userId) {
        User user = userService.getById(userId);
        // TODO implement security check
        return userMapper.getUserProfileRepresentation(user);
    }
}
