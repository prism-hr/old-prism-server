package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;

public class ScoreBuilder {

    private Integer id;

    private QuestionType questionType;

    private String question;

    private String textResponse;

    private Date dateResponse;

    private Date secondDateResponse;

    private Integer ratingResponse;
    
    private Boolean required;

    public ScoreBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ScoreBuilder questionType(QuestionType questionType) {
        this.questionType = questionType;
        return this;
    }


    public ScoreBuilder question(String question) {
        this.question = question;
        return this;
    }

    public ScoreBuilder textResponse(String textResponse) {
        this.textResponse = textResponse;
        return this;
    }
    
    public ScoreBuilder dateResponse(Date dateResponse) {
        this.dateResponse = dateResponse;
        return this;
    }
    
    public ScoreBuilder secondDateResponse(Date secondDateResponse) {
        this.secondDateResponse = secondDateResponse;
        return this;
    }
    
    public ScoreBuilder ratingResponse(Integer ratingResponse) {
        this.ratingResponse = ratingResponse;
        return this;
    }

    public ScoreBuilder required(Boolean required) {
        this.required = required;
        return this;
    }

    public Score build() {
        Score score = new Score();
        score.setId(id);
        score.setQuestionType(questionType);
        score.setQuestion(question);
        score.setTextResponse(textResponse);
        score.setDateResponse(dateResponse);
        score.setSecondDateResponse(secondDateResponse);
        score.setRatingResponse(ratingResponse);
        score.setRequired(required);
        return score;
    }
}
