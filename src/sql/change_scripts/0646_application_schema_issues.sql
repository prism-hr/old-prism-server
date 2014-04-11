ALTER TABLE APPLICATION
	ADD COLUMN new_submitted_ip_address VARCHAR(32)
;

UPDATE APPLICATION
SET new_submitted_ip_address = INET_NTOA(CONV(HEX(submitted_ip_address),16,10))
;

ALTER TABLE APPLICATION
	DROP COLUMN submitted_ip_address,
	CHANGE COLUMN new_submitted_ip_address submitted_ip_address VARCHAR(32)
;
