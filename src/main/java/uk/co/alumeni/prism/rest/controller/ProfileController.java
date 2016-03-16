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

import uk.co.alumeni.prism.mapping.ProfileMapper;
import uk.co.alumeni.prism.rest.dto.profile.ProfileListFilterDTO;
import uk.co.alumeni.prism.rest.representation.ProfileRepresentationCandidate;
import uk.co.alumeni.prism.rest.representation.profile.ProfileListRowRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileRepresentationSummary;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/candidates")
public class ProfileController {

    @Inject
    private ProfileMapper profileMapper;

    @Inject
    private ObjectMapper objectMapper;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET)
    public List<ProfileListRowRepresentation> getUserProfiles(@RequestParam(required = false) String filter,
            @RequestParam(required = false) String lastSequenceIdentifier) throws IOException {
        ProfileListFilterDTO filterDTO = filter != null ? objectMapper.readValue(filter, ProfileListFilterDTO.class) : null;
        return profileMapper.getProfileListRowRepresentations(filterDTO, lastSequenceIdentifier);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{userId}", method = RequestMethod.GET, params = "type=summary")
    public ProfileRepresentationSummary getUserProfileSummary(@PathVariable Integer userId) {
        return profileMapper.getProfileRepresentationSummary(userId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{userId}", method = RequestMethod.GET)
    public ProfileRepresentationCandidate getUserProfile(@PathVariable Integer userId) {
        return profileMapper.getProfileRepresentationCandidate(userId);
    }

}
