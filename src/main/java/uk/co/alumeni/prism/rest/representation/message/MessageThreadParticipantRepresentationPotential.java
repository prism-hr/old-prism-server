package uk.co.alumeni.prism.rest.representation.message;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

import java.util.Collection;
import java.util.Set;

import static com.google.common.collect.Sets.newLinkedHashSet;

public class MessageThreadParticipantRepresentationPotential {

    private PrismRole role;

    private Set<UserRepresentationSimple> users = newLinkedHashSet();

    public PrismRole getRole() {
        return role;
    }

    public Set<UserRepresentationSimple> getUsers() {
        return users;
    }

    public MessageThreadParticipantRepresentationPotential withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public MessageThreadParticipantRepresentationPotential withUsers(Collection<UserRepresentationSimple> users) {
        this.users.addAll(users);
        return this;
    }

}
