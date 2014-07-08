package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.rest.domain.UserRepresentation;
import com.zuehlke.pgadmissions.rest.dto.RegistrationDetails;
import com.zuehlke.pgadmissions.rest.validation.InvalidRequestException;
import com.zuehlke.pgadmissions.rest.validation.validator.RegistrationDetailsValidator;
import com.zuehlke.pgadmissions.security.TokenUtils;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.WebApplicationException;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserResource {

    @Resource(name = "pgAdmissionUserDetailsService")
    private UserDetailsService userService;

    @Autowired
    @Named("authenticationManager")
    private AuthenticationManager authManager;

    @Autowired
    private RegistrationDetailsValidator registrationDetailsValidator;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    /**
     * Retrieves the currently logged in user.
     *
     * @return A transfer containing the username and the roles.
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public UserRepresentation getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof String && ((String) principal).equals("anonymousUser")) {
            throw new WebApplicationException(401);
        }
        User user = (User) principal;

        return dozerBeanMapper.map(user, UserRepresentation.class);
    }

    /**
     * Authenticates a user and creates an authentication token.
     *
     * @param username The name of the user.
     * @param password The password of the user.
     * @return A transfer containing the authentication token.
     */
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST, produces = "application/json")
    public Map<String, String> authenticate(@RequestParam(required = false, value = "username") String username,
                                            @RequestParam(required = false, value = "password") String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = this.authManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload user as password of authentication principal will be null after authorisation and password is needed for token generation
        UserDetails userDetails = this.userService.loadUserByUsername(username);

        return ImmutableMap.of("token", TokenUtils.createToken(userDetails));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String submitRegistration(@RequestBody RegistrationDetails registrationDetails, BindingResult result, Model model, HttpServletRequest request) {
        registrationDetailsValidator.validate(registrationDetails, result);

        if (result.hasErrors()) {
            throw new InvalidRequestException("Invalid registration details", result);
        }

        User user = registrationService.submitRegistration(registrationDetails);
        return "OK";
    }

}
