UPDATE INTERVIEW_COMMENT set decline = IF(old_decline = 'YES', 1, 0), willing_to_supervise = IF(old_willing_to_supervise='YES', 1,0), suitable_candidate= IF(old_suitable_candidate = 'YES', 1, 0), admins_notified=IF(old_admins_notified = 'YES', 1, 0);