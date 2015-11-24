package uk.co.alumeni.prism.rest.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
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

import com.google.common.collect.ImmutableMap;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.dto.auth.OauthLoginDTO;
import uk.co.alumeni.prism.rest.dto.auth.OauthUserDefinition;
import uk.co.alumeni.prism.rest.dto.auth.UsernamePasswordLoginDTO;
import uk.co.alumeni.prism.rest.dto.user.UserRegistrationDTO;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentation;
import uk.co.alumeni.prism.security.AuthenticationTokenHelper;
import uk.co.alumeni.prism.services.UserAccountService;

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

    private Map<String, Object> generateTokenOrSuggestedDetails(User user, HttpServletRequest request, HttpServletResponse response) {
        if (user == null) {
            OauthUserDefinition userDefinition = (OauthUserDefinition) request.getSession().getAttribute(UserAccountService.OAUTH_USER_TO_CONFIRM);
            UserRepresentation suggestedDetails = new UserRepresentation().withFirstName(userDefinition.getFirstName())
                    .withLastName(userDefinition.getLastName()).withEmail(userDefinition.getEmail());
            response.setStatus(HttpStatus.I_AM_A_TEAPOT.value());
            return ImmutableMap.of("suggestedUserDetails", (Object) suggestedDetails);
        }

        if (user.getUserAccount() == null || !user.getUserAccount().getEnabled()) {
            throw new AccessDeniedException("Account not activated. Check your email for the activation message.");
        }
        return ImmutableMap.of("token", (Object) authenticationTokenHelper.createToken(user));

    }

}
