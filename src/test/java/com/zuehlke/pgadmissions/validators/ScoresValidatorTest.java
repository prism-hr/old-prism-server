package com.zuehlke.pgadmissions.validators;

import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.builders.ScoreBuilder;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ScoresValidatorTest {
    
    @Autowired
    private Validator validator;
    
    private ScoresValidator scoresValidator;

    @Test
    public void shouldInvalidateWhenRequiredTextScoreIsEmpty() {
        Question question = new Question();
        question.setRequired(true);

        Score score = new ScoreBuilder().originalQuestion(question).questionType(QuestionType.TEXT).build();
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(score, "score");
        scoresValidator.validate(score, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getGlobalError().getCode());
    }

    @Test
    public void shouldInvalidateWhenRequiredDateScoreIsEmpty() {
        Question question = new Question();
        question.setRequired(true);

        Score score = new ScoreBuilder().originalQuestion(question).questionType(QuestionType.DATE).build();
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(score, "score");
        scoresValidator.validate(score, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getGlobalError().getCode());
    }

    @Test
    public void shouldInvalidateWhenDateBeforeMinDate() {
        Question question = new Question();
        question.setRequired(false);
        question.setMinDate("2013-07-15");

        Score score = new ScoreBuilder().originalQuestion(question).questionType(QuestionType.DATE)
                .dateResponse(new GregorianCalendar(2013, 3, 12).getTime()).build();
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(score, "score");
        scoresValidator.validate(score, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notbefore", mappingResult.getGlobalError().getCode());
    }

    @Test
    public void shouldInvalidateWhenDateAfterMaxDate() {
        Question question = new Question();
        question.setRequired(false);
        question.setMaxDate("2013-07-15");

        Score score = new ScoreBuilder().originalQuestion(question).questionType(QuestionType.DATE)
                .dateResponse(new GregorianCalendar(2013, 11, 12).getTime()).build();
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(score, "score");
        scoresValidator.validate(score, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notafter", mappingResult.getGlobalError().getCode());
    }

//    @Test
//    public void shouldInvalidateWhenFirstDateIsMissingForDateRange() {
//        Question question1 = new Question();
//        question1.setRequired(true);
//
//        Date secondDate = new GregorianCalendar(2013, 03, 12).getTime();
//        Score score1 = new ScoreBuilder().originalQuestion(question1).questionType(QuestionType.DATE_RANGE).secondDateResponse(secondDate).build();
//        Comment comment = new CommentBuilder().scores(score1).build();
//        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(comment, "comment");
//        validator.validate(comment, mappingResult);
//        Assert.assertEquals(1, mappingResult.getErrorCount());
//        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("scores[0]").getCode());
//    }

//    @Test
//    public void shouldInvalidateWhenFirstDateIsAfterSecondDateForDateRange() {
//        Question question1 = new Question();
//        question1.setRequired(false);
//
//        Date firstDate = new GregorianCalendar(2013, 11, 12).getTime();
//        Date secondDate = new GregorianCalendar(2013, 03, 12).getTime();
//        Score score1 = new ScoreBuilder().originalQuestion(question1).questionType(QuestionType.DATE_RANGE).dateResponse(firstDate)
//                .secondDateResponse(secondDate).build();
//        Comment comment = new CommentBuilder().scores(score1).build();
//        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(comment, "comment");
//        validator.validate(comment, mappingResult);
//        Assert.assertEquals(1, mappingResult.getErrorCount());
//        Assert.assertEquals("daterange.field.notafter", mappingResult.getFieldError("scores[0]").getCode());
//    }
    
    @Test
    public void shouldInvalidateWhenRequiredDropdownScoreIsEmpty() {
        Question question = new Question();
        question.setRequired(true);

        Score score = new ScoreBuilder().originalQuestion(question).questionType(QuestionType.DROPDOWN).build();
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(score, "score");
        scoresValidator.validate(score, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getGlobalError().getCode());
    }
    
    @Test
    public void shouldInvalidateWhenRequiredRatingScoreIsEmpty() {
        Question question = new Question();
        question.setRequired(true);

        Score score = new ScoreBuilder().originalQuestion(question).questionType(QuestionType.RATING).ratingResponse(null).build();
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(score, "score");
        scoresValidator.validate(score, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getGlobalError().getCode());
    }
    
    @Test
    public void shouldInvalidateWhenRatingScoreHasNotCorrectValue() {
        Question question = new Question();
        question.setRequired(false);

        Score score = new ScoreBuilder().originalQuestion(question).questionType(QuestionType.RATING).ratingResponse(6).build();
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(score, "score");
        scoresValidator.validate(score, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getGlobalError().getCode());
    }

    @Before
    public void prepare(){
        scoresValidator = new ScoresValidator();
        scoresValidator.setValidator((javax.validation.Validator) validator);
    }

}
