package com.zuehlke.pgadmissions.rest.controller;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.definitions.PrismOauthProvider;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccountExternal;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthAssociationType;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthLoginDTO;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthUserDefinition;
import com.zuehlke.pgadmissions.rest.dto.auth.UsernamePasswordLoginDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserRegistrationDTO;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentation;
import com.zuehlke.pgadmissions.security.AuthenticationTokenHelper;
import com.zuehlke.pgadmissions.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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
import java.util.Collections;
import java.util.Map;

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
    private AuthenticationService authenticationService;

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
    @RequestMapping(value = "/oauth/{provider}", method = RequestMethod.POST)
    public Map<String, Object> oauthLogin(@PathVariable String provider, @Valid @RequestBody OauthLoginDTO oauthLoginDTO, HttpServletRequest request,
            HttpServletResponse response) {
        PrismOauthProvider oauthProvider = PrismOauthProvider.getByName(provider);
        User user = authenticationService.getOrCreateUserAccountExternal(oauthProvider, oauthLoginDTO, request.getSession());
        return generateTokenOrSuggestedDetails(user, request, response);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/oauth/twitter", method = RequestMethod.GET)
    public Map<String, Object> oauthRequestToken(@RequestParam(required = false) OauthAssociationType associationType,
            @RequestParam(value = "activationCode", required = false) String activationCode,
            @RequestParam(value = "oauth_token", required = false) String oAuthToken,
            @RequestParam(value = "oauth_verifier", required = false) String oAuthVerifier, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (oAuthToken == null) {
            String authorizeUrl = authenticationService.requestToken(request.getSession(), PrismOauthProvider.TWITTER);
            response.sendRedirect(authorizeUrl);
            return null;
        } else {
            OauthLoginDTO oauthLoginDTO = new OauthLoginDTO();
            oauthLoginDTO.setAssociationType(associationType);
            oauthLoginDTO.setOauthToken(oAuthToken);
            oauthLoginDTO.setOauthVerifier(oAuthVerifier);
            oauthLoginDTO.setActivationCode(activationCode);

            User user = authenticationService.getOrCreateUserAccountExternal(PrismOauthProvider.TWITTER, oauthLoginDTO, request.getSession());
            return generateTokenOrSuggestedDetails(user, request, response);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/oauth/{provider}", method = RequestMethod.DELETE)
    public Map<String, String> unlinkExternalAccount(@PathVariable String provider) {
        PrismOauthProvider oauthProvider = PrismOauthProvider.getByName(provider);
        UserAccountExternal newPrimaryExternalAccount = authenticationService.unlinkExternalAccount(oauthProvider);
        return Collections.singletonMap("primaryExternalAccount", newPrimaryExternalAccount != null ? newPrimaryExternalAccount.getAccountType().getName()
                : null);
    }

    private Map<String, Object> generateTokenOrSuggestedDetails(User user, HttpServletRequest request, HttpServletResponse response) {
        if (user == null) {
            OauthUserDefinition userDefinition = (OauthUserDefinition) request.getSession().getAttribute(AuthenticationService.OAUTH_USER_TO_CONFIRM);
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
