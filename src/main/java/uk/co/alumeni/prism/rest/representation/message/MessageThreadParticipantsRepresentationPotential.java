package uk.co.alumeni.prism.rest.representation.message;

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Collection;
import java.util.Set;

public class MessageThreadParticipantsRepresentationPotential {

    private Set<MessageThreadParticipantRepresentationPotential> participants = newLinkedHashSet();

    private Set<MessageThreadParticipantRepresentationPotential> partnerParticipants = newLinkedHashSet();

    public Set<MessageThreadParticipantRepresentationPotential> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<MessageThreadParticipantRepresentationPotential> participants) {
        this.participants = participants;
    }

    public Set<MessageThreadParticipantRepresentationPotential> getPartnerParticipants() {
        return partnerParticipants;
    }

    public void setPartnerParticipants(Set<MessageThreadParticipantRepresentationPotential> partnerParticipants) {
        this.partnerParticipants = partnerParticipants;
    }

    public MessageThreadParticipantsRepresentationPotential addParticipants(
            Collection<MessageThreadParticipantRepresentationPotential> participants) {
        this.participants.addAll(participants);
        return this;
    }

    public MessageThreadParticipantsRepresentationPotential addPartnerParticipants(
            Collection<MessageThreadParticipantRepresentationPotential> partnerParticipants) {
        this.partnerParticipants.addAll(partnerParticipants);
        return this;
    }

}
