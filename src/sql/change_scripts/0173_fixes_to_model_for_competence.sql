alter table competence
	add column adopted_count int(10) unsigned not null,
	add column created_timestamp datetime not null,
	add column updated_timestamp datetime not null
;

alter table advert_competence
	add column description text
;
