package com.zuehlke.pgadmissions.services;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UclIrisProfileService {

    private Logger log = LoggerFactory.getLogger(UclIrisProfileService.class);
    
    private static final String IRIS_PROFILE_URL = "http://iris.ucl.ac.uk/iris/browse/profile?upi=";
    
    public UclIrisProfileService() {
    }
    
    public String geProfile(final String upi) {
        try {
            Document doc = downloadIrisProfile(upi);
            Elements displayNameDiv = doc.select("div.displayName");
            if (!displayNameDiv.isEmpty() && displayNameDiv.size() == 1) {
                return displayNameDiv.text();                
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
        }
        return StringUtils.EMPTY;
    }
    
    public boolean profileExists(final String upi) {
        return StringUtils.isNotEmpty(geProfile(upi));
    }
    
    Document downloadIrisProfile(final String upi) throws IOException {
        return Jsoup.connect(IRIS_PROFILE_URL + StringUtils.upperCase(upi)).get();
    }
}
