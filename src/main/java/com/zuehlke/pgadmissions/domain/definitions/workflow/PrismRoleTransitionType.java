package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuelhke.pgadmissions.workflow.resolvers.role.transition.BranchResolver;
import com.zuelhke.pgadmissions.workflow.resolvers.role.transition.CreateResolver;
import com.zuelhke.pgadmissions.workflow.resolvers.role.transition.DeleteResolver;
import com.zuelhke.pgadmissions.workflow.resolvers.role.transition.ExhumeResolver;
import com.zuelhke.pgadmissions.workflow.resolvers.role.transition.RetireResolver;
import com.zuelhke.pgadmissions.workflow.resolvers.role.transition.ReviveResolver;
import com.zuelhke.pgadmissions.workflow.resolvers.role.transition.RoleTransitionResolver;
import com.zuelhke.pgadmissions.workflow.resolvers.role.transition.UpdateResolver;

public enum PrismRoleTransitionType {

	DELETE(true, DeleteResolver.class), //
	RETIRE(false, RetireResolver.class), //
	CREATE(true, CreateResolver.class), //
	BRANCH(false, BranchResolver.class), //
	UPDATE(false, UpdateResolver.class), //
	EXHUME(true, ExhumeResolver.class), //
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
