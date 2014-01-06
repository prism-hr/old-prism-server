package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;

@Entity(name="ADMITTER_COMMENT")
public class AdmitterComment extends Comment {

    private static final long serialVersionUID = 8991440051685308411L;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type")
    private CommentType type = CommentType.ADMITTER_COMMENT;
    
    @Enumerated(EnumType.STRING)
    @Column(name="qualified_for_phd")   
    private ValidationQuestionOptions qualifiedForPhd;
    
    @Enumerated(EnumType.STRING)
    @Column(name="english_compentency_ok")  
    private ValidationQuestionOptions englishCompentencyOk;
    
    @Enumerated(EnumType.STRING)
    @Column(name="home_or_overseas")
    private HomeOrOverseas homeOrOverseas;
    
    public void setType(CommentType type) {
    }

    @Override
    public CommentType getType() {
        return type;
    }
    
    public ValidationQuestionOptions getQualifiedForPhd() {
        return qualifiedForPhd;
    }
    
    public void setQualifiedForPhd(ValidationQuestionOptions qualifiedForPhd) {
        this.qualifiedForPhd = qualifiedForPhd;
    }
    
    public ValidationQuestionOptions getEnglishCompentencyOk() {
        return englishCompentencyOk;
    }
    
    public void setEnglishCompentencyOk(ValidationQuestionOptions englishCompentencyOk) {
        this.englishCompentencyOk = englishCompentencyOk;
    }
    
    public HomeOrOverseas getHomeOrOverseas() {
        return homeOrOverseas;
    }
    
    public void setHomeOrOverseas(HomeOrOverseas homeOrOverseas) {
        this.homeOrOverseas = homeOrOverseas;
    }

}
