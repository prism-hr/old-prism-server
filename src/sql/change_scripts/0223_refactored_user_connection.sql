drop table user_connection
;

alter table advert_target_advert
	add column advert_user_id int(10) unsigned after advert_id,
	add column target_advert_user_id int(10) unsigned after target_advert_id,
	add index (advert_user_id),
	add index (target_advert_user_id),
	add foreign key (advert_user_id) references user (id),
	add foreign key (target_advert_user_id) references user (id)
;

update advert inner join advert_target_advert
	on advert.id = advert_target_advert.advert_id
set advert_target_advert.advert_user_id = advert.user_id
;

alter table advert_target_advert
	modify column advert_user_id int(10) unsigned not null
;

alter table project
	add column staff_email_list text after name,
	add column staff_email_list_group int(1) unsigned after staff_email_list,
	add column student_email_list text after staff_email_list_group,
	add column student_email_list_group int(1) unsigned after student_email_list
;

alter table program
	add column staff_email_list text after name,
	add column staff_email_list_group int(1) unsigned after staff_email_list,
	add column student_email_list text after staff_email_list_group,
	add column student_email_list_group int(1) unsigned after student_email_list
;

alter table department
	add column staff_email_list text after name,
	add column staff_email_list_group int(1) unsigned after staff_email_list,
	add column student_email_list text after staff_email_list_group,
	add column student_email_list_group int(1) unsigned after student_email_list
;

alter table institution
	add column staff_email_list text after name,
	add column staff_email_list_group int(1) unsigned after staff_email_list,
	add column student_email_list text after staff_email_list_group,
	add column student_email_list_group int(1) unsigned after student_email_list
;
