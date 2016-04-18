update advert_target
set accepted_timestamp = from_unixtime(substr(sequence_identifier, 1, 10), "%Y-%m-%d %H:%i:%S")
where sequence_identifier is not null
;
