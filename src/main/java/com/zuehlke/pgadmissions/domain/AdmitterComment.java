package com.zuehlke.pgadmissions.domain;

import com.zuehlke.pgadmissions.domain.enums.CommentPropertyType;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;

public class AdmitterComment extends Comment {

    private static final long serialVersionUID = 8991440051685308411L;
    
    public ValidationQuestionOptions getQualifiedForPhd() {
    	return ValidationQuestionOptions.valueOf(super.getCommentProperty(CommentPropertyType.ACADMEMICALLYQUALIFIED).getValueText());
    }
    
    public void setQualifiedForPhd(ValidationQuestionOptions qualifiedForPhd) {
    	super.setCommentProperty(CommentPropertyType.ACADMEMICALLYQUALIFIED, qualifiedForPhd.toString());
    }
    
    public ValidationQuestionOptions getEnglishCompentencyOk() {
    	return ValidationQuestionOptions.valueOf(super.getCommentProperty(CommentPropertyType.LINGUISTICALLYQUALIFIED).getValueText());
    }
    
    public void setEnglishCompentencyOk(ValidationQuestionOptions englishCompentencyOk) {
    	super.setCommentProperty(CommentPropertyType.LINGUISTICALLYQUALIFIED, englishCompentencyOk.toString());
    }
    
    public HomeOrOverseas getHomeOrOverseas() {
    	return HomeOrOverseas.valueOf(super.getCommentProperty(CommentPropertyType.FEESTATUS).getValueText());
    }
    
    public void setHomeOrOverseas(HomeOrOverseas homeOrOverseas) {
    	super.setCommentProperty(CommentPropertyType.FEESTATUS, homeOrOverseas.toString());
    }

}