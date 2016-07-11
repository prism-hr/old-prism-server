package uk.co.alumeni.prism.rest.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.dto.auth.OauthLoginDTO;
import uk.co.alumeni.prism.rest.dto.auth.OauthUserDefinition;
import uk.co.alumeni.prism.rest.dto.auth.UsernamePasswordLoginDTO;
import uk.co.alumeni.prism.rest.dto.user.UserRegistrationDTO;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.security.AuthenticationTokenHelper;
import uk.co.alumeni.prism.services.UserAccountService;
import uk.co.alumeni.prism.services.UserService;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;
import static uk.co.alumeni.prism.services.UserAccountService.OAUTH_USER_TO_CONFIRM;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Resource(name = "prismUserDetailsService")
    private UserDetailsService userDetailsService;

    @Inject
    @Named("authenticationManager")
    private AuthenticationManager authenticationManager;

    @Inject
    private AuthenticationTokenHelper authenticationTokenHelper;

    @Inject
    private UserAccountService authenticationService;

    @Inject
    private UserService userService;

    @Inject
    private UserMapper userMapper;

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String, String> authenticate(@Valid @RequestBody UsernamePasswordLoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(loginDTO.getUsername());
        return ImmutableMap.of("token", authenticationTokenHelper.createToken(userDetails));
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void submitRegistration(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO, HttpServletRequest request) {
        authenticationService.registerUser(userRegistrationDTO, request.getSession());
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/oauth/linkedin", method = RequestMethod.POST)
    public Map<String, Object> oauthLogin(@Valid @RequestBody OauthLoginDTO oauthLoginDTO, HttpServletRequest request,
                                          HttpServletResponse response) {
        User user = authenticationService.getOrCreateUserAccountExternal(oauthLoginDTO, request.getSession());
        return generateTokenOrSuggestedDetails(user, request, response);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    public Map<String, Object> activateAccount(@RequestBody Map<String, String> body) {
        User user = userService.getUserByActivationCode(body.get("activationCode"));
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        String status;
        String loginProvider = null;
        if (user.getUserAccount() == null) {
            status = "NOT_REGISTERED";
        } else {
            userService.enableUser(user.getId());
            status = "ACTIVATED";
            loginProvider = user.getUserAccount().getLinkedinId() != null ? "linkedin" : null;
        }
        UserRepresentationSimple userRepresentation = userMapper.getUserRepresentationSimple(user, user);

        Map<String, Object> result = Maps.newHashMap();
        result.put("status", status);
        result.put("user", userRepresentation);
        if (loginProvider != null) {
            result.put("loginProvider", loginProvider);
        }
        return result;
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public void resetPassword(@RequestBody Map<String, String> body) {
        userService.resetPassword(body.get("email"));
    }


    private Map<String, Object> generateTokenOrSuggestedDetails(User user, HttpServletRequest request, HttpServletResponse response) {
        if (user == null) {
            OauthUserDefinition userDefinition = (OauthUserDefinition) request.getSession().getAttribute(OAUTH_USER_TO_CONFIRM);
            UserRepresentation suggestedDetails = new UserRepresentation().withFirstName(userDefinition.getFirstName())
                    .withLastName(userDefinition.getLastName()).withEmail(userDefinition.getEmail());
            response.setStatus(I_AM_A_TEAPOT.value());
            return ImmutableMap.of("suggestedUserDetails", (Object) suggestedDetails);
        }

        if (user.getUserAccount() == null || !user.getUserAccount().getEnabled()) {
            throw new AccessDeniedException("Account not activated. Check your email for the activation message.");
        }
        return ImmutableMap.of("token", (Object) authenticationTokenHelper.createToken(user));

    }

}
