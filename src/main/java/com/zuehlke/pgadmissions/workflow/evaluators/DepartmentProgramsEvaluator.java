package com.zuehlke.pgadmissions.workflow.evaluators;

import com.zuehlke.pgadmissions.domain.resource.department.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentProgramsEvaluator implements ResourceCompletenessEvaluator<Department> {

    @Override
    public boolean evaluate(Department department) {
        return !department.getPrograms().isEmpty();
    }

}
