alter table imported_program
	add column code varchar(50) after name,
	add index (code)
;
