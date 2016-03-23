package uk.co.alumeni.prism.rest.representation;

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Collection;
import java.util.Set;

import uk.co.alumeni.prism.rest.representation.message.MessageThreadParticipantRepresentationPotential;
import uk.co.alumeni.prism.rest.representation.profile.ProfileRepresentationMessage;
import uk.co.alumeni.prism.rest.representation.user.UserProfileRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class ProfileRepresentationCandidate extends ProfileRepresentationMessage {

    private UserRepresentationSimple user;

    private UserProfileRepresentation profile;

    private Set<MessageThreadParticipantRepresentationPotential> messageThreadParticipants = newLinkedHashSet();

    private Set<MessageThreadParticipantRepresentationPotential> partnerMessageThreadParticipants = newLinkedHashSet();

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public UserProfileRepresentation getProfile() {
        return profile;
    }

    public void setProfile(UserProfileRepresentation profile) {
        this.profile = profile;
    }

    public Set<MessageThreadParticipantRepresentationPotential> getMessageThreadParticipants() {
        return messageThreadParticipants;
    }

    public void setMessageThreadParticipants(Set<MessageThreadParticipantRepresentationPotential> messageThreadParticipants) {
        this.messageThreadParticipants = messageThreadParticipants;
    }

    public Set<MessageThreadParticipantRepresentationPotential> getPartnerMessageThreadParticipants() {
        return partnerMessageThreadParticipants;
    }

    public void setPartnerMessageThreadParticipants(Set<MessageThreadParticipantRepresentationPotential> partnerMessageThreadParticipants) {
        this.partnerMessageThreadParticipants = partnerMessageThreadParticipants;
    }

    public ProfileRepresentationCandidate withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public ProfileRepresentationCandidate withProfile(UserProfileRepresentation profile) {
        this.profile = profile;
        return this;
    }

    public ProfileRepresentationCandidate withReadMessageCount(Integer readMessageCount) {
        setReadMessageCount(readMessageCount);
        return this;
    }

    public ProfileRepresentationCandidate withUnreadMessageCount(Integer unreadMessageCount) {
        setUnreadMessageCount(unreadMessageCount);
        return this;
    }
    
    public ProfileRepresentationCandidate addMessageThreadParticipants(Collection<MessageThreadParticipantRepresentationPotential> messageThreadParticipants) {
        this.messageThreadParticipants.addAll(messageThreadParticipants);
        return this;
    }

    public ProfileRepresentationCandidate addPartnerMessageThreadParticipants(
            Collection<MessageThreadParticipantRepresentationPotential> partnerMessageThreadParticipants) {
        this.partnerMessageThreadParticipants.addAll(partnerMessageThreadParticipants);
        return this;
    }

}
