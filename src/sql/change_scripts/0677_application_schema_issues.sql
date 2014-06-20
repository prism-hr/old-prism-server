/* Mistake in workflow configuration */

UPDATE STATE_DURATION
SET day_duration = 28
WHERE day_duration = 25
;
