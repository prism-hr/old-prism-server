package com.zuehlke.pgadmissions.rest.representation;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.SocialPresence;

public class SocialPresenceRepresentation {

    private final List<SocialProfile> potentialLinkedinProfiles = Lists.newLinkedList();

    private final List<SocialProfile> potentialTwitterProfiles = Lists.newLinkedList();

    private final List<SocialProfile> potentialFacebookProfiles = Lists.newLinkedList();

    public final List<SocialProfile> getPotentialLinkedinProfiles() {
        return potentialLinkedinProfiles;
    }

    public final List<SocialProfile> getPotentialTwitterProfiles() {
        return potentialTwitterProfiles;
    }

    public final List<SocialProfile> getPotentialFacebookProfiles() {
        return potentialFacebookProfiles;
    }

    public SocialPresenceRepresentation addPotentialProfile(SocialPresence presence, SocialProfile potentialProfile) {
        switch (presence) {
        case FACEBOOK:
            potentialFacebookProfiles.add(potentialProfile);
            break;
        case LINKEDIN:
            potentialLinkedinProfiles.add(potentialProfile);
            break;
        case TWITTER:
            potentialTwitterProfiles.add(potentialProfile);
            break;
        }
        return this;
    }

    public static class SocialProfile {

        private String title;

        private String uri;

        public final String getTitle() {
            return title;
        }

        public final void setTitle(String title) {
            this.title = title;
        }

        public final String getUri() {
            return uri;
        }

        public final void setUri(String uri) {
            this.uri = uri;
        }

        public SocialProfile withTitle(String title) {
            this.title = title;
            return this;
        }

        public SocialProfile withUri(String uri) {
            this.uri = uri;
            return this;
        }

    }

}
