ALTER TABLE SYSTEM
	ADD COLUMN helpdesk VARCHAR(2000) NOT NULL DEFAULT "http://alumenilimited.freshdesk.com/" AFTER locale
;

ALTER TABLE SYSTEM
	MODIFY COLUMN helpdesk VARCHAR(2000) NOT NULL
;

ALTER TABLE INSTITUTION
	ADD COLUMN helpdesk VARCHAR(2000) NOT NULL DEFAULT "http://uclprism.freshdesk.com/" AFTER homepage
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN helpdesk VARCHAR(2000) NOT NULL
;
