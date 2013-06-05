package com.zuehlke.pgadmissions.domain.builders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.AdmitterComment;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;

public class AdmitterCommentBuilder {

    private CommentType type;  
    private ValidationQuestionOptions qualifiedForPhd;
    private ValidationQuestionOptions englishCompentencyOk;
    private HomeOrOverseas homeOrOverseas;
    private ApplicationForm application;
    private RegisteredUser user;
    private String strComment;
    private Integer id;
    private Date createdTimeStamp;
    private List<Score> scores = Lists.newArrayList();
    
    public AdmitterCommentBuilder date(Date createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
        return this;
    }
    
    public AdmitterCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }
    
    public AdmitterCommentBuilder application (ApplicationForm application){
        this.application = application;
        return this;
    }

    public AdmitterCommentBuilder comment (String comment){
        this.strComment = comment;
        return this;
    }
    
    public AdmitterCommentBuilder user (RegisteredUser user){
        this.user = user;
        return this;
    }
    
    public AdmitterCommentBuilder scores(Score... scores){
        this.scores.addAll(Arrays.asList(scores));
        return this;
    }
    
    public AdmitterCommentBuilder type(CommentType type) {
        this.type=type;
        return this;
    }
    
    public AdmitterCommentBuilder qualifiedForPhd(ValidationQuestionOptions qualifiedForPhd) {
        this.qualifiedForPhd=qualifiedForPhd;
        return this;
    }
    
    public AdmitterCommentBuilder englishCompentencyOk(ValidationQuestionOptions englishCompentencyOk) {
        this.englishCompentencyOk=englishCompentencyOk;
        return this;
    }
    
    public AdmitterCommentBuilder homeOrOverseas(HomeOrOverseas homeOrOverseas) {
        this.homeOrOverseas=homeOrOverseas;
        return this;
    }
    
    public AdmitterComment build() {
        AdmitterComment comment = new AdmitterComment();
        comment.setApplication(application);
        comment.setComment(strComment);
        comment.setId(id);
        comment.setUser(user);
        comment.setDate(createdTimeStamp);
        comment.getScores().addAll(scores);
        comment.setType(type);
        comment.setQualifiedForPhd(qualifiedForPhd);
        comment.setEnglishCompentencyOk(englishCompentencyOk);
        comment.setHomeOrOverseas(homeOrOverseas);
        return comment;
    }
}
