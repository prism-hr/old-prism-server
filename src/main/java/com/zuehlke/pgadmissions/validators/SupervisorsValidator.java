package com.zuehlke.pgadmissions.validators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.Supervisor;

@Component
public class SupervisorsValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ApprovalRound.class.equals(clazz) || OfferRecommendedComment.class.equals(clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validate(Object target, Errors errors) {
        List<Supervisor> supervisors;
        try {
            supervisors = (List<Supervisor>) PropertyUtils.getSimpleProperty(target, "supervisors");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (supervisors.size() != 2) {
            errors.rejectValue("supervisors", "approvalround.supervisors.incomplete");
        } else {
            int primarySupervisors = 0;
            for (Supervisor supervisor : supervisors) {
                if (BooleanUtils.isTrue(supervisor.getIsPrimary())) {
                    primarySupervisors++;
                }
            }

            if (primarySupervisors != 1) {
                errors.rejectValue("supervisors", "approvalround.supervisors.noprimary");
            }
            
            List<Supervisor> inspectSupervisors = new ArrayList<Supervisor>(supervisors.size());
            for (Supervisor supervisor : supervisors) {
                if (!inspectSupervisors.isEmpty()) {
                    if (inspectSupervisors.contains(supervisor)) {
                        errors.rejectValue("supervisors", "approvaround.supervisors.duplicates");
                    }
                }
                inspectSupervisors.add(supervisor);
            }
        }
    }

}
