package com.zuehlke.pgadmissions.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.mappers.UserMapper;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserCorrectionDTO;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationUnverified;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping("api/messages")
@PreAuthorize("isAuthenticated()")
public class MessagesController {

    @Inject
    private UserMapper userMapper;
    
    @Inject
    private UserService userService;

    @RequestMapping(value = "bouncedUsers", method = RequestMethod.GET)
    public List<UserRepresentationUnverified> getBouncedOrUnverifiedUsers(UserListFilterDTO filterDTO) throws Exception {
        return userMapper.getUserUnverifiedRepresentations(filterDTO);
    }

    @RequestMapping(value = "bouncedUsers/{userId}", method = RequestMethod.PUT)
    public void correctBouncedOrUnverifiedUser(@PathVariable Integer userId, @Valid @RequestBody UserCorrectionDTO userCorrectionDTO) throws Exception {
        userService.correctBouncedOrUnverifiedUser(userId, userCorrectionDTO);
    }

}
