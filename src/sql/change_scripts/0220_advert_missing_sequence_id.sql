update institution inner join advert
	on institution.advert_id = advert.id
set advert.sequence_identifier = concat(substr(institution.sequence_identifier, 1, 13), lpad(advert.id, 10, "0"))
where advert.sequence_identifier is null
;

update institution inner join advert
	on institution.advert_id = advert.id
set advert.sequence_identifier = concat(substr(institution.sequence_identifier, 1, 13), lpad(advert.id, 10, "0"))
where length(advert.sequence_identifier) < 23
;

update department inner join advert
	on department.advert_id = advert.id
set advert.sequence_identifier = concat(substr(department.sequence_identifier, 1, 13), lpad(advert.id, 10, "0"))
where advert.sequence_identifier is null
;
