package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.SourcesOfInterest;

public class SourcesOfInterestBuilder {
    
    private String name;
    
    private Integer id;
    
    private boolean enabled;
    
    private String code;

    public SourcesOfInterestBuilder code(String code) {
        this.code = code;
        return this;
    }
    
    public SourcesOfInterestBuilder name(String ethName) {
        this.name = ethName;
        return this;
    }

    public SourcesOfInterestBuilder id(Integer ethId) {
        this.id = ethId;
        return this;
    }
    
    public SourcesOfInterestBuilder enabled(boolean enabled){
        this.enabled = enabled;
        return this;
    }

    public SourcesOfInterest build() {
        SourcesOfInterest soi = new SourcesOfInterest();
        soi.setId(id);
        soi.setName(name);
        soi.setEnabled(enabled);
        soi.setCode(code);
        return soi;
    }
}
