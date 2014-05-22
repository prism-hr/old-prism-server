package com.zuehlke.pgadmissions.domain.builders;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.AdmitterComment;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;

public class AdmitterCommentBuilder {

    private ValidationQuestionOptions qualifiedForPhd;
    private ValidationQuestionOptions englishCompentencyOk;
    private ResidenceStatus homeOrOverseas;
    private Application application;
    private User user;
    private String strComment;
    private Integer id;
    private DateTime createdTimeStamp;
    private List<Score> scores = Lists.newArrayList();

    public AdmitterCommentBuilder date(DateTime createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
        return this;
    }

    public AdmitterCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public AdmitterCommentBuilder application(Application application) {
        this.application = application;
        return this;
    }

    public AdmitterCommentBuilder comment(String comment) {
        this.strComment = comment;
        return this;
    }

    public AdmitterCommentBuilder user(User user) {
        this.user = user;
        return this;
    }

    public AdmitterCommentBuilder scores(Score... scores) {
        this.scores.addAll(Arrays.asList(scores));
        return this;
    }

    public AdmitterCommentBuilder qualifiedForPhd(ValidationQuestionOptions qualifiedForPhd) {
        this.qualifiedForPhd = qualifiedForPhd;
        return this;
    }

    public AdmitterCommentBuilder englishCompentencyOk(ValidationQuestionOptions englishCompentencyOk) {
        this.englishCompentencyOk = englishCompentencyOk;
        return this;
    }

    public AdmitterCommentBuilder homeOrOverseas(ResidenceStatus homeOrOverseas) {
        this.homeOrOverseas = homeOrOverseas;
        return this;
    }

    public AdmitterComment build() {
        AdmitterComment comment = new AdmitterComment();
        comment.setApplication(application);
        comment.setContent(strComment);
        comment.setId(id);
        comment.setUser(user);
        comment.setCreatedTimestamp(createdTimeStamp);
        comment.getScores().addAll(scores);
        comment.setQualifiedForPhd(qualifiedForPhd);
        comment.setEnglishCompetencyOk(englishCompentencyOk);
        comment.setHomeOrOverseas(homeOrOverseas);
        return comment;
    }
}
