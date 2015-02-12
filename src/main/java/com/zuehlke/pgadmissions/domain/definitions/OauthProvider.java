package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.collect.Maps;

import java.util.Map;

public enum OauthProvider {

    FACEBOOK("facebook"),
    LINKEDIN("linkedin"),
    GOOGLE("google"),
    TWITTER("twitter");

    private static Map<String, OauthProvider> byNameMap = Maps.newHashMap();
    static {
        for (OauthProvider provider : values()) {
            byNameMap.put(provider.getName(), provider);
        }
    }
    private String name;

    OauthProvider(String name) {
        this.name = name;
    }

    public static OauthProvider getByName(String name) {
        return byNameMap.get(name);
    }

    public String getName() {
        return name;
    }
}
