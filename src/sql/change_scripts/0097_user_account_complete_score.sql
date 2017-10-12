alter table user_account
	add column complete_score int(3) unsigned after shared,
	add index (complete_score)
;

update user_account
set complete_score = 0
;

alter table user_account
	modify complete_score int(3) unsigned not null
;
