package uk.co.alumeni.prism.security;

import com.google.common.base.Charsets;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;
import uk.co.alumeni.prism.services.UserService;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;

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
            if (userDetails != null) {
                TokenValidityStatus tokenValidityStatus = authenticationTokenHelper.validateToken(authToken, userDetails);
                if (tokenValidityStatus.isValid()) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null,
                            Collections.emptyList());
                    authentication.setDetails(userDetails);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    if (tokenValidityStatus.getRenewedToken() != null) {
                        httpResponse.setHeader("x-auth-token-renew", tokenValidityStatus.getRenewedToken());
                    }
                }
            }
        }

        if (httpRequest.getHeader("x-auth-required") != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated (x-auth-required flag set)");
            return;
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
