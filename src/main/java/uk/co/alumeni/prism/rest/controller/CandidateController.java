package uk.co.alumeni.prism.rest.controller;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.mapping.ProfileMapper;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.dto.profile.ProfileListFilterDTO;
import uk.co.alumeni.prism.rest.representation.CandidateRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileListRowRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileRepresentationSummary;
import uk.co.alumeni.prism.rest.representation.user.UserProfileRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.services.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    public CandidateRepresentation getUserProfile(@PathVariable Integer userId) {
        User currentUser = userService.getCurrentUser();
        User user = userService.getById(userId);
        // TODO implement security check
        UserRepresentationSimple userRepresentation = userMapper.getUserRepresentationSimple(user, currentUser);
        UserProfileRepresentation profileRepresentation = userMapper.getUserProfileRepresentation(user);
        CandidateRepresentation candidate = new CandidateRepresentation().withUser(userRepresentation).withProfile(profileRepresentation);
        return candidate;
    }
}
