package com.zuehlke.pgadmissions.domain.enums;

import java.util.Arrays;

public enum OpportunityListType {
    
    OPPORTUNITIESBYFEEDID,
    OPPORTUNITIESBYUSERUPI,
    OPPORTUNITIESBYUSERUSERNAME,
    RECOMMENDEDOPPORTUNTIIES,
    CURRENTOPPORTUNITYBYADVERTID,
    CURRENTOPPORTUNITYBYPROGRAMCODE;
    
    public static Boolean isCurrentOpportunityListType(OpportunityListType feedKey) {
        return Arrays.asList(CURRENTOPPORTUNITYBYADVERTID, CURRENTOPPORTUNITYBYPROGRAMCODE).contains(feedKey);
    }
    
}
