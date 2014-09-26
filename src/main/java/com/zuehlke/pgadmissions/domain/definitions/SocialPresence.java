package com.zuehlke.pgadmissions.domain.definitions;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.User;

public enum SocialPresence {

    LINKEDIN_COMPANY("014399221313847880480:klv9napshqc&q", 10, new ArrayList<String>(0), new Class[] { Institution.class }), //
    LINKEDIN_PERSON("014399221313847880480:zaaucdh1nbs", 1, new ArrayList<String>(0), new Class[] { User.class }), //
    TWITTER("014399221313847880480:_wynbi1gc5o", 10, Lists.newArrayList("/status", "/media"), new Class[] { Institution.class, User.class }), //
    FACEBOOK("014399221313847880480:wzhqxjux4r", 10, Lists.newArrayList("/media", "/search", "/events", "/posts"), new Class[] { Institution.class });

    private String searchEngine;

    private int resultsToConsider;

    private List<String> exclusions;

    private Class<?>[] subscriptions;

    private SocialPresence(String searchEngine, Integer resultsToConsider, List<String> exclusions, Class<?>[] subscriptions) {
        this.searchEngine = searchEngine;
        this.resultsToConsider = resultsToConsider;
        this.exclusions = exclusions;
        this.subscriptions = subscriptions;
    }

    public final String getSearchEngine() {
        return searchEngine;
    }

    public final int getResultsToConsider() {
        return resultsToConsider;
    }

    public final List<String> getExclusions() {
        return exclusions;
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
    
    public static boolean doExclude(SocialPresence presence, String uri) {
        for (String exclusion : presence.getExclusions()) {
            if (uri.contains(exclusion)) {
                return true;
            }
        }
        return false;
    }

}
