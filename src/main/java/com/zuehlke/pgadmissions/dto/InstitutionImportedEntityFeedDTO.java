package com.zuehlke.pgadmissions.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;

public class InstitutionImportedEntityFeedDTO {

    public ImportedEntityFeed importedProgramFeed;
    
    public List<ImportedEntityFeed> importedEntityFeeds = Lists.newLinkedList();

    public ImportedEntityFeed getImportedProgramFeed() {
        return importedProgramFeed;
    }

    public void setImportedProgramFeed(ImportedEntityFeed importedProgramFeed) {
        this.importedProgramFeed = importedProgramFeed;
    }

    public List<ImportedEntityFeed> getImportedEntityFeeds() {
        return importedEntityFeeds;
    }
    
    public void addEntityFeed(ImportedEntityFeed importedEntityFeed) {
        importedEntityFeeds.add(importedEntityFeed);
    }
    
}
