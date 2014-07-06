package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.System;

public enum PrismScope {
    
    SYSTEM(System.class, 1), //
    INSTITUTION(Institution.class, 2), //
    PROGRAM(Program.class, 3), //
    PROJECT(Project.class, 4), //
    APPLICATION(Application.class, 5);
    
    private Class<? extends Resource> resourceClass;
    
    private Integer precedence;
    
    private static HashMap<Class<? extends Resource>, PrismScope> resourceScopes = Maps.newHashMap();
    
    static {
        resourceScopes.put(System.class, SYSTEM);
        resourceScopes.put(Institution.class, INSTITUTION);
        resourceScopes.put(Program.class, PROGRAM);
        resourceScopes.put(Project.class, PROJECT);
        resourceScopes.put(Application.class, APPLICATION);
    }
    
    private static List<PrismScope> creatableScopes = Lists.newArrayList();
    
    static {
        for (PrismScope scope : PrismScope.values()) {
            if (scope.getPrecedence() > 1) {
                creatableScopes.add(scope);
            }
        }
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
    
    public static PrismScope getResourceScope(Class<? extends Resource> resourceClass) {
        return resourceScopes.get(resourceClass);
    }
    
    public static List<PrismScope> getCreatableScopes() {
        return creatableScopes;
    }

    public String getLowerCaseName() {
        return resourceClass.getSimpleName().toLowerCase();
    }
    
}