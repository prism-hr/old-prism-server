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
    
    SYSTEM(System.class, 1),
    INSTITUTION(Institution.class, 2),
    PROGRAM(Program.class, 3),
    PROJECT(Project.class, 4),
    APPLICATION(Application.class, 5);
    
    private Class<? extends PrismResource> resourceClass;
    
    private Integer precedence;
    
    private static HashMultimap<PrismScope, PrismScope> descendentScopes = HashMultimap.create();
    
    static {
        descendentScopes.put(SYSTEM, INSTITUTION);
        descendentScopes.put(INSTITUTION, PROGRAM);
        descendentScopes.put(PROGRAM, PROJECT);
        descendentScopes.put(PROGRAM, APPLICATION);
        descendentScopes.put(PROJECT, APPLICATION);
    }
    
    private PrismScope(Class<? extends PrismResource> resourceClass, int precedence) {
        this.resourceClass = resourceClass;
    }
    
    public  Class<? extends PrismResource> getResourceClass() {
        return resourceClass;
    }
    
    public Integer getPrecedence() {
        return precedence;
    }
    
    public Set<PrismScope> getDescendentScopes(PrismScope parentScope) {
        return descendentScopes.get(parentScope);
    }
    
    public String getLowerCaseName() {
        return resourceClass.getSimpleName().toLowerCase();
    }
    
}
