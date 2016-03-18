package uk.co.alumeni.prism.rest.representation.action;

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Collection;
import java.util.Set;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class ActionMessageThreadParticipantRepresentation {

    private PrismRole role;

    private Set<UserRepresentationSimple> users = newLinkedHashSet();

    public PrismRole getRole() {
        return role;
    }

    public Set<UserRepresentationSimple> getUsers() {
        return users;
    }

    public ActionMessageThreadParticipantRepresentation withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public ActionMessageThreadParticipantRepresentation withUsers(Collection<UserRepresentationSimple> users) {
        this.users.addAll(users);
        return this;
    }

}
