create table hefce_jacs (
	id int(10) unsigned not null auto_increment,
	code varchar(10) not null,
	description varchar(255) not null,
	enabled int(1) unsigned not null,
	parent_id int(10) unsigned,
	primary key (id),
	unique index (code),
	unique index (description),
	index (enabled),
	index (parent_id),
	foreign key (parent_id) references hefce_jacs (id))
collate = utf8_general_ci,
engine = innodb
;

rename table hefce_jacs to imported_subject_area
;

create table imported_institution_subject_area (
	imported_institution_id int(10) unsigned not null,
	imported_subject_area_id int(10) unsigned not null,
	primary key (imported_institution_id, imported_subject_area_id),
	index (imported_subject_area_id),
	foreign key (imported_institution_id) references imported_institution (id),
	foreign key (imported_subject_area_id) references imported_subject_area (id))
collate = utf8_general_ci,
engine = innodb
;

create table imported_program (
	id int(10) unsigned not null auto_increment,
	imported_institution_id int(10) unsigned not null,
	qualification varchar(50) not null,
	title varchar(255) not null,
	homepage text,
	enabled int(1) unsigned not null,
	primary key (id),
	unique index (imported_institution_id, qualification, title),
	index (imported_institution_id, qualification, title, enabled),
	foreign key (imported_institution_id) references imported_institution(id))
collate = utf8_general_ci,
engine = innodb
;

create table imported_program_subject_area (
	imported_program_id int(10) unsigned not null,
	imported_subject_area_id int(10) unsigned not null,
	primary key (imported_program_id, imported_subject_area_id),
	index (imported_subject_area_id),
	foreign key (imported_program_id) references imported_program (id),
	foreign key (imported_subject_area_id) references imported_subject_area (id))
collate = utf8_general_ci,
engine = innodb
;

create table user_subject_area (
	user_id int(10) unsigned not null,
	imported_subject_area_id int(10) unsigned not null,
	primary key (user_id, imported_subject_area_id),
	index (imported_subject_area_id),
	foreign key (user_id) references user (id),
	foreign key (imported_subject_area_id) references imported_subject_area (id))
collate = utf8_general_ci,
engine = innodb
;

create table user_program (
	user_id int(10) unsigned not null,
	imported_program_id int(10) unsigned not null,
	primary key (user_id, imported_program_id),
	index (imported_program_id),
	foreign key (user_id) references user (id),
	foreign key (imported_program_id) references imported_program (id))
collate = utf8_general_ci,
engine = innodb
;

create table competence (
	id int(10) unsigned not null auto_increment,
	title varchar(255) not null,
	description text,
	primary key (id),
	unique index (title))
collate = utf8_general_ci,
engine = innodb
;

create table advert_competence (
	advert_id int(10) unsigned not null,
	competence_id int(10) unsigned not null,
	primary key (advert_id, competence_id),
	index (competence_id),
	foreign key (advert_id) references advert (id),
	foreign key (competence_id) references competence (id))
collate = utf8_general_ci,
engine = innodb
;

create table advert_subject_area (
	advert_id int(10) unsigned not null,
	imported_subject_area_id int(10) unsigned not null,
	primary key (advert_id, imported_subject_area_id),
	index (imported_subject_area_id),
	foreign key (advert_id) references advert (id),
	foreign key (imported_subject_area_id) references imported_subject_area (id))
collate = utf8_general_ci,
engine = innodb
;

create table comment_competence (
	id int(10) unsigned not null auto_increment,
	comment_id int(10) unsigned not null,
	competence_id int(10) unsigned not null,
	rating decimal (3,2) unsigned not null,
	primary key (id),
	unique index (comment_id, competence_id),
	index (competence_id),
	foreign key (comment_id) references comment (id),
	foreign key (competence_id) references competence (id))
collate = utf8_general_ci,
engine = innodb
;

create table user_competence (
	id int(10) unsigned not null auto_increment,
	user_id int(10) unsigned not null,
	competence_id int(10) unsigned not null,
	rating_count int(10) unsigned not null,
	rating_average decimal (3,2) unsigned not null,
	primary key (id),
	unique index (user_id, competence_id),
	index (competence_id),
	foreign key (user_id) references user (id),
	foreign key (competence_id) references competence (id))
collate = utf8_general_ci,
engine = innodb
;
