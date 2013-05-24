package com.zuehlke.pgadmissions.services;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@Service
public class UclIrisProfileService {

    private static final String IRIS_PROFILE_URL = "http://iris.ucl.ac.uk/iris/browse/profile?upi=";
    
    public String geProfile(final String upi) {
        try {
            WebClient client = new WebClient();
            client.setJavaScriptEnabled(false);
            HtmlPage page = (HtmlPage) client.getPage(IRIS_PROFILE_URL + StringUtils.upperCase(upi));
            HtmlElement displayNameDiv = (HtmlElement) page.getFirstByXPath("//*[contains(@class, 'displayName')]");
            if (displayNameDiv != null) {
                return displayNameDiv.asText();
            }
        } catch (Exception e) {
            // do nothing
        }
        return StringUtils.EMPTY;
    }
    
    public boolean profileExists(final String upi) {
        return StringUtils.isNotEmpty(geProfile(upi));
    }
}
