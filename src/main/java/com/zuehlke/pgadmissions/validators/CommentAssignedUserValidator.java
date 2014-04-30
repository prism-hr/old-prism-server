package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.AssignSupervisorsComment;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;

@Component
public class CommentAssignedUserValidator implements Validator {
    // TODO validate CommentAsignedUser rather than supervisor and add check if the same user is not added twice
    
    @Override
    public boolean supports(Class<?> clazz) {
        return AssignSupervisorsComment.class.equals(clazz) || OfferRecommendedComment.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
//        List<Supervisor> supervisors;
//        try {
//            supervisors = (List<Supervisor>) PropertyUtils.getSimpleProperty(target, "supervisors");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        if (supervisors.size() != 2) {
//            errors.rejectValue("supervisors", "approvalround.supervisors.incomplete");
//        } else {
//            int primarySupervisors = 0;
//            for (Supervisor supervisor : supervisors) {
//                if (BooleanUtils.isTrue(supervisor.getIsPrimary())) {
//                    primarySupervisors++;
//                }
//            }
//
//            if (primarySupervisors != 1) {
//                errors.rejectValue("supervisors", "approvalround.supervisors.noprimary");
//            }
//        }
    }

}
