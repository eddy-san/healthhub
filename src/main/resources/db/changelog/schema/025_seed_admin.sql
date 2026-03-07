IF NOT EXISTS (
    SELECT 1
    FROM app_user
    WHERE username = 'admin'
)
BEGIN
INSERT INTO app_user (
    username,
    password_hash,
    email,
    enabled
)
VALUES (
           'admin',
           'pbkdf2$120000$e+xOxYr0+zrcmMndQnU9pQ==$mM1f9Xn4SLowMQabjWhbJNG3kTDz/mFS+U3rDrthLtA=',
           'admin@healthhub.local',
           1
       );
END;

IF NOT EXISTS (
    SELECT 1
    FROM app_user_role ur
    JOIN app_user u ON ur.user_id = u.id
    JOIN app_role r ON ur.role_id = r.id
    WHERE u.username = 'admin'
      AND r.role_name = 'ADMIN'
)
BEGIN
INSERT INTO app_user_role (user_id, role_id)
SELECT u.id, r.id
FROM app_user u
         JOIN app_role r ON r.role_name = 'ADMIN'
WHERE u.username = 'admin';
END;