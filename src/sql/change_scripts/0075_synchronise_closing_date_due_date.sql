update project inner join advert
	on project.advert_id = advert.id
set project.due_date = advert.closing_date
;

update program inner join advert
	on program.advert_id = advert.id
set program.due_date = advert.closing_date
;
