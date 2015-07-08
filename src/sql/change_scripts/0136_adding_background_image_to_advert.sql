alter table advert
	add column background_image_id int(10) unsigned after description,
	add foreign key (background_image_id) references document (id)
;
