alter table advert_target_advert
	drop index advert_id_4,
	drop column endorsed,
	add column rating int(1) unsigned,
	add index (advert_id, rating)
;

alter table comment
	change column application_rating rating  decimal(3,2) unsigned
;

alter table advert_target_advert
	modify column rating decimal(3,2) unsigned
;

delete
from state_transition
where state_action_id in (
	select id
	from state_action
	where action_id in (
		select id
		from action
		where id like "%UNENDORSE"))
;

delete
from state_action_notification
where state_action_id in (
	select id
	from state_action
	where action_id in (
		select id
		from action
		where id like "%UNENDORSE"))
;

delete
from state_action_assignment
where state_action_id in (
	select id
	from state_action
	where action_id in (
		select id
		from action
		where id like "%UNENDORSE"))
;

delete
from state_action
where action_id in (
	select id
	from action
	where id like "%UNENDORSE")
;

delete
from action
where id like "%UNENDORSE"
;

update advert_target_advert
set rating = null
;
