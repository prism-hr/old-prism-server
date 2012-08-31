package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Domicile;

public class DomicileBuilder {
    
    private Integer id;
    
    private String name;
    
    private Boolean enabled;
    
    public DomicileBuilder id(Integer id){
        this.id = id;
        return this;
    }
    
    public DomicileBuilder name(String name){
        this.name = name;
        return this;
    }
        
    public DomicileBuilder enabled(Boolean enabled){
        this.enabled = enabled;
        return this;
    }
    
    public Domicile toDomicile() {
        Domicile domicile = new Domicile();
        domicile.setId(id);
        domicile.setName(name);
        domicile.setEnabled(enabled);
        return domicile;
    }
}
