package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;

import javax.validation.Valid;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserCorrectionDTO;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping("api/messages")
@PreAuthorize("isAuthenticated()")
public class MessagesController {

	@Autowired
	private UserService userService;

	@Autowired
	private Mapper beanMapper;

	@RequestMapping(value = "bouncedUsers", method = RequestMethod.GET)
	public List<UserRepresentation> getBouncedOrUnverifiedUsers(UserListFilterDTO userListFilterDTO) throws Exception {
		List<User> bouncedUsers = userService.getBouncedOrUniverifiedUsers(userListFilterDTO);
		List<UserRepresentation> userRepresentations = Lists.newArrayListWithCapacity(bouncedUsers.size());
		for (User bouncedUser : bouncedUsers) {
			userRepresentations.add(beanMapper.map(bouncedUser, UserRepresentation.class));
		}
		return userRepresentations;
	}

	@RequestMapping(value = "bouncedUsers/{userId}", method = RequestMethod.PUT)
	public void correctBouncedOrUnverifiedUser(@PathVariable Integer userId, @Valid @RequestBody UserCorrectionDTO userCorrectionDTO) throws Exception {
		userService.correctBouncedOrUniverifiedUser(userId, userCorrectionDTO);
	}

}
