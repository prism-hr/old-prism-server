package com.zuehlke.pgadmissions.rest.representation;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.SocialPresence;

public class SocialPresenceRepresentation {

    private final List<ExtendedSocialProfile> potentialLinkedinProfiles = Lists.newLinkedList();
    
    private final List<SocialProfile> potentialTwitterProfiles = Lists.newLinkedList();
    
    private final List<SocialProfile> potentialFacebookProfiles = Lists.newLinkedList();
    
    public final List<ExtendedSocialProfile> getPotentialLinkedinProfiles() {
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
        case LINKEDIN_COMPANY:
        case LINKEDIN_PERSON:
            potentialLinkedinProfiles.add((ExtendedSocialProfile) potentialProfile);
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
    
    public static class ExtendedSocialProfile extends SocialProfile {
        
        private String imageUri;
        
        private String summary;
        
        private String homepageUri;

        public final String getImageUri() {
            return imageUri;
        }

        public final void setImageUri(String imageUri) {
            this.imageUri = imageUri;
        }
        
        public final String getSummary() {
            return summary;
        }

        public final void setSummary(String summary) {
            this.summary = summary;
        }

        public final String getHomepageUri() {
            return homepageUri;
        }

        public final void setHomepageUri(String homepageUri) {
            this.homepageUri = homepageUri;
        }
        
        public ExtendedSocialProfile withTitle(String title) {
            setTitle(title);
            return this;
        }
        
        public ExtendedSocialProfile withUri(String uri) {
            setUri(uri);
            return this;
        }
        
    }
    
}
