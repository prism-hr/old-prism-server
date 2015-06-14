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

alter table imported_institution_implementation
	partition by key (institution_id)
	partitions 1000
;

alter table imported_institution_implementation
	modify column institution_id int(10) unsigned not null
;

alter table imported_program
	change column title name varchar(255) not null,
	drop column enabled
;

alter table imported_program
	drop index imported_institution_id_2,
	add foreign key (imported_institution_id) references imported_institution (id)
;

create table imported_program_implementation (
	id int(10) unsigned not null auto_increment,
	institution_id int(10) unsigned not null,
	imported_program_id int(10) unsigned not null,
	enabled int(1) unsigned not null,
	index (id),
	unique index (institution_id, imported_program_id))
collate = utf8_general_ci
engine = innodb
partition by key (institution_id)
partitions 1000
;

alter table imported_age_range_implementation
	add unique index (institution_id, code),
	add index (institution_id, enabled)
;

alter table imported_entity_implementation
	add column imported_entity_type varchar(50) after institution_id,
	add unique index (institution_id, imported_entity_type, code),
	add index (institution_id, enabled)
;

update imported_entity_implementation inner join imported_entity
	on imported_entity_implementation.imported_entity_id = imported_entity.id
set imported_entity_implementation.imported_entity_type = imported_entity.imported_entity_type
;

alter table imported_entity_implementation
	modify column imported_entity_type varchar(50) not null
;

alter table imported_language_qualification_type_implementation
	add unique index (institution_id, code),
	add index (institution_id, enabled)
;

alter table imported_institution_implementation
	add unique index (institution_id, code),
	add index (institution_id, enabled)
;

alter table imported_program_implementation
	add column code varchar(50) after imported_program_id,
	add unique index (institution_id, code),
	add index (institution_id, enabled)
;

alter table imported_institution
	change column domicile_id imported_domicile_id int(10) unsigned not null,
	add index (imported_domicile_id, custom)
;

alter table imported_program
	add column custom int(1) unsigned not null,
	add index (imported_institution_id, custom)
;


alter table imported_institution_implementation
	modify column code varchar(50)
;

update imported_institution_implementation
set code = null
where custom is true
;

alter table imported_institution_implementation
	modify column code varchar(50)
;

update imported_institution_implementation inner join imported_institution
	on imported_institution_implementation.imported_institution_id = imported_institution.id
set imported_institution_implementation.code = null
where imported_institution.custom is true
;

alter table imported_subject_area
	drop column enabled
;

create table imported_subject_area_implementation (
	id int(10) unsigned not null auto_increment,
	institution_id int(10) unsigned not null,
	imported_subject_area_id int(10) unsigned not null,
	code varchar(50),
	enabled int(1) unsigned not null,
	index (id),
	unique index (institution_id, imported_subject_area_id),
	unique index (institution_id, code))
collate = utf8_general_ci
engine = innodb
partition by key (institution_id)
partitions 1000
;

create table user_institution (
	user_id int(10) unsigned not null,
	imported_institution_id int(10) unsigned not null,
	primary key (user_id, imported_institution_id),
	index (imported_institution_id),
	foreign key (user_id) references user (id),
	foreign key (imported_institution_id) references imported_institution (id))
collate = utf8_general_ci
engine = innodb
;

alter table user_program
	add unique index (user_id, imported_program_id),
	drop primary key,
	add column id int(10) unsigned not null auto_increment first,
	add column relation_strength int(10) unsigned not null,
	add primary key (id)
;

alter table user_institution
	add unique index (user_id, imported_institution_id),
	drop primary key,
	add column id int(10) unsigned not null auto_increment first,
	add column relation_strength int(10) unsigned not null,
	add primary key (id)
;

alter table user_subject_area
	modify column relation_strength int(10) unsigned not null
;

alter table imported_institution_subject_area
	modify column relation_strength int(10) unsigned not null
;

alter table imported_program_subject_area
	modify column relation_strength int(10) unsigned not null
;

alter table application_qualification
	modify column subject varchar(200) not null after title
;

insert into user_institution (user_id, imported_institution_id, relation_strength)
	select application.user_id, imported_institution_implementation.imported_institution_id,
		count(distinct application_qualification.subject)
	from application inner join application_qualification
		on application.id = application_qualification.application_id
	inner join imported_institution_implementation
		on application_qualification.institution_id = imported_institution_implementation.id
	group by application.user_id, imported_institution_implementation.imported_institution_id
;

alter table application_qualification
	modify column institution_id int(10) unsigned not null after application_id,
	modify column qualification_type_id int(10) unsigned not null after institution_id
;

alter table imported_program
	modify column qualification varchar(50)
;

insert into imported_program(imported_institution_id, qualification, name, custom)
	select imported_institution_implementation.imported_institution_id, application_qualification.title, 
		application_qualification.subject, 1
	from application_qualification inner join imported_institution_implementation
		on application_qualification.institution_id = imported_institution_implementation.id
	group by imported_institution_implementation.imported_institution_id, application_qualification.title,
		application_qualification.subject
;

alter table application_qualification
	add column program_id int(10) unsigned after application_id,
	add index (program_id)
;

insert into imported_program_implementation(institution_id, imported_program_id, enabled)
	select institution.id, imported_program.id, 1
	from institution inner join imported_program
;

update application inner join application_qualification
	on application.id = application_qualification.application_id
inner join imported_institution_implementation
	on application_qualification.institution_id = imported_institution_implementation.id
inner join imported_program
	on imported_institution_implementation.imported_institution_id = imported_program.imported_institution_id
	and application_qualification.title = imported_program.qualification
	and application_qualification.subject = imported_program.name
inner join imported_program_implementation
	on imported_program.id = imported_program_implementation.imported_program_id
	and application.institution_id = imported_program_implementation.institution_id
set application_qualification.program_id = imported_program_implementation.id
;

alter table application_qualification
	drop column institution_id,
	drop column title,
	drop column subject
;

alter table imported_program
	add column imported_qualification_type_id int(10) unsigned after imported_institution_id,
	add index (imported_qualification_type_id),
	add foreign key (imported_qualification_type_id) references imported_entity (id)
;

update imported_program inner join imported_program_implementation
	on imported_program.id = imported_program_implementation.imported_program_id
inner join application_qualification
	on imported_program_implementation.id = application_qualification.program_id
inner join imported_entity_implementation
	on application_qualification.qualification_type_id = imported_entity_implementation.id
set imported_program.imported_qualification_type_id = imported_entity_implementation.imported_entity_id
;

alter table application_qualification
	drop column qualification_type_id
;

insert into user_program (user_id, imported_program_id, relation_strength)
	select application.user_id, imported_program_implementation.imported_program_id, 1
	from application inner join application_qualification
		on application.id = application_qualification.application_id
	inner join imported_program_implementation
		on application_qualification.program_id = imported_program_implementation.id
	group by application.user_id, imported_program_implementation.imported_program_id
;
