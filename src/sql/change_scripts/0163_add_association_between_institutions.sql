alter table institution
	add column imported_institution_id int(10) unsigned unique after name,
	add foreign key (imported_institution_id) references imported_institution (id)
;
