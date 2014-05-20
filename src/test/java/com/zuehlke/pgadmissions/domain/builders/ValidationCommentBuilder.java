package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;

public class ValidationCommentBuilder {

    private User user;
    private Application application;
    private ValidationQuestionOptions qualifiedForPhd;
    private ValidationQuestionOptions englishCompentencyOk;
    private ResidenceStatus homeOrOverseas;
    private DateTime date;
    private String comment;
    private Integer id;
    private List<Document> documents = new ArrayList<Document>();

    public ValidationCommentBuilder user(User user) {
        this.user = user;
        return this;
    }

    public ValidationCommentBuilder documents(Document... documents) {
        this.documents.addAll(Arrays.asList(documents));
        return this;
    }

    public ValidationCommentBuilder application(Application application) {
        this.application = application;
        return this;
    }

    public ValidationCommentBuilder qualifiedForPhd(ValidationQuestionOptions qualifiedForPhd) {
        this.qualifiedForPhd = qualifiedForPhd;
        return this;
    }

    public ValidationCommentBuilder englishCompentencyOk(ValidationQuestionOptions englishCompentencyOk) {
        this.englishCompentencyOk = englishCompentencyOk;
        return this;
    }

    public ValidationCommentBuilder homeOrOverseas(ResidenceStatus homeOrOverseas) {
        this.homeOrOverseas = homeOrOverseas;
        return this;
    }

    public ValidationCommentBuilder date(DateTime date) {
        this.date = date;
        return this;
    }

    public ValidationCommentBuilder comment(String comment) {
        this.comment = comment;
        return this;
    }

    public ValidationCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ValidationComment build() {
        ValidationComment validationComment = new ValidationComment();
        validationComment.setApplication(application);
        validationComment.setContent(comment);
        validationComment.setCreatedTimestamp(date);
        validationComment.setEnglishCompetencyOk(englishCompentencyOk);
        validationComment.setHomeOrOverseas(homeOrOverseas);
        validationComment.setId(id);
        validationComment.setQualifiedForPhd(qualifiedForPhd);
        validationComment.setUser(user);
        validationComment.getDocuments().addAll(documents);
        return validationComment;
    }
}
