package com.zuehlke.pgadmissions.domain.definitions;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.user.User;

public enum SocialPresence {

    LINKEDIN_COMPANY("https://www.linkedin.com/company/", "014399221313847880480:klv9napshqc&q", new Class[] { Institution.class }), //
    TWITTER("https://twitter.com/", "014399221313847880480:_wynbi1gc5o", new Class[] { Institution.class, User.class }), //
    FACEBOOK("https://www.facebook.com/", "014399221313847880480:wzhqxjux4rk", new Class[] { Institution.class });

    private String searchEngineUri;
    
    private String searchEngineKey;

    private Class<?>[] subscriptions;

    private SocialPresence(String searchEngineUri, String searchEngineKey, Class<?>[] subscriptions) {
        this.searchEngineUri = searchEngineUri;
        this.searchEngineKey = searchEngineKey;
        this.subscriptions = subscriptions;
    }

    public final String getSearchEngineUri() {
        return searchEngineUri;
    }

    public final void setSearchEngineUri(String searchEngineUri) {
        this.searchEngineUri = searchEngineUri;
    }

    public final String getSearchEngineKey() {
        return searchEngineKey;
    }

    public final Class<?>[] getSubscriptions() {
        return subscriptions;
    }

    public static <T> List<SocialPresence> getClassSubscriptions(Class<T> subscribingClass) {
        List<SocialPresence> classSubscriptions = Lists.newArrayList();
        for (SocialPresence presence : SocialPresence.values()) {
            for (Class<?> subscriber : presence.getSubscriptions()) {
                if (subscriber.equals(subscribingClass)) {
                    classSubscriptions.add(presence);
                }
            }
        }
        return classSubscriptions;
    }

}
