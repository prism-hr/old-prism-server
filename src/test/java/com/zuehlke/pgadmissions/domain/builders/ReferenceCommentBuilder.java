package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.User;

public class ReferenceCommentBuilder {

    private Integer id;

    private Document document;
    private boolean suitableForUcl;
    private boolean suitableForProgramme;
    private Integer applicantRating;
    private String comment;
    private User user;
    private User providedBy;
    private List<Score> scores = new ArrayList<Score>();

    private Application application;
    private Date date;

    public ReferenceCommentBuilder date(Date date) {
        this.date = date;
        return this;
    }

    public ReferenceCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ReferenceCommentBuilder document(Document document) {
        this.document = document;
        return this;
    }

    public ReferenceCommentBuilder user(User user) {
        this.user = user;
        return this;
    }

    public ReferenceCommentBuilder comment(String comment) {
        this.comment = comment;
        return this;
    }

    public ReferenceCommentBuilder application(Application application) {
        this.application = application;
        return this;
    }

    public ReferenceCommentBuilder suitableForUcl(boolean suitableForUcl) {
        this.suitableForUcl = suitableForUcl;
        return this;
    }

    public ReferenceCommentBuilder suitableForProgramme(boolean suitableForProgramme) {
        this.suitableForProgramme = suitableForProgramme;
        return this;
    }

    public ReferenceCommentBuilder applicantRating(Integer applicantRating) {
        this.applicantRating = applicantRating;
        return this;
    }

    public ReferenceCommentBuilder providedBy(User providedBy) {
        this.providedBy = providedBy;
        return this;
    }

    public ReferenceCommentBuilder scores(Score... scores) {
        this.scores.addAll(Arrays.asList(scores));
        return this;
    }

    public ReferenceComment build() {
        ReferenceComment reference = new ReferenceComment();
        reference.setId(id);
        reference.setSuitableForProgramme(suitableForProgramme);
        reference.setSuitableForInstitution(suitableForUcl);
        reference.setRating(applicantRating);
        reference.setContent(comment);
        reference.setUser(user);
        reference.setDelegateProvider(providedBy);
        if (document != null) {
            reference.addDocument(document);
        }
        reference.setApplication(application);
        reference.setCreatedTimestamp(date);
        reference.getScores().addAll(scores);
        return reference;
    }
}
