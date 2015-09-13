alter table user_subject_area
	add unique index (user_id, imported_subject_area_id),
	drop primary key,
	add id int(10) unsigned not null auto_increment first,
	add primary key (id),
	add column relation_strength decimal(3,2) unsigned
;

rename table imported_entity to imported_entity_mapping
;

update imported_entity_mapping
set name = "Your application was not considered eligible. This will be because: (a) your application for study at your chosen institution was unsuccessful; (b) your fee status has been determined as Overseas, and/or; (c) Your research falls outside or our remit."
where name = "Your application was not considered eligible. This will be because: (a) your application for PhD study at your chosen institution was not successful; (b) your fee status has been determined as Overseas, and/or; (c) Your research falls outside of our funding remit."
;

create table imported_entity (
	id int(10) unsigned auto_increment not null,
	imported_entity_type varchar(50) not null,
	name varchar(255) not null,
	primary key (id),
	unique index (imported_entity_type, name))EN
collate = utf8_general_ci
engine = innodb
;

insert into imported_entity (imported_entity_type, name)
	select imported_entity_type, name
	from imported_entity_mapping
	group by imported_entity_type, name
;

alter table imported_entity_mapping
	drop foreign key imported_entity_mapping_ibfk_1,
	drop primary key,
	drop index institution_id,
	drop index imported_entity_type_id,
	drop index institution_id_3,
	add index (id),
	add column imported_entity_id int(10) unsigned after institution_id
;

update imported_entity_mapping inner join imported_entity
	on imported_entity_mapping.imported_entity_type = imported_entity.imported_entity_type
		and imported_entity_mapping.name = imported_entity.name
set imported_entity_mapping.imported_entity_id = imported_entity.id
;

alter table imported_entity_mapping
	modify column imported_entity_id int(10) unsigned not null,
	drop column imported_entity_type,
	drop column name
;

alter table imported_entity_mapping
	add primary key (id),
	add index (imported_entity_id),
	add foreign key (institution_id) references institution (id),
	add foreign key (imported_entity_id) references imported_entity (id)
;

rename table imported_age_range to imported_age_range_mapping
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
	from imported_age_range_mapping
	group by name
;

alter table imported_age_range_mapping
	drop primary key,
	drop index institution_id_2,
	drop column lower_bound,
	drop column upper_bound,
	add index (id),
	add column imported_age_range_id int(10) unsigned after institution_id
;

update imported_age_range_mapping inner join imported_age_range
	on imported_age_range_mapping.name = imported_age_range.name
set imported_age_range_mapping.imported_age_range_id = imported_age_range.id
;

alter table imported_age_range_mapping
	modify column imported_age_range_id int(10) unsigned not null,
	drop column name
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_11
;

alter table imported_age_range_mapping
	add primary key (id),
	add index (imported_age_range_id),
	add foreign key (institution_id) references institution (id),
	add foreign key (imported_age_range_id) references imported_age_range (id)
;

alter table comment_competence
	add column remark varchar(255)
;

rename table imported_language_qualification_type to imported_language_qualification_type_mapping
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
	from imported_language_qualification_type_mapping
	group by name
;

alter table imported_language_qualification_type_mapping
	drop foreign key imported_language_qualification_type_mapping_ibfk_1
;

alter table imported_language_qualification_type_mapping
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
	add column imported_language_qualification_type_id int(10) unsigned after institution_id
;

update imported_language_qualification_type_mapping inner join imported_language_qualification_type
	on imported_language_qualification_type_mapping.name = imported_language_qualification_type.name
set imported_language_qualification_type_mapping.imported_language_qualification_type_id = imported_language_qualification_type.id
;

alter table imported_language_qualification_type_mapping
	modify column imported_language_qualification_type_id int(10) unsigned not null,
	drop column name
;

alter table application_language_qualification
	drop foreign key application_language_qualification_ibfk_3
;

alter table imported_language_qualification_type_mapping
	add primary key (id),
	add index (imported_language_qualification_type_id),
	add foreign key (institution_id) references institution (id),
	add foreign key (imported_language_qualification_type_id) references imported_language_qualification_type (id)
;

rename table imported_institution to imported_institution_mapping
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
	select domicile.imported_entity_id, imported_institution_mapping.name,
		imported_institution_mapping.custom
	from imported_institution_mapping inner join imported_entity_mapping as domicile
		on imported_institution_mapping.domicile_id = domicile.id
	group by imported_institution_mapping.name, domicile.imported_entity_id
;

alter table imported_institution_mapping
	drop foreign key imported_institution_mapping_ibfk_1
;

alter table imported_institution_mapping
	drop primary key,
	drop index institution_id,
	drop index institution_id_3,
	drop column custom,
	add index (id),
	add column imported_institution_id int(10) unsigned after institution_id
;

delete application_qualification.*
from application_qualification inner join (
	select id as id
	from imported_institution_mapping inner join (
		select institution_id as institution_id,
			domicile_id as domicile_id,
			name as name,
			count(id) as duplicate_count
		from imported_institution_mapping
		group by institution_id, domicile_id, name
		having count(id) > 1) duplicate_institution
		on imported_institution_mapping.institution_id = duplicate_institution.institution_id
			and imported_institution_mapping.domicile_id = duplicate_institution.domicile_id
			and imported_institution_mapping.name = duplicate_institution.name
			where id not in (
					select min(id)
					from imported_institution_mapping inner join (
						select institution_id as institution_id,
							domicile_id as domicile_id,
							name as name,
							count(id) as duplicate_count
						from imported_institution_mapping
						group by institution_id, domicile_id, name
						having count(id) > 1) duplicate_institution
						on imported_institution_mapping.institution_id = duplicate_institution.institution_id
							and imported_institution_mapping.domicile_id = duplicate_institution.domicile_id
							and imported_institution_mapping.name = duplicate_institution.name
					group by imported_institution_mapping.name)) as unmapped_institution
	on application_qualification.institution_id = unmapped_institution.id
;

delete imported_institution_mapping.*
from imported_institution_mapping inner join (
	select id as id
	from imported_institution_mapping inner join (
		select institution_id as institution_id,
			domicile_id as domicile_id,
			name as name,
			count(id) as duplicate_count
		from imported_institution_mapping
		group by institution_id, domicile_id, name
		having count(id) > 1) duplicate_institution
		on imported_institution_mapping.institution_id = duplicate_institution.institution_id
			and imported_institution_mapping.domicile_id = duplicate_institution.domicile_id
			and imported_institution_mapping.name = duplicate_institution.name
			where id not in (
					select min(id)
					from imported_institution_mapping inner join (
						select institution_id as institution_id,
							domicile_id as domicile_id,
							name as name,
							count(id) as duplicate_count
						from imported_institution_mapping
						group by institution_id, domicile_id, name
						having count(id) > 1) duplicate_institution
						on imported_institution_mapping.institution_id = duplicate_institution.institution_id
							and imported_institution_mapping.domicile_id = duplicate_institution.domicile_id
							and imported_institution_mapping.name = duplicate_institution.name
					group by imported_institution_mapping.name)) as unmapped_institution
	on imported_institution_mapping.id = unmapped_institution.id
;
