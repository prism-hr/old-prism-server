package com.zuehlke.pgadmissions.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;

import com.google.common.base.Charsets;
import com.zuehlke.pgadmissions.services.UserService;

public class AuthenticationTokenProcessingFilter extends GenericFilterBean {

    @Inject
    private AuthenticationTokenHelper authenticationTokenHelper;

    @Inject
    private UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = this.getAsHttpRequest(request);
        HttpServletResponse httpResponse = this.getAsHttpResponse(response);

        String authToken = this.extractAuthTokenFromRequest(httpRequest);
        Integer userId = authenticationTokenHelper.getIdFromToken(authToken);

        if (userId != null) {

            UserDetails userDetails = this.userService.getById(userId);
            TokenValidityStatus tokenValidityStatus = authenticationTokenHelper.validateToken(authToken, userDetails);
            if (tokenValidityStatus.isValid()) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null,
                        Collections.<GrantedAuthority> emptyList());
                authentication.setDetails(userDetails);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                if (tokenValidityStatus.getRenewedToken() != null) {
                    httpResponse.setHeader("x-auth-token-renew", tokenValidityStatus.getRenewedToken());
                }
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

    private HttpServletResponse getAsHttpResponse(ServletResponse response) {
        if (!(response instanceof HttpServletResponse)) {
            throw new RuntimeException("Expecting an HTTP response");
        }

        return (HttpServletResponse) response;
    }

    private String extractAuthTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if ((cookies == null) || (cookies.length == 0)) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("authToken".equals(cookie.getName())) {
                try {
                    return URLDecoder.decode(cookie.getValue(), Charsets.UTF_8.name()).replace("\"", "");
                } catch (UnsupportedEncodingException e) {
                    throw new Error(e);
                }
            }
        }
        return null;
    }
}
