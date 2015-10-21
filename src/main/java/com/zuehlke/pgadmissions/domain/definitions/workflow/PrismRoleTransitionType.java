package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuehlke.pgadmissions.workflow.resolvers.role.transition.BranchResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.role.transition.CreateResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.role.transition.DeleteResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.role.transition.ReviveResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.role.transition.RoleTransitionResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.role.transition.UpdateResolver;

public enum PrismRoleTransitionType {

    DELETE(true, DeleteResolver.class), //
    RETIRE(false, DeleteResolver.class), //
    CREATE(true, CreateResolver.class), //
    BRANCH(false, BranchResolver.class), //
    REVIVE(false, ReviveResolver.class), //
    UPDATE(false, UpdateResolver.class), //
    EXHUME(true, UpdateResolver.class);

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
