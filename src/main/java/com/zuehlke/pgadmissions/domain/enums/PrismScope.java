package com.zuehlke.pgadmissions.domain.enums;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.System;

public enum PrismScope {
    
    SYSTEM(System.class),
    INSTITUTION(Institution.class),
    PROGRAM(Program.class),
    PROJECT(Project.class),
    APPLICATION(Application.class);
    
    private Class<? extends PrismResource> resourceClass;
    
    private static HashMultimap<PrismScope, PrismScope> descendentScopes = HashMultimap.create();
    
    static {
        descendentScopes.put(SYSTEM, INSTITUTION);
        descendentScopes.put(INSTITUTION, PROGRAM);
        descendentScopes.put(PROGRAM, PROJECT);
        descendentScopes.put(PROGRAM, APPLICATION);
        descendentScopes.put(PROJECT, APPLICATION);
    }
    
    private PrismScope(Class<? extends PrismResource> resourceClass) {
        this.resourceClass = resourceClass;
    }
    
    public  Class<? extends PrismResource> getResourceClass() {
        return resourceClass;
    }
    
    public Set<PrismScope> getDescendentScopes(PrismScope parentScope) {
        return descendentScopes.get(parentScope);
    }
    
    public String getLowerCaseName() {
        return resourceClass.getSimpleName().toLowerCase();
    }
    
}
