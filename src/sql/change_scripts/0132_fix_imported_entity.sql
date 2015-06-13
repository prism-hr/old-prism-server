alter table user_subject_area
	add unique index (user_id, imported_subject_area_id),
	drop primary key,
	add id int(10) unsigned not null auto_increment first,
	add primary key (id),
	add column relation_strength decimal(3,2) unsigned
;

rename table imported_entity to imported_entity_implementation
;

create table imported_entity (
	id int(10) unsigned auto_increment not null,
	imported_entity_type varchar(50) not null,
	name varchar(255) not null,
	primary key (id),
	unique index (imported_entity_type, name))
collate = utf8_general_ci
engine = innodb
;

insert into imported_entity (imported_entity_type, name)
	select imported_entity_type, name
	from imported_entity_implementation
	group by imported_entity_type, name
;

alter table imported_entity_implementation
	drop foreign key imported_entity_implementation_ibfk_1,
	drop primary key,
	drop index institution_id,
	drop index imported_entity_type_id,
	drop index institution_id_3,
	add index (id),
	add column imported_entity_id int(10) unsigned after institution_id,
	add unique index (institution_id, imported_entity_id)
;

update imported_entity_implementation inner join imported_entity
	on imported_entity_implementation.imported_entity_type = imported_entity.imported_entity_type
		and imported_entity_implementation.name = imported_entity.name
set imported_entity_implementation.imported_entity_id = imported_entity.id
;

alter table imported_entity_implementation
	modify column imported_entity_id int(10) unsigned not null,
	drop column imported_entity_type,
	drop column name
;

alter table address
	drop foreign key address_ibfk_1
;

alter table application_funding
	drop foreign key application_funding_ibfk_2
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_10
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_3
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_4
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_5
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_6
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_7
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_8
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_9
;

alter table application_program_detail
	drop foreign key application_program_detail_ibfk_2
;

alter table application_program_detail
	drop foreign key application_program_detail_ibfk_3
;

alter table application_program_detail
	drop foreign key application_program_detail_ibfk_4
;

alter table application_qualification
	drop foreign key application_qualification_ibfk_4
;

alter table comment
	drop foreign key comment_ibfk_14
;

alter table imported_institution
	drop foreign key imported_institution_ibfk_2
;

alter table program
	drop foreign key program_ibfk_10
;

alter table project
	drop foreign key project_ibfk_9
;

alter table resource_study_option
	drop foreign key resource_study_option_ibfk_2
;

alter table imported_entity_implementation
	partition by key (institution_id)
	partitions 1000
;

rename table imported_age_range to imported_age_range_implementation
;

create table imported_age_range (
	id int(10) unsigned auto_increment not null,
	name varchar(255) not null,
	lower_bound int(3) unsigned not null,
	upper_bound int(3) unsigned,
	primary key (id),
	unique index (name),
	unique index (lower_bound),
	unique index (upper_bound))
collate = utf8_general_ci
engine = innodb
;

insert into imported_age_range (name, lower_bound, upper_bound)
	select name, lower_bound, upper_bound
	from imported_age_range_implementation
	group by name
;

alter table imported_age_range_implementation
	drop primary key,
	drop index institution_id,
	drop index institution_id_2,
	drop column lower_bound,
	drop column upper_bound,
	add index (id),
	add column imported_age_range_id int(10) unsigned after institution_id,
	add unique index (institution_id, imported_age_range_id)
;

update imported_age_range_implementation inner join imported_age_range
	on imported_age_range_implementation.name = imported_age_range.name
set imported_age_range_implementation.imported_age_range_id = imported_age_range.id
;

alter table imported_age_range_implementation
	modify column imported_age_range_id int(10) unsigned not null,
	drop column name
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_11
;

alter table imported_age_range_implementation
	partition by key (institution_id)
	partitions 1000
;

alter table comment_competence
	add column remark varchar(255)
;

rename table imported_language_qualification_type to imported_language_qualification_type_implementation
;

create table imported_language_qualification_type (
	id int(10) unsigned auto_increment not null,
	name varchar(255) not null,
	minimum_overall_score decimal(5,2) unsigned,
	maximum_overall_score decimal(5,2) unsigned,
	minimum_reading_score decimal(5,2) unsigned,
	maximum_reading_score decimal(5,2) unsigned,
	minimum_writing_score decimal(5,2) unsigned,
	maximum_writing_score decimal(5,2) unsigned,
	minimum_speaking_score decimal(5,2) unsigned,
	maximum_speaking_score decimal(5,2) unsigned,
	minimum_listening_score decimal(5,2) unsigned,
	maximum_listening_score decimal(5,2) unsigned,
	primary key (id),
	unique index (name))
collate = utf8_general_ci
engine = innodb
;

insert into imported_language_qualification_type (name, minimum_overall_score,
	maximum_overall_score, minimum_reading_score, maximum_reading_score, 
	minimum_writing_score, maximum_writing_score, minimum_speaking_score,
	maximum_speaking_score, minimum_listening_score, maximum_listening_score)
	select name, minimum_overall_score, maximum_overall_score, minimum_reading_score, 
		maximum_reading_score, minimum_writing_score, maximum_writing_score, 
		minimum_speaking_score, maximum_speaking_score, minimum_listening_score, 
		maximum_listening_score
	from imported_language_qualification_type_implementation
	group by name
;

alter table imported_language_qualification_type_implementation
	drop foreign key imported_language_qualification_type_implementation_ibfk_1
;

alter table imported_language_qualification_type_implementation
	drop primary key,
	drop index institution_id,
	drop index institution_id_3,
	drop column minimum_overall_score, 
	drop column maximum_overall_score, 
	drop column minimum_reading_score, 
	drop column maximum_reading_score, 
	drop column minimum_writing_score, 
	drop column maximum_writing_score, 
	drop column minimum_speaking_score, 
	drop column maximum_speaking_score, 
	drop column minimum_listening_score, 
	drop column maximum_listening_score,
	add index (id),
	add column imported_language_qualification_type_id int(10) unsigned after institution_id,
	add unique index (institution_id, imported_language_qualification_type_id)
;

update imported_language_qualification_type_implementation inner join imported_language_qualification_type
	on imported_language_qualification_type_implementation.name = imported_language_qualification_type.name
set imported_language_qualification_type_implementation.imported_language_qualification_type_id = imported_language_qualification_type.id
;

alter table imported_language_qualification_type_implementation
	modify column imported_language_qualification_type_id int(10) unsigned not null,
	drop column name
;

alter table application_language_qualification
	drop foreign key application_language_qualification_ibfk_3
;

alter table imported_language_qualification_type_implementation
	partition by key (institution_id)
	partitions 1000
;

rename table imported_institution to imported_institution_implementation
;

create table imported_institution (
	id int(10) unsigned auto_increment not null,
	domicile_id int(10) unsigned not null,
	name varchar(255) not null,
	custom int(1) unsigned not null,
	primary key (id),
	unique index (domicile_id, name))
collate = utf8_general_ci
engine = innodb
;

insert into imported_institution (domicile_id, name, custom)
	select domicile.imported_entity_id, imported_institution_implementation.name, 
		imported_institution_implementation.custom
	from imported_institution_implementation inner join imported_entity_implementation as domicile
		on imported_institution_implementation.domicile_id = domicile.id
	group by imported_institution_implementation.name, domicile.imported_entity_id
;

alter table imported_institution_implementation
	drop foreign key imported_institution_implementation_ibfk_1
;

alter table imported_institution_implementation
	drop primary key,
	drop index institution_id,
	drop index institution_id_3,
	drop column custom,
	add index (id),
	add column imported_institution_id int(10) unsigned after institution_id,
	add unique index (institution_id, imported_institution_id)
;

create procedure clean_imported_institution()
begin

	create temporary table duplicates (
		id int(10) unsigned not null,
		primary key (id))
	collate = utf8_general_ci
	engine = memory;
	
	insert into duplicates (id)
		select id
		from imported_institution_implementation inner join (
			select institution_id as institution_id,
				domicile_id as domicile_id, 
				name as name, 
				count(id) as duplicate_count
			from imported_institution_implementation
			group by institution_id, domicile_id, name
			having count(id) > 1) duplicate_institution
			on imported_institution_implementation.institution_id = duplicate_institution.institution_id
				and imported_institution_implementation.domicile_id = duplicate_institution.domicile_id
				and imported_institution_implementation.name = duplicate_institution.name;
		
	delete 
	from application_qualification
	where institution_id in (
		select id
		from duplicates);
		
	delete
	from imported_institution_implementation
	where id in (
		select id
		from duplicates);
	
	drop table duplicates;

end
;

call clean_imported_institution()
;

drop procedure clean_imported_institution
;

update imported_institution_implementation inner join imported_entity_implementation as domicile
	on imported_institution_implementation.domicile_id = domicile.id
inner join imported_institution
	on imported_institution_implementation.name = imported_institution.name
	and domicile.imported_entity_id = imported_institution.domicile_id
set imported_institution_implementation.imported_institution_id = imported_institution.id
;

alter table imported_institution_implementation
	modify column imported_institution_id int(10) unsigned not null,
	drop column name,
	drop column domicile_id
;

alter table application_qualification
	drop foreign key application_qualification_ibfk_5
;

alter table imported_program
	drop foreign key imported_program_ibfk_1
;

alter table imported_institution_subject_area
	drop foreign key imported_institution_subject_area_ibfk_1
;

alter table imported_institution_implementation
	partition by key (institution_id)
	partitions 1000
;
