package uk.co.alumeni.prism.domain.definitions.workflow;

import uk.co.alumeni.prism.workflow.resolvers.role.transition.*;

public enum PrismRoleTransitionType {

    DELETE(true, DeleteResolver.class), //
    RETIRE(false, DeleteResolver.class), //
    UPDATE(false, UpdateResolver.class), //
    EXHUME(true, UpdateResolver.class), //
    CREATE(true, CreateResolver.class), //
    BRANCH(false, BranchResolver.class), //
    REVIVE(false, ReviveResolver.class);

    private boolean specified;

    private Class<? extends RoleTransitionResolver> resolver;

    private PrismRoleTransitionType(boolean specified, Class<? extends RoleTransitionResolver> resolver) {
        this.specified = specified;
        this.resolver = resolver;
    }

    public final boolean isSpecified() {
        return specified;
    }

    public Class<? extends RoleTransitionResolver> getResolver() {
        return resolver;
    }

}
