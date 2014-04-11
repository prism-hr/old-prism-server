UPDATE EMAIL_TEMPLATE SET subject = '%3$s %4$s Application %1$s for UCL %2$s - Reference Request' WHERE name = 'REFEREE_NOTIFICATION'
;

UPDATE EMAIL_TEMPLATE SET subject = 'REMINDER: %3$s %4$s Application %1$s for UCL %2$s - Reference Request' WHERE name = 'REFEREE_REMINDER'
;
