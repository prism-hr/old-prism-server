package com.zuehlke.pgadmissions.workflow.evaluators;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.department.Department;

@Component
public class DepartmentProgramsEvaluator implements ResourceCompletenessEvaluator<Department> {

    @Override
    public boolean evaluate(Department department) {
        return !department.getImportedPrograms().isEmpty();
    }

}
