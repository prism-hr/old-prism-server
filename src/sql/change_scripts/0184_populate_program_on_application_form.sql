UPDATE APPLICATION_FORM SET APPLICATION_FORM.program_id = (select PROJECT.program_id from PROJECT where APPLICATION_FORM.project_id = PROJECT.id )
;