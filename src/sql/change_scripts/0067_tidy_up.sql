alter table user_feedback
    change column feature_requests feature_request TEXT after content
;
