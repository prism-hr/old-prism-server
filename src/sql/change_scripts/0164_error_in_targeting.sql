alter table imported_program
	modify column ucas_program_count int(10) unsigned
;

update imported_program
set ucas_program_count = null
where ucas_program_count = 0
;
