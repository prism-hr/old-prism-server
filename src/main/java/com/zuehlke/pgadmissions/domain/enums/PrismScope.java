package com.zuehlke.pgadmissions.domain.enums;

public enum PrismScope {
    
    SYSTEM("System"),
    INSTITUTION("Institution"),
    PROGRAM("Program"),
    PROJECT("Project"),
    APPLICATION("Application");
    
    private String simpleName;
    
    private PrismScope(String simpleName) {
        this.simpleName = simpleName;
    }
    
    public String getSimpleName() {
        return simpleName;
    }
    
    public String getCanonicalName() {
        return "com.zuehlke.pgadmissions.domain." + simpleName;
    }
    
    public String getLowerCaseName() {
        return simpleName.toLowerCase();
    }
    
}
