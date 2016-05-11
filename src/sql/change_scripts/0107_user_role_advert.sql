alter table user_role
	add column advert_id int(10) unsigned after application_id,
	add index (advert_id),
	add foreign key (advert_id) references advert (id)
;

update institution inner join user_role
	on institution.id = user_role.institution_id
set user_role.advert_id = institution.advert_id
;

update department inner join user_role
	on department.id = user_role.department_id
set user_role.advert_id = department.advert_id
;

update program inner join user_role
	on program.id = user_role.program_id
set user_role.advert_id = program.advert_id
;

update project inner join user_role
	on project.id = user_role.project_id
set user_role.advert_id = project.advert_id
;
