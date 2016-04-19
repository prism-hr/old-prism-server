package uk.co.alumeni.prism.domain.definitions;

import java.util.Map;

import com.google.common.collect.Maps;

public enum PrismOauthProvider {

    FACEBOOK("facebook", false), //
    LINKEDIN("linkedin", true), //
    GOOGLE("google", false), //
    TWITTER("twitter", false);

    private static Map<String, PrismOauthProvider> byNameMap = Maps.newHashMap();

    static {
        for (PrismOauthProvider provider : values()) {
            byNameMap.put(provider.getName(), provider);
        }

    }

    private String name;

    private boolean publishAssociation;

    private PrismOauthProvider(String name, boolean publishAssociation) {
        this.name = name;
        this.publishAssociation = publishAssociation;
    }

    public String getName() {
        return name;
    }

    public final boolean isPublishAssociation() {
        return publishAssociation;
    }

    public static PrismOauthProvider getByName(String name) {
        return byNameMap.get(name);
    }

}
