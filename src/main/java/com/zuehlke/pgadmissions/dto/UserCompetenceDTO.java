package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.user.User;

public class UserCompetenceDTO {

    public User user;

    public Competence competence;

    public Long ratingCount;

    public BigDecimal ratingSum;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Competence getCompetence() {
        return competence;
    }

    public void setCompetence(Competence competence) {
        this.competence = competence;
    }

    public Long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public BigDecimal getRatingSum() {
        return ratingSum;
    }

    public void setRatingSum(BigDecimal ratingSum) {
        this.ratingSum = ratingSum;
    }

}
