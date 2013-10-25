package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.OfferRecommendedCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class OfferRecommendedCommentValidatorTest {

    @Autowired
    private Validator validator;

    private OfferRecommendedComment comment;

    private OfferRecommendedCommentValidator offerRecommendedCommentValidator;

    private SupervisorsValidator supervisorsValidator;

    @Test
    public void shouldSupportOfferRecommendedComment() {
        assertTrue(offerRecommendedCommentValidator.supports(OfferRecommendedComment.class));
    }

    @Test
    public void shouldRejectIfProjectTitleNotProvided() {
        comment.setProjectTitle(null);

        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(comment, "offerRecommendedComment");
        offerRecommendedCommentValidator.validate(comment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("projectTitle").getCode());
    }

    @Test
    public void shouldRejectIfProjectAbstractNotProvided() {
        comment.setProjectAbstract(null);

        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(comment, "offerRecommendedComment");
        offerRecommendedCommentValidator.validate(comment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("projectAbstract").getCode());
    }

    @Test
    public void shouldRejectIfRecommendedConditionsNotProvided() {
        comment.setRecommendedConditions(null);

        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(comment, "offerRecommendedComment");
        offerRecommendedCommentValidator.validate(comment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("recommendedConditions").getCode());
    }

    @Test
    public void shouldAcceptComment() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(comment, "offerRecommendedComment");
        offerRecommendedCommentValidator.validate(comment, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Before
    public void setup() {
        Supervisor supervisor1 = new SupervisorBuilder().id(4).build();
        Supervisor supervisor2 = new SupervisorBuilder().id(5).isPrimary(true).build();
        comment = new OfferRecommendedCommentBuilder().projectTitle("title").projectAbstract("abstract").recommendedConditionsAvailable(true)
                .recommendedStartDate(DateTime.now().plusDays(1).toDate()).recommendedConditions("conditions").supervisors(supervisor1, supervisor2).build();
        offerRecommendedCommentValidator = new OfferRecommendedCommentValidator();
        offerRecommendedCommentValidator.setValidator((javax.validation.Validator) validator);
        supervisorsValidator = new SupervisorsValidator();
        offerRecommendedCommentValidator.setSupervisorsValidator(supervisorsValidator);
    }
}
