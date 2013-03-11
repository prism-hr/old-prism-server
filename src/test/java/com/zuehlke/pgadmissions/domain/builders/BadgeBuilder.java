package com.zuehlke.pgadmissions.domain.builders;


import java.util.Date;

import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Program;

public class BadgeBuilder {

    private Integer id;
    private Program program;
    private String projectTitle;
    private Date closingDate;
    
    public BadgeBuilder id(Integer id){
        this.id = id;
        return this;
    }
    
    public BadgeBuilder program(Program program){
        this.program = program;
        return this;
    }
    
    public BadgeBuilder projectTitle(String projectTitle){
        this.projectTitle = projectTitle;
        return this;
    }
    
    
    public BadgeBuilder closingDate(Date closingDate){
        this.closingDate = closingDate;
        return this;
    }
    
    public Badge build(){
        Badge badge = new Badge();
        badge.setId(id);
        badge.setProgram(program);
        badge.setProjectTitle(projectTitle);
        badge.setClosingDate(closingDate);
        return badge;
    }
}
