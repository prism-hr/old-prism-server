DELETE FROM comment_state
WHERE comment_id IN (
  SELECT id FROM comment
  WHERE action_id LIKE "%_ENDORSE"
)
;

DELETE FROM comment_transition_state
WHERE comment_id IN (
  SELECT id FROM comment
  WHERE action_id LIKE "%_ENDORSE"
)
;

DELETE FROM comment
WHERE action_id LIKE "%_ENDORSE"
;

DELETE
FROM state_transition
WHERE state_action_id IN (
  SELECT id
  FROM state_action
  WHERE action_id IN (
    SELECT id
    FROM action
    WHERE id LIKE "%_ENDORSE"))
;

DELETE
FROM state_action_assignment
WHERE state_action_id IN (
  SELECT id
  FROM state_action
  WHERE action_id IN (
    SELECT id
    FROM action
    WHERE id LIKE "%_ENDORSE"))
;

DELETE
FROM state_action
WHERE action_id IN (
  SELECT id
  FROM action
  WHERE id LIKE "%_ENDORSE")
;

DELETE
FROM action
WHERE id LIKE "%_ENDORSE"
;
