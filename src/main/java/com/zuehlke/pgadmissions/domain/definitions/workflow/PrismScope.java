package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.HashMap;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.System;

public enum PrismScope {
    
    SYSTEM(System.class, 1, "SM"), //
    INSTITUTION(Institution.class, 2, "IN"), //
    PROGRAM(Program.class, 3, "PM"), //
    PROJECT(Project.class, 4, "PT"), //
    APPLICATION(Application.class, 5, "AN");
    
    private Class<? extends Resource> resourceClass;
    
    private Integer precedence;
    
    private String shortCode;
    
    private static HashMap<Class<? extends Resource>, PrismScope> resourceScopes = Maps.newHashMap();
    
    static {
        resourceScopes.put(System.class, SYSTEM);
        resourceScopes.put(Institution.class, INSTITUTION);
        resourceScopes.put(Program.class, PROGRAM);
        resourceScopes.put(Project.class, PROJECT);
        resourceScopes.put(Application.class, APPLICATION);
    }
    
    private static HashMap<PrismScope, PrismAction> fallbackActions = Maps.newHashMap();
    
    static {
        fallbackActions.put(SYSTEM, PrismAction.SYSTEM_VIEW_APPLICATION_LIST);
        fallbackActions.put(INSTITUTION, PrismAction.SYSTEM_VIEW_INSTITUTION_LIST);
        fallbackActions.put(PROGRAM, PrismAction.SYSTEM_VIEW_PROGRAM_LIST);
        fallbackActions.put(PROJECT, PrismAction.SYSTEM_VIEW_PROJECT_LIST);
        fallbackActions.put(APPLICATION, PrismAction.SYSTEM_VIEW_APPLICATION_LIST);
    }
    
    private PrismScope(Class<? extends Resource> resourceClass, int precedence, String shortCode) {
        this.resourceClass = resourceClass;
        this.precedence = precedence;
        this.shortCode = shortCode;
    }
    
    public  Class<? extends Resource> getResourceClass() {
        return resourceClass;
    }
    
    public Integer getPrecedence() {
        return precedence;
    }
    
    public String getShortCode() {
        return shortCode;
    }

    public static PrismScope getResourceScope(Class<? extends Resource> resourceClass) {
        return resourceScopes.get(resourceClass);
    }
    
    public static PrismAction getFallbackAction(PrismScope scope) {
        return fallbackActions.get(scope);
    }

    public String getLowerCaseName() {
        return resourceClass.getSimpleName().toLowerCase();
    }
    
}
