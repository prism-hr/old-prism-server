update resource_state inner join project
    on resource_state.project_id = project.id
set project.due_date = null
where resource_state.state_id in ("PROJECT_APPROVED",
    "PROJECT_DISABLED_COMPLETED")
;

update resource_state inner join program
    on resource_state.program_id = program.id
set program.due_date = null
where resource_state.state_id in ("PROGRAM_APPROVED",
    "PROGRAM_DISABLED_COMPLETED")
;

update resource_state inner join department
    on resource_state.department_id = department.id
set department.due_date = null
where resource_state.state_id in ("DEPARTMENT_APPROVED",
    "DEPARTMENT_DISABLED_COMPLETED")
;

update resource_state inner join institution
    on resource_state.institution_id = institution.id
set institution.due_date = null
where resource_state.state_id in ("INSTITUTION_APPROVED",
    "INSTITUTION_DISABLED_COMPLETED")
;
