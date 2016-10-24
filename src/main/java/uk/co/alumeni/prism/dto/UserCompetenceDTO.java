package uk.co.alumeni.prism.dto;

import uk.co.alumeni.prism.domain.Competence;
import uk.co.alumeni.prism.domain.user.User;

public class UserCompetenceDTO {

    public User user;

    public Competence competence;

    public Long ratingCount;

    public Long ratingSum;

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

    public Long getRatingSum() {
        return ratingSum;
    }

    public void setRatingSum(Long ratingSum) {
        this.ratingSum = ratingSum;
    }

}
