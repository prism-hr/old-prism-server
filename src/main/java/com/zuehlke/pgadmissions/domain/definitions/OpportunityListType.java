package com.zuehlke.pgadmissions.domain.definitions;

import java.util.Arrays;

public enum OpportunityListType {
    
    OPPORTUNITIESBYFEEDID,
    OPPORTUNITIESBYUSERUPI,
    OPPORTUNITIESBYUSERUSERNAME,
    RECOMMENDEDOPPORTUNTIIESBYAPPLICANTID,
    CURRENTOPPORTUNITYBYADVERTID,
    CURRENTOPPORTUNITYBYAPPLICATIONFORMID;
    
    public static Boolean isCurrentOpportunityListType(OpportunityListType feedKey) {
        return Arrays.asList(CURRENTOPPORTUNITYBYADVERTID, CURRENTOPPORTUNITYBYAPPLICATIONFORMID).contains(feedKey);
    }
    
    public static Boolean isSingletonOpportunityListType(OpportunityListType feedKey) {
        return Arrays.asList(CURRENTOPPORTUNITYBYADVERTID).contains(feedKey);
    }
    
    public static Boolean neverHasSelectedAdvertListType(OpportunityListType feedKey) {
        return Arrays.asList(RECOMMENDEDOPPORTUNTIIESBYAPPLICANTID).contains(feedKey);
    }
    
    public static Boolean shouldBeRandomisedForDisplay(OpportunityListType feedKey) {
        return feedKey == null || Arrays.asList(OPPORTUNITIESBYFEEDID, OPPORTUNITIESBYUSERUPI, OPPORTUNITIESBYUSERUSERNAME).contains(feedKey);
    }
    
}
