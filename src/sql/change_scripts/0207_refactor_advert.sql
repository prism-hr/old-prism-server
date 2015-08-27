alter table advert
	add column user_id int(10) unsigned after id,
	add index (user_id),
	add foreign key (user_id) references user (id)
;

update advert inner join institution
	on institution.advert_id = advert.id
set advert.institution_id = institution.id,
	advert.system_id = institution.system_id

update advert inner join institution
	on advert.id = institution.advert_id
set advert.user_id = institution.user_id
;

update advert inner join department
	on advert.id = department.advert_id
set advert.user_id = department.user_id
;

update advert inner join program
	on advert.id = program.advert_id
set advert.user_id = program.user_id
;

update advert inner join project
	on advert.id = project.advert_id
set advert.user_id = project.user_id
;

alter table advert
	modify column user_id int(10) unsigned not null
;

alter table advert
	add column imported_opportunity_type_id int(10) unsigned after project_id,
	add index (imported_opportunity_type_id, sequence_identifier),
	add foreign key (imported_opportunity_type_id) references imported_entity (id)
;

alter table advert
	add column opportunity_category varchar(255) after imported_opportunity_type_id,
	add index (opportunity_category, sequence_identifier)
;

update advert inner join project
	on advert.project_id = project.id
set advert.institution_id = project.institution_id
;

update advert inner join program
	on advert.program_id = program.id
set advert.institution_id = program.institution_id
;

update advert inner join department
	on advert.department_id = department.id
set advert.institution_id = department.institution_id
;

update project inner join advert
	 on project.advert_id = advert.project_id
set advert.opportunity_category = project.opportunity_category
;

update program inner join advert
	 on program.advert_id = advert.program_id
set advert.opportunity_category = program.opportunity_category
;

update department inner join advert
	 on department.advert_id = advert.department_id
set advert.opportunity_category = department.opportunity_category
;

update institution inner join advert
	 on institution.advert_id = advert.id
set advert.opportunity_category = institution.opportunity_category
;

create table advert_condition (
	id int(10) unsigned not null auto_increment,
	advert_id int(10) unsigned not null,
	action_condition varchar(50) not null,
	partner_mode int(1) unsigned not null,
	primary key (id),
	unique index (advert_id, action_condition),
	foreign key (advert_id) references advert (id))
collate = utf8_general_ci
	engine = innodb
;

insert into advert_condition (advert_id, action_condition, partner_mode)
	select institution.advert_id, resource_condition.action_condition, resource_condition.partner_mode
	from resource_condition inner join institution
		on resource_condition.institution_id = institution.id
;

insert into advert_condition (advert_id, action_condition, partner_mode)
	select department.advert_id, resource_condition.action_condition, resource_condition.partner_mode
	from resource_condition inner join department
		on resource_condition.department_id = department.id
;

insert into advert_condition (advert_id, action_condition, partner_mode)
	select program.advert_id, resource_condition.action_condition, resource_condition.partner_mode
	from resource_condition inner join program
		on resource_condition.program_id = program.id
;

insert into advert_condition (advert_id, action_condition, partner_mode)
	select project.advert_id, resource_condition.action_condition, resource_condition.partner_mode
	from resource_condition inner join project
		on resource_condition.project_id = project.id
;

drop table resource_condition
;

alter table resource_study_option
	add column advert_id int (10) unsigned after id,
	add unique index (advert_id, imported_study_option_id),
	add foreign key (advert_id) references advert (id)
;

update resource_study_option inner join program
	on resource_study_option.program_id = program.id
set resource_study_option.advert_id = program.advert_id
;

update resource_study_option inner join project
	on resource_study_option.project_id = project.id
set resource_study_option.advert_id = project.advert_id
;

alter table resource_study_option
	drop index program_id,
	drop index project_id,
	drop foreign key resource_study_option_ibfk_1,
	drop foreign key resource_study_option_ibfk_4,
	drop foreign key resource_study_option_ibfk_5,
	drop column program_id,
	drop column project_id
;

rename table resource_study_option to advert_study_option
;

rename table resource_study_option_instance to advert_study_option_instance
;

alter table advert_study_option_instance
	change program_study_option_id advert_study_option_id int(10) unsigned not null
;

alter table resource_study_location
	add column advert_id int (10) unsigned after id,
	add unique index (advert_id, study_location),
	add foreign key (advert_id) references advert (id)
;

update resource_study_location inner join program
	on resource_study_location.program_id = program.id
set resource_study_location.advert_id = program.advert_id
;

update resource_study_location inner join project
	on resource_study_location.project_id = project.id
set resource_study_location.advert_id = project.advert_id
;

alter table resource_study_location
	drop index program_id,
	drop index project_id,
	drop foreign key resource_study_location_ibfk_1,
	drop foreign key resource_study_location_ibfk_3,
	drop foreign key resource_study_location_ibfk_4,
	drop column program_id,
	drop column project_id
;

rename table resource_study_location to advert_study_location
;
