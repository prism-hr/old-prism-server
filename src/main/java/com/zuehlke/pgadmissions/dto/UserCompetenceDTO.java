package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

public class UserCompetenceDTO {

    public Integer user;

    public Integer competence;

    public Long ratingCount;

    public BigDecimal ratingSum;

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public Integer getCompetence() {
        return competence;
    }

    public void setCompetence(Integer competence) {
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
