package com.zuehlke.pgadmissions.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.services.UserService;

public class AuthenticationTokenProcessor extends GenericFilterBean {

    private final UserService userService;
    
    public AuthenticationTokenProcessor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = getAsHttpRequest(request);

        String authToken = extractAuthTokenFromRequest(httpRequest);
        String userName = AuthenticationTokenUtils.getUserNameFromToken(authToken);

        if (userName != null) {

            UserDetails userDetails = userService.loadUserByUsername(userName);
            ((User)userDetails).toString();

            if (AuthenticationTokenUtils.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session has expired");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private HttpServletRequest getAsHttpRequest(ServletRequest request) {
        if (!(request instanceof HttpServletRequest)) {
            throw new RuntimeException("Expecting an HTTP request");
        }
        return (HttpServletRequest) request;
    }

    private String extractAuthTokenFromRequest(HttpServletRequest httpRequest) {
        String authToken = httpRequest.getHeader("X-Auth-Token");
        if (authToken == null) {
            authToken = httpRequest.getParameter("token");
        }
        return authToken;
    }
}
