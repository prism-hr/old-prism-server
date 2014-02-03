package com.zuehlke.pgadmissions.domain;


public interface SelfReferringImportedObject extends ImportedObject {

    SelfReferringImportedObject getEnabledObject();
    
    void setEnabledObject(SelfReferringImportedObject enabledObject);
}
