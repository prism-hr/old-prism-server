alter table advert_competence
	drop importance
;

alter table advert_institution
	drop column importance
;

alter table advert_subject_area
	drop column importance
;

alter table advert_program
	drop column importance
;

alter table advert_department
	drop column importance
;

alter table advert
	change column title name varchar(255) not null
;

alter table project
	change column title name varchar(255) not null
;

alter table program
	change column title name varchar(255) not null
;

alter table department
	change column title name varchar(255) not null
;

alter table institution
	change column title name varchar(255) not null
;

alter table system
	change column title name varchar(255) not null
;

alter table competence
	change column title name varchar(255) not null
;
