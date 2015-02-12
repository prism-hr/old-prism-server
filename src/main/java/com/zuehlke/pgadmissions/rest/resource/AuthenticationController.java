package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.definitions.OauthProvider;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthAssociationType;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthLoginDTO;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthUserDefinition;
import com.zuehlke.pgadmissions.rest.dto.auth.UsernamePasswordLoginDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserRegistrationDTO;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.security.AuthenticationTokenHelper;
import com.zuehlke.pgadmissions.services.AuthenticationService;
import com.zuehlke.pgadmissions.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @Resource(name = "prismUserDetailsService")
    private UserDetailsService userDetailsService;

    @Inject
    @Named("authenticationManager")
    private AuthenticationManager authenticationManager;

    @Inject
    private AuthenticationTokenHelper authenticationTokenHelper;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private UserService userService;

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
    public void submitRegistration(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO,
                                   HttpServletRequest request) throws Exception {
        authenticationService.registerUser(userRegistrationDTO, request.getSession());
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/oauth/{provider}", method = RequestMethod.POST)
    public Map<String, Object> oauthLogin(@PathVariable String provider,
                                          @Valid @RequestBody OauthLoginDTO oauthLoginDTO,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        OauthProvider oauthProvider = OauthProvider.getByName(provider);

        User user = authenticationService.getOrCreateUser(oauthProvider, oauthLoginDTO, request.getSession());
        if (user == null) {
            // user not created, need to confirm details
            OauthUserDefinition userDefinition = (OauthUserDefinition) request.getSession().getAttribute(AuthenticationService.OAUTH_USER_TO_CONFIRM);
            UserRepresentation suggestedDetails = new UserRepresentation().withFirstName(userDefinition.getFirstName())
                    .withLastName(userDefinition.getLastName()).withEmail(userDefinition.getEmail());
            response.setStatus(HttpStatus.I_AM_A_TEAPOT.value());
            return ImmutableMap.of("suggestedUserDetails", (Object) suggestedDetails);

        }

        return ImmutableMap.of("token", (Object) authenticationTokenHelper.createToken(user));
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/oauth/twitter", method = RequestMethod.GET)
    public Map<String, String> oauthRequestToken(@RequestParam(required = false) OauthAssociationType associationType,
                                                 @RequestParam(value = "oauth_token", required = false) String oAuthToken,
                                                 @RequestParam(value = "oauth_verifier", required = false) String oAuthVerifier,
                                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (oAuthToken == null) { // first step
            String authorizeUrl = authenticationService.requestToken(request.getSession(), OauthProvider.TWITTER);
            response.sendRedirect(authorizeUrl);
            return null;
        } else { // second step
            OauthLoginDTO oauthLoginDTO = new OauthLoginDTO();
            oauthLoginDTO.setAssociationType(associationType);
            oauthLoginDTO.setOauthToken(oAuthToken);
            oauthLoginDTO.setOauthVerifier(oAuthVerifier);

            User user = authenticationService.getOrCreateUser(OauthProvider.TWITTER, oauthLoginDTO, request.getSession());

            return ImmutableMap.of("token", authenticationTokenHelper.createToken(user));
        }
    }

}
