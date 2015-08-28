alter table advert_target_advert
	drop index advert_id_2,
	drop index advert_id_4,
	drop column rating_count,
	drop column rating_average
;

alter table project
	add column opportunity_rating_count int(10) unsigned after application_rating_average,
	add column opportunity_rating_average decimal(3,2) unsigned after opportunity_rating_count,
	add index (opportunity_rating_count, sequence_identifier),
	add index (opportunity_rating_average, sequence_identifier)
;

alter table program
	add column opportunity_rating_count int(10) unsigned after application_rating_average,
	add column opportunity_rating_average decimal(3,2) unsigned after opportunity_rating_count,
	add index (opportunity_rating_count, sequence_identifier),
	add index (opportunity_rating_average, sequence_identifier)
;

alter table department
	add column opportunity_rating_count int(10) unsigned after application_rating_average,
	add column opportunity_rating_average decimal(3,2) unsigned after opportunity_rating_count,
	add index (opportunity_rating_count, sequence_identifier),
	add index (opportunity_rating_average, sequence_identifier)
;

alter table institution
	add column opportunity_rating_count int(10) unsigned after application_rating_average,
	add column opportunity_rating_average decimal(3,2) unsigned after opportunity_rating_count,
	add index (opportunity_rating_count, sequence_identifier),
	add index (opportunity_rating_average, sequence_identifier)
;

alter table comment
	modify column rating decimal(3,2) unsigned after transition_state_id
;
