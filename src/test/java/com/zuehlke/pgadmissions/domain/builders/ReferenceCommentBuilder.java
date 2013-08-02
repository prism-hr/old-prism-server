package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ReferenceCommentBuilder {

    private Integer id;

    private Document document;
    private Referee referee;
    private boolean suitableForUcl;
    private boolean suitableForProgramme;
    private Integer applicantRating;
    private String comment;
    private RegisteredUser user;
    private RegisteredUser providedBy;
    private List<Score> scores = new ArrayList<Score>();

    private ApplicationForm application;
    private Date date;

    public ReferenceCommentBuilder date(Date date) {
        this.date = date;
        return this;
    }

    public ReferenceCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ReferenceCommentBuilder referee(Referee referee) {
        this.referee = referee;
        return this;
    }

    public ReferenceCommentBuilder document(Document document) {
        this.document = document;
        return this;
    }

    public ReferenceCommentBuilder user(RegisteredUser user) {
        this.user = user;
        return this;
    }

    public ReferenceCommentBuilder comment(String comment) {
        this.comment = comment;
        return this;
    }

    public ReferenceCommentBuilder application(ApplicationForm application) {
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

    public ReferenceCommentBuilder suitableCandidateForProgramme(Integer applicantRating) {
        this.applicantRating = applicantRating;
        return this;
    }

    public ReferenceCommentBuilder providedBy(RegisteredUser providedBy) {
        this.providedBy = providedBy;
        return this;
    }

    public ReferenceCommentBuilder scores(Score... scores) {
        for (Score score : scores) {
            this.scores.add(score);
        }
        ;
        return this;
    }

    public ReferenceComment build() {
        ReferenceComment reference = new ReferenceComment();
        reference.setId(id);
        reference.setReferee(referee);
        reference.setSuitableForProgramme(suitableForProgramme);
        reference.setSuitableForUCL(suitableForUcl);
        reference.setApplicantRating(applicantRating);
        reference.setComment(comment);
        reference.setUser(user);
        reference.setProvidedBy(providedBy);
        reference.setType(CommentType.REFERENCE);
        if (document != null) {
            reference.getDocuments().add(document);
        }
        reference.setApplication(application);
        reference.setDate(date);
        reference.getScores().addAll(scores);
        return reference;
    }
}
