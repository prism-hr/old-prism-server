UPDATE ADVERT 
SET description = REPLACE(description, "\n", ""),
	funding = REPLACE(funding, "\n", "")
WHERE description REGEXP "\n"
	OR funding REGEXP "\n"
;
