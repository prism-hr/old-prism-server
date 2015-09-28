alter table advert_target_advert
	drop column selected
;

alter table advert_target_advert
	drop foreign key advert_target_advert_ibfk_5,
	drop index target_advert_user_id,
	drop column target_advert_user_id
;

alter table address
	modify column imported_domicile_id varchar(10),
	modify column address_line_1 varchar(255),
	modify column address_town varchar(255)
;
