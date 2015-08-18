package com.zuehlke.pgadmissions.domain.definitions;

import java.util.Map;

import com.google.common.collect.Maps;

public enum OauthProvider {

    FACEBOOK("facebook", false), //
    LINKEDIN("linkedin", true), //
    GOOGLE("google", false), //
    TWITTER("twitter", false);

    private static Map<String, OauthProvider> byNameMap = Maps.newHashMap();

    static {
        for (OauthProvider provider : values()) {
            byNameMap.put(provider.getName(), provider);
        }

    }
    
    private String name;
    
    private boolean publishAssociation;

    private OauthProvider(String name, boolean publishAssociation) {
        this.name = name;
        this.publishAssociation = publishAssociation;
    }

    public String getName() {
        return name;
    }
    
    public final boolean isPublishAssociation() {
        return publishAssociation;
    }

    public static OauthProvider getByName(String name) {
        return byNameMap.get(name);
    }
    
}
