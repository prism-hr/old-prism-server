package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.validation.Valid;
import javax.ws.rs.WebApplicationException;

import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.rest.representation.UserExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.UserRegistrationValidator;
import com.zuehlke.pgadmissions.security.AuthenticationTokenUtils;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RegistrationService;
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
    private RegistrationService registrationService;
    
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
    public String submitRegistration(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO)
            throws WorkflowEngineException {
        registrationService.submitRegistration(userRegistrationDTO);
        return "OK";
    }

    @RequestMapping(value = "/activate", method = RequestMethod.PUT)
    public Map<String, String> activateAccount(@RequestParam String activationCode){
        User user = userService.getUserByActivationCode(activationCode);
        if(user == null){
            throw new ResourceNotFoundException();
        }
        boolean activated = userService.activateUser(user.getId());
//        if(activated){
            return ImmutableMap.of("status", "ACTIVATED", "user", user.getEmail());
//        }
//        return ImmutableMap.of("status", "ALREADY_ACTIVATED");
    }

    @RequestMapping(value="/suggestion", method = RequestMethod.GET, params = "searchTerm")
    public List<UserRepresentation> getSimilarUsers(@RequestParam String searchTerm) {
        return userService.getSimilarUsers(searchTerm);
    }

    @InitBinder(value = "userRegistrationDTO")
    public void configureCommentBinding(WebDataBinder binder) {
        binder.setValidator(userRegistrationValidator);
    }

}
