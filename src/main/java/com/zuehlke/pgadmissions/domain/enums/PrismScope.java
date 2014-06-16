package com.zuehlke.pgadmissions.domain.enums;

import java.util.Set;

import com.google.common.collect.HashMultimap;

public enum PrismScope {
    
    SYSTEM("System"),
    INSTITUTION("Institution"),
    PROGRAM("Program"),
    PROJECT("Project"),
    APPLICATION("Application");
    
    private String simpleName;
    
    private static HashMultimap<PrismScope, PrismScope> descendentScopes = HashMultimap.create();
    
    static {
        descendentScopes.put(SYSTEM, INSTITUTION);
        descendentScopes.put(INSTITUTION, PROGRAM);
        descendentScopes.put(PROGRAM, PROJECT);
        descendentScopes.put(PROGRAM, APPLICATION);
        descendentScopes.put(PROJECT, APPLICATION);
    }
    
    private PrismScope(String simpleName) {
        this.simpleName = simpleName;
    }
    
    public String getSimpleName() {
        return simpleName;
    }
    
    public Set<PrismScope> getDescendentScopes(PrismScope parent) {
        return descendentScopes.get(parent);
    }
    
    public String getCanonicalName() {
        return "com.zuehlke.pgadmissions.domain." + simpleName;
    }
    
    public String getLowerCaseName() {
        return simpleName.toLowerCase();
    }
    
}
