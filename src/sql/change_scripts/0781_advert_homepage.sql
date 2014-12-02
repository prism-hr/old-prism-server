ALTER TABLE ADVERT
	ADD COLUMN homepage VARCHAR(2000) AFTER description,
	CHANGE COLUMN apply_link apply_homepage VARCHAR(2000)
;
