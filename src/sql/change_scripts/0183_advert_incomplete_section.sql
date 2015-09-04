alter table institution
	add column advert_incomplete_section text after advert_id
;

alter table department
	add column advert_incomplete_section text after advert_id
;

alter table program
	add column advert_incomplete_section text after advert_id
;

alter table project
	add column advert_incomplete_section text after advert_id
;
