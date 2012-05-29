UPDATE REGISTERED_USER SET originally_program_id = (select PROJECT.program_id from PROJECT where REGISTERED_USER.originally_project_id = PROJECT.id )
;