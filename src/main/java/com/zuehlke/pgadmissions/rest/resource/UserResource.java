package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.validation.Valid;
import javax.ws.rs.WebApplicationException;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.rest.representation.UserExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.UserRegistrationValidator;
import com.zuehlke.pgadmissions.security.AuthenticationTokenUtils;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserResource {

    @Resource(name = "prismUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    @Named("authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRegistrationValidator userRegistrationValidator;

    @Autowired
    private ProgramService programService;

    @Autowired
    private UserService userService;

    @Autowired
    private Mapper dozerBeanMapper;

    @RequestMapping(method = RequestMethod.GET)
    public UserExtendedRepresentation getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof String && ((String) principal).equals("anonymousUser")) {
            throw new WebApplicationException(401);
        }
        User user = (User) principal;
        return dozerBeanMapper.map(user, UserExtendedRepresentation.class);
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public Map<String, String> authenticate(@RequestParam(required = false, value = "username") String username,
                                            @RequestParam(required = false, value = "password") String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        return ImmutableMap.of("token", AuthenticationTokenUtils.createToken(userDetails));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void submitRegistration(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO)
            throws WorkflowEngineException {
        userService.registerUser(userRegistrationDTO);
    }

    @RequestMapping(value = "/activate", method = RequestMethod.PUT)
    public Map<String, String> activateAccount(@RequestParam String activationCode) {
        User user = userService.getUserByActivationCode(activationCode);
        if (user == null) {
            throw new ResourceNotFoundException();
        }
        userService.activateUser(user.getId());
        return ImmutableMap.of("status", "ACTIVATED", "user", user.getEmail());
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public void resetPassword(@RequestParam String email) {
        userService.resetPassword(email);
    }

    @RequestMapping(value = "/suggestion", method = RequestMethod.GET, params = "searchTerm")
    public List<UserRepresentation> getSimilarUsers(@RequestParam String searchTerm) {
        return userService.getSimilarUsers(searchTerm);
    }

    @InitBinder(value = "userRegistrationDTO")
    public void configureCommentBinding(WebDataBinder binder) {
        binder.setValidator(userRegistrationValidator);
    }

}
