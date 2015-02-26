package com.zuehlke.pgadmissions.rest.resource;

import com.zuehlke.pgadmissions.rest.dto.user.UserCorrectionDTO;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
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
    public List<UserRepresentation> getBouncedOrUnverifiedUsers() throws Exception {
//        List<User> bouncedUsers = userService.getBouncedOrUniverifiedUsers(userListFilterDTO);
//        List<UserRepresentation> userRepresentations = Lists.newArrayListWithCapacity(bouncedUsers.size());
//        for (User bouncedUser : bouncedUsers) {
//            userRepresentations.add(beanMapper.map(bouncedUser, UserRepresentation.class));
//        }
//        return userRepresentations;
        return null;
    }

    @RequestMapping(value = "bouncedUsers/{userId}", method = RequestMethod.PUT)
    public void correctBouncedOrUnverifiedUser(@PathVariable Integer userId, @Valid @RequestBody UserCorrectionDTO userCorrectionDTO) throws Exception {
        userService.correctBouncedOrUniverifiedUser(userId, userCorrectionDTO);
    }

}