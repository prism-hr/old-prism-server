alter table institution
	add column imported_institution_id int(10) unsigned unique after advert_id,
	add index (imported_institution_id),
	add foreign key (imported_institution_id) references imported_institution (id)
;

alter table advert
	modify column institution_id int(10) unsigned
;
