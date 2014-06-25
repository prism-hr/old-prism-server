package com.zuehlke.pgadmissions.domain.enums;

import java.util.HashMap;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.System;

public enum PrismScope {
    
    SYSTEM(System.class, 1),
    INSTITUTION(Institution.class, 2),
    PROGRAM(Program.class, 3),
    PROJECT(Project.class, 4),
    APPLICATION(Application.class, 5);
    
    private Class<? extends Resource> resourceClass;
    
    private Integer precedence;
    
    private static HashMultimap<PrismScope, PrismScope> descendentScopes = HashMultimap.create();
    
    private static HashMap<Class<? extends Resource>, PrismScope> resourceScopes = Maps.newHashMap();
    
    static {
        descendentScopes.put(SYSTEM, INSTITUTION);
        descendentScopes.put(INSTITUTION, PROGRAM);
        descendentScopes.put(PROGRAM, PROJECT);
        descendentScopes.put(PROGRAM, APPLICATION);
        descendentScopes.put(PROJECT, APPLICATION);
    }
    
    static {
        resourceScopes.put(System.class, SYSTEM);
        resourceScopes.put(Institution.class, INSTITUTION);
        resourceScopes.put(Program.class, PROGRAM);
        resourceScopes.put(Project.class, PROJECT);
        resourceScopes.put(Application.class, APPLICATION);
    }
    
    private PrismScope(Class<? extends Resource> resourceClass, int precedence) {
        this.resourceClass = resourceClass;
        this.precedence = precedence;
    }
    
    public  Class<? extends Resource> getResourceClass() {
        return resourceClass;
    }
    
    public Integer getPrecedence() {
        return precedence;
    }
    
    public Set<PrismScope> getDescendentScopes(PrismScope parentScope) {
        return descendentScopes.get(parentScope);
    }
    
    public static PrismScope getResourceScope(Class<? extends Resource> resourceClass) {
        return resourceScopes.get(resourceClass);
    }
    
    public String getLowerCaseName() {
        return resourceClass.getSimpleName().toLowerCase();
    }
    
}
