ALTER TABLE imported_institution
	ADD COLUMN hesa_id INT(10) UNSIGNED AFTER facebook_id
;

ALTER TABLE imported_institution_subject_area
	ADD COLUMN tariff_bands_1_79 INT(10),
	ADD COLUMN tariff_bands_80_119 INT(10),
	ADD COLUMN tariff_bands_120_179 INT(10),
	ADD COLUMN tariff_bands_180_239 INT(10),
	ADD COLUMN tariff_bands_240_299 INT(10),
	ADD COLUMN tariff_bands_300_359 INT(10),
	ADD COLUMN tariff_bands_360_419 INT(10),
	ADD COLUMN tariff_bands_420_479 INT(10),
	ADD COLUMN tariff_bands_480_539 INT(10),
	ADD COLUMN tariff_bands_540_over INT(10),
	ADD COLUMN tariff_bands_unknown INT(10),
	ADD COLUMN tariff_bands_not_applicable INT(10),
	ADD COLUMN honours_first_class INT(10),
	ADD COLUMN honours_upper_second_class INT(10),
	ADD COLUMN honours_lower_second_class INT(10),
	ADD COLUMN honours_third_class INT(10),
	ADD COLUMN honours_unclassified INT(10),
	ADD COLUMN honours_classification_not_applicable INT(10),
	ADD COLUMN honours_not_applicable INT(10),
	ADD COLUMN study_full_time INT(10),
	ADD COLUMN study_part_time INT(10),
	ADD COLUMN course_count INT(10),
	ADD COLUMN fpe INT(10)
;
