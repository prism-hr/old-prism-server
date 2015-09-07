alter table application
	add column opportunity_category varchar(50) after advert_id,
	add index (opportunity_category, sequence_identifier)
;

alter table project
	add column opportunity_category varchar(50) after imported_opportunity_type_id,
	add index (opportunity_category, sequence_identifier)
;

alter table program
	add column opportunity_category varchar(50) after imported_opportunity_type_id,
	add index (opportunity_category, sequence_identifier)
;

alter table department
	add column opportunity_category varchar(50) after advert_id,
	add index (opportunity_category, sequence_identifier)
;

alter table institution
	add column opportunity_category varchar(50) after advert_id,
	add index (opportunity_category, sequence_identifier)
;

update department left join department_imported_program
	on department.id = department_imported_program.department_id
set department.advert_incomplete_section = replace(department.advert_incomplete_section,
	"SYSTEM_RESOURCE_SUMMARY_HEADER", "SYSTEM_RESOURCE_SUMMARY_HEADER|SYSTEM_RESOURCE_DEPARTMENT_PROGRAMS_HEADER")
where department_imported_program.department_id is null
	and department.advert_incomplete_section like "SYSTEM_RESOURCE_SUMMARY_HEADER%"
;

update department left join department_imported_program
	on department.id = department_imported_program.department_id
set department.advert_incomplete_section = concat("SYSTEM_RESOURCE_DEPARTMENT_PROGRAMS_HEADER|",
	department.advert_incomplete_section)
where department_imported_program.department_id is null
	and department.advert_incomplete_section not like "SYSTEM_RESOURCE_SUMMARY_HEADER%"
	and department.advert_incomplete_section is not null
;

update department left join department_imported_program
	on department.id = department_imported_program.department_id
set department.advert_incomplete_section = "SYSTEM_RESOURCE_DEPARTMENT_PROGRAMS_HEADER"
where department_imported_program.department_id is null
	and department.advert_incomplete_section is null
;

update program inner join imported_entity
	on program.imported_opportunity_type_id = imported_entity.id
set program.opportunity_category = "STUDY"
where imported_entity.name like "STUDY%"
;

update program inner join imported_entity
	on program.imported_opportunity_type_id = imported_entity.id
set program.opportunity_category = "FUNDING"
where imported_entity.name like "SCHOLARSHIP%"
;

update program inner join imported_entity
	on program.imported_opportunity_type_id = imported_entity.id
set program.opportunity_category = "EXPERIENCE"
where imported_entity.name in ("WORK_EXPERIENCE", "VOLUNTEERING")
;

update program inner join imported_entity
	on program.imported_opportunity_type_id = imported_entity.id
set program.opportunity_category = "WORK"
where imported_entity.name like "EMPLOYMENT%"
;

update program inner join imported_entity
	on program.imported_opportunity_type_id = imported_entity.id
set program.opportunity_category = "LEARNING"
where imported_entity.name = "TRAINING"
;

update project inner join imported_entity
	on project.imported_opportunity_type_id = imported_entity.id
set project.opportunity_category = "STUDY"
where imported_entity.name like "STUDY%"
;

update project inner join imported_entity
	on project.imported_opportunity_type_id = imported_entity.id
set project.opportunity_category = "FUNDING"
where imported_entity.name like "SCHOLARSHIP%"
;

update project inner join imported_entity
	on project.imported_opportunity_type_id = imported_entity.id
set project.opportunity_category = "EXPERIENCE"
where imported_entity.name in ("WORK_EXPERIENCE", "VOLUNTEERING")
;

update project inner join imported_entity
	on project.imported_opportunity_type_id = imported_entity.id
set project.opportunity_category = "WORK"
where imported_entity.name like "EMPLOYMENT%"
;

update project inner join imported_entity
	on project.imported_opportunity_type_id = imported_entity.id
set project.opportunity_category = "LEARNING"
where imported_entity.name = "TRAINING"
;

update application inner join project
	on application.project_id = project.id
set application.opportunity_category = project.opportunity_category
;

update application inner join program
	on application.program_id = program.id
set application.opportunity_category = program.opportunity_category
where application.opportunity_category is null
;

alter table department
	modify column opportunity_category varchar(255)
;

alter table institution
	modify column opportunity_category varchar(255)
;

create procedure set_resource_opportunity_category()
begin

	create temporary table resource_opportunity_category (
		resource_id int(10) unsigned,
		opportunity_category varchar (50),
		primary key (resource_id, opportunity_category))
	collate = utf8_general_ci
		engine = memory;
		
	set @department_id = (
		select id
		from department
		order by id
		limit 1);
	
	iteration: while @department_id is not null do
	
		insert ignore into resource_opportunity_category
			select @department_id, opportunity_category
			from project
			where department_id = @department_id
			group by opportunity_category;

		insert ignore into resource_opportunity_category
			select @department_id, opportunity_category
			from program
			where department_id = @department_id
			group by opportunity_category;
	
		set @department_id = (
			select id
			from department
			where id > @department_id
			order by id
			limit 1);
		
		if @department_id is null then
			leave iteration;
		end if;
	
	end while;
	
	update department inner join (
		select resource_id as department_id,
			group_concat(opportunity_category separator "|") as opportunity_category
		from resource_opportunity_category
		group by resource_id) as department_opportunity_category
		on department.id = department_opportunity_category.department_id
	set department.opportunity_category = department_opportunity_category.opportunity_category;
	
	delete from resource_opportunity_category;
	
	set @institution_id = (
		select id
		from institution
		order by id
		limit 1);
	
	iteration: while @institution_id is not null do
	
		insert ignore into resource_opportunity_category
			select @institution_id, opportunity_category
			from project
			where institution_id = @institution_id
			group by opportunity_category;

		insert ignore into resource_opportunity_category
			select @institution_id, opportunity_category
			from program
			where institution_id = @institution_id
			group by opportunity_category;
	
		set @institution_id = (
			select id
			from institution
			where id > @institution_id
			order by id
			limit 1);
		
		if @institution_id is null then
			leave iteration;
		end if;
	
	end while;
	
	update institution inner join (
		select resource_id as institution_id,
			group_concat(opportunity_category separator "|") as opportunity_category
		from resource_opportunity_category
		group by resource_id) as institution_opportunity_category
		on institution.id = institution_opportunity_category.institution_id
	set institution.opportunity_category = institution_opportunity_category.opportunity_category;
	
	drop table resource_opportunity_category;

end
;

call set_resource_opportunity_category()
;

drop procedure set_resource_opportunity_category
;

alter table project
	modify column opportunity_category varchar(50) not null
;

alter table program
	modify column opportunity_category varchar(50) not null
;
