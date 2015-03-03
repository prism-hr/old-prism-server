package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserCorrectionDTO;
import com.zuehlke.pgadmissions.rest.representation.UnverifiedUserRepresentation;
import com.zuehlke.pgadmissions.services.UserService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/messages")
@PreAuthorize("isAuthenticated()")
public class MessagesController {

    @Autowired
    private UserService userService;

    @Autowired
    private Mapper beanMapper;

    @RequestMapping(value = "bouncedUsers", method = RequestMethod.GET)
    public List<UnverifiedUserRepresentation> getBouncedOrUnverifiedUsers(UserListFilterDTO userListFilterDTO) throws Exception {
        List<User> bouncedUsers = userService.getBouncedOrUnverifiedUsers(userListFilterDTO);
        List<UnverifiedUserRepresentation> userRepresentations = Lists.newArrayListWithCapacity(bouncedUsers.size());
        for (User bouncedUser : bouncedUsers) {
            UnverifiedUserRepresentation userRepresentation = beanMapper.map(bouncedUser, UnverifiedUserRepresentation.class);

            String emailBouncedMessage = bouncedUser.getEmailBouncedMessage();
            if (emailBouncedMessage != null) {
                JsonPrimitive diagnosticCodePrimitive = new JsonParser().parse(emailBouncedMessage).getAsJsonObject().getAsJsonPrimitive("diagnosticCode");
                userRepresentation.setBouncedDiagnosticCode(diagnosticCodePrimitive != null ? diagnosticCodePrimitive.getAsString() : "Diagnostic information not available");
            }

            userRepresentations.add(userRepresentation);
        }
        return userRepresentations;
    }

    @RequestMapping(value = "bouncedUsers/{userId}", method = RequestMethod.PUT)
    public void correctBouncedOrUnverifiedUser(@PathVariable Integer userId, @Valid @RequestBody UserCorrectionDTO userCorrectionDTO) throws Exception {
        userService.correctBouncedOrUnverifiedUser(userId, userCorrectionDTO);
    }

}
