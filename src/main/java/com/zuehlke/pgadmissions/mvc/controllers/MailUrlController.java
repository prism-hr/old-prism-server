package com.zuehlke.pgadmissions.mvc.controllers;

import com.zuehlke.pgadmissions.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("api/mail")
public class MailUrlController {

    @Value("${application.url}")
    private String applicationUrl;

    @RequestMapping(method = RequestMethod.GET, value = "{redirectPath:.+}")
    public void redirect(HttpServletRequest request, HttpServletResponse response, @PathVariable String redirectPath) {
        String redirect = applicationUrl + "/" + Constants.ANGULAR_HASH + "/";
        if(redirectPath != null){
            redirect += redirectPath;
            if(request.getQueryString() != null){
                redirect += "?" + request.getQueryString();
            }
        }
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", redirect);
    }

}
