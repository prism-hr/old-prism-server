alter table user_advert
	add column identified int(1) unsigned not null,
	add index (user_id, identified)
;

alter table comment
	add column application_identified int(1) unsigned after rating
;

update state_group
set ordinal = ordinal + 20
;
