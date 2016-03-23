package uk.co.alumeni.prism.rest.controller;

import static java.util.Collections.emptyList;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.mapping.MessageMapper;
import uk.co.alumeni.prism.mapping.ProfileMapper;
import uk.co.alumeni.prism.rest.dto.MessageDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileListFilterDTO;
import uk.co.alumeni.prism.rest.representation.ProfileRepresentationCandidate;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadParticipantsRepresentationPotential;
import uk.co.alumeni.prism.rest.representation.message.MessageThreadRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileListRowRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileRepresentationSummary;
import uk.co.alumeni.prism.services.MessageService;
import uk.co.alumeni.prism.services.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/candidates")
public class ProfileController {

    @Inject
    private MessageMapper messageMapper;

    @Inject
    private MessageService messageService;

    @Inject
    private ProfileMapper profileMapper;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private UserService userService;

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

    @RequestMapping(value = "{userId}/threads", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public List<MessageThreadRepresentation> getMessageThreads(@PathVariable Integer userId, @RequestParam(required = false) String q) {
        User user = userService.getById(userId);
        if (user != null) {
            return messageMapper.getMessageThreadRepresentations(user.getUserAccount(), q);
        }
        return emptyList();
    }

    @RequestMapping(value = "{userId}/threads/{id}/participants", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public MessageThreadParticipantsRepresentationPotential getMessageThreadParticipants(@PathVariable Integer userId) {
        User user = userService.getById(userId);
        if (user != null) {
            return messageMapper.getMessageThreadParticipantsRepresentation(user.getUserAccount());
        }
        return null;
    }

    @RequestMapping(value = "{userId}/threads", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void createMessageThread(@PathVariable Integer userId, @Valid @RequestBody MessageDTO messageDTO) {
        User user = userService.getById(userId);
        if (user != null) {
            messageService.createMessageThread(user.getUserAccount(), messageDTO);
        }
    }

    @RequestMapping(value = "{resourceId}/threads/{threadId}/messages", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void createMessage(@PathVariable Integer userId, @PathVariable Integer threadId, @Valid @RequestBody MessageDTO messageDTO) {
        User user = userService.getById(userId);
        if (user != null) {
            messageService.createMessage(user.getUserAccount(), threadId, messageDTO);
        }
    }

    @RequestMapping(value = "{resourceId}/threads/{threadId}/view", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void viewMessageThread(@RequestBody Map<String, Integer> body) {
        messageService.viewMessageThread(body.get("latestUnreadMessageId"));
    }

}
