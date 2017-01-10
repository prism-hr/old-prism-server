package uk.co.alumeni.prism.interceptors;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EncryptionKeyInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SecretKey key = (SecretKey) request.getSession().getAttribute("key");
        KeyContextHolder.setContext(key);
        return true;
    }


}
