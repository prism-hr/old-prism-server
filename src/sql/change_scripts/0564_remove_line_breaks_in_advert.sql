UPDATE ADVERT 
SET description = REPLACE(description, "\n", "<p></p>"),
	funding = REPLACE(funding, "\n", "<p></p>")
WHERE description REGEXP "\n"
	OR funding REGEXP "\n"
;

UPDATE ADVERT 
SET description = REPLACE(description, "<p><p></p></p>", "<p></p>"),
	funding = REPLACE(funding, "<p><p></p></p>", "<p></p>")
WHERE description REGEXP "<p><p></p></p>"
	OR funding REGEXP "<p><p></p></p>"
;
