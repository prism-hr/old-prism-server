alter table project
	add column background_image_id int(10) unsigned after title,
	add index (background_image_id),
	add foreign key (background_image_id) references document (id)
;

alter table program
	add column background_image_id int(10) unsigned after title,
	add index (background_image_id),
	add foreign key (background_image_id) references document (id)
;
