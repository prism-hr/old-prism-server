package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.rest.representation.UserExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.validation.InvalidRequestException;
import com.zuehlke.pgadmissions.rest.validation.validator.RegistrationDetailsValidator;
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
    private RegistrationDetailsValidator registrationDetailsValidator;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private Mapper dozerBeanMapper;
    
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public UserExtendedRepresentation getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof String && ((String) principal).equals("anonymousUser")) {
            throw new WebApplicationException(401);
        }
        User user = (User) principal;
        return dozerBeanMapper.map(user, UserExtendedRepresentation.class);
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST, produces = "application/json")
    public Map<String, String> authenticate(@RequestParam(required = false, value = "username") String username,
            @RequestParam(required = false, value = "password") String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        return ImmutableMap.of("token", AuthenticationTokenUtils.createToken(userDetails));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String submitRegistration(@RequestBody UserRegistrationDTO registrationDetails, BindingResult result, Model model, HttpServletRequest request)
            throws WorkflowEngineException {
        registrationDetailsValidator.validate(registrationDetails, result);

        if (result.hasErrors()) {
            throw new InvalidRequestException("Invalid registration details", result);
        }

        registrationService.submitRegistration(registrationDetails);
        return "OK";
    }

    @RequestMapping(value="/suggestion", method = RequestMethod.GET, params = "firstName")
    public List<UserRepresentation> getSimilarUsers(@RequestParam String searchTerm) {
        return userService.getSimilarUsers(searchTerm);
    }

}
