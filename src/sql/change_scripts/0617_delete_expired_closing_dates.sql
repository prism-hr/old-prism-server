CREATE PROCEDURE SP_DELETE_EXPIRED_CLOSING_DATES()
BEGIN

	DECLARE baseline_date DATE;
	
	SET baseline_date = (SELECT CURRENT_DATE()); 

	DELETE FROM PROGRAM_CLOSING_DATES
	WHERE closing_date < baseline_date;
	
	UPDATE PROJECT
	SET closing_date = NULL
	WHERE closing_date < baseline_date;

END
;
