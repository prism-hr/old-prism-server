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
    
    SYSTEM(System.class, 1, "SM", null), //
    INSTITUTION(Institution.class, 2, "IN", 50), //
    PROGRAM(Program.class, 3, "PM", 50), //
    PROJECT(Project.class, 4, "PT", 50), //
    APPLICATION(Application.class, 5, "AN", 50);
    
    private Class<? extends Resource> resourceClass;
    
    private Integer precedence;
    
    private String shortCode;
    
    private Integer resourceListRecordsToRetrieve;
    
    private static final HashMap<Class<? extends Resource>, PrismScope> resourceScopes = Maps.newHashMap();
    
    static {
        resourceScopes.put(System.class, SYSTEM);
        resourceScopes.put(Institution.class, INSTITUTION);
        resourceScopes.put(Program.class, PROGRAM);
        resourceScopes.put(Project.class, PROJECT);
        resourceScopes.put(Application.class, APPLICATION);
    }
    
    private PrismScope(Class<? extends Resource> resourceClass, int precedence, String shortCode, Integer resourceListRecordsToRetrieve) {
        this.resourceClass = resourceClass;
        this.precedence = precedence;
        this.shortCode = shortCode;
        this.resourceListRecordsToRetrieve = resourceListRecordsToRetrieve;
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

    public final Integer getResourceListRecordsToRetrieve() {
        return resourceListRecordsToRetrieve;
    }

    public static PrismScope getResourceScope(Class<? extends Resource> resourceClass) {
        return resourceScopes.get(resourceClass);
    }

    public String getLowerCaseName() {
        return resourceClass.getSimpleName().toLowerCase();
    }
    
}
