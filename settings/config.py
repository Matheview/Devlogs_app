from datetime import datetime
import hashlib

# FILE CONFIG
PROJECT_DIR = "/root/DevslogApp"
SECRET_HASH = "55672dd2b20791b24d64598fffa304bc"
EMAIL_REGEX = '^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$'

# DB configuration for mysql
DB_HOST = "nazwaszymons.nazwa.pl"
DB_DBNAME = "nazwaszymons_projektSzc"
DB_USER = "nazwaszymons_projektSzc"
DB_PASSWD = "Qwerty1234"

# QUERIES - user
ADD_COMMENT = """INSERT INTO comments(task_id, user_id, comment_desc, created_at) VALUES ('{0}', '{1}', '{2}', NOW())"""
ADD_LOG_ACTION = "INSERT INTO loginactions(user_id, token, created_at) VALUES ({0}, '{1}', NOW())"
ADD_NOTIFY_CREATE_USER = """INSERT INTO notifications(domain_id, user_id, notify_desc, is_readed, created_at) VALUES (
                         (SELECT id FROM domains WHERE domain_desc='{0}'), 
                         (SELECT id FROM users WHERE email='{1}'),
                         CONCAT('Użytkownik ', (SELECT name FROM users WHERE email='{1}'), ' dodał Ciebie do przestrzeni roboczej {0}'),
                         False, NOW())"""
ADD_NOTIFY_PERMISSION_GRANTED = """INSERT INTO notifications(domain_id, user_id, notify_desc, is_readed, created_at) VALUES (
                         (SELECT id FROM domains WHERE domain_desc='{0}'), 
                         (SELECT id FROM users WHERE email='{1}' OR id='{1}'),
                         CONCAT('Użytkownik ', (SELECT name FROM users WHERE id='{2}'), ' usunął Twoje uprawenienia do domeny {0}'),
                         False, NOW())"""
# ADD_NOTIFY_CREATE_ACCESS_TO_DOMAIN = """INSERT INTO notifications(domain_id, user_id, notify_desc, is_readed, created_at) VALUES (
#                          (SELECT id FROM domains WHERE domain_desc='{0}'),
#                          (SELECT id FROM users WHERE email='{1}'),
#                          CONCAT('Użytkownik ', (SELECT name FROM users WHERE email='{1}'), ' dodał Ciebie do przestrzeni roboczej {0}'),
#                          False, NOW())"""
ADD_USER_TO_PROJECT = """INSERT INTO accesses(creator_id, domain_id, project_id, privilege_id, granted_to, created_at) VALUES ({0}, {1}, {2}, 3, {3}, NOW());"""
GET_PROJECTS_PLUS_USER_COUNTS = """SELECT COUNT(u.name), p.id, p.project_name, d.domain_desc,
       (SELECT pr.privilege FROM accesses ac INNER JOIN privileges pr ON ac.privilege_id = pr.id WHERE ac.project_id=p.id AND ac.granted_to='{0}')
    FROM accesses acc
    RIGHT JOIN users u ON acc.granted_to=u.id
    RIGHT JOIN projects p on acc.project_id = p.id
    RIGHT JOIN domains d on acc.domain_id = d.id
    RIGHT JOIN privileges pr ON acc.privilege_id=pr.id
    WHERE acc.project_id IN (SELECT DISTINCT(project_id) FROM accesses ac WHERE ac.granted_to='{0}' AND ac.project_id IS NOT NULL)
    GROUP BY p.id;"""
CHANGE_PASSWORD = "UPDATE users SET password='{0}' WHERE id={1}"
CHECK_PRIV = "SELECT id FROM privileges WHERE privilege='{0}'"
CHECK_CAN_REMOVE_STATUS = """SELECT id FROM tasks WHERE status_id='{1}' AND project_id='{0}'"""
CHECK_DOMAIN_AVAILABLE = "SELECT id FROM domains WHERE domain_desc='{0}'"
CHECK_DOMAIN_EXIST = "SELECT id FROM domains WHERE id='{0}' OR domain_desc='{0}'"
CHECK_DOMAIN_EXIST_BY_NAME = "SELECT id FROM domains WHERE domain_desc='{0}'"
CHECK_USER_EXIST = "SELECT id FROM users WHERE id='{0}' OR name='{0}' OR email='{0}'"
CHECK_ADMIN_EXIST = "SELECT id, granted_to FROM accesses WHERE granted_to=(SELECT id FROM users WHERE id='{0}' OR name='{0}' OR email='{0}') AND privilege_id=1"
CHECK_KIEROWNIK_EXIST = "SELECT id FROM accesses WHERE granted_to=(SELECT id FROM users WHERE id='{0}' OR email='{0}' or name='{0}') AND privilege_id=2"
CHECK_TOKEN = "SELECT CASE WHEN TIMESTAMPDIFF(HOUR, created_at, NOW()) < 24 THEN token ELSE 'ExpiredTokenAfter24Hours' END FROM loginactions WHERE user_id='{}' ORDER BY created_at DESC LIMIT 1"
CHECK_PERMISSION = "SELECT id FROM accesses WHERE granted_to='{0}' AND domain_id=(SELECT id FROM domains WHERE domain_desc='{1}')"
CHECK_TASK_EXISTS = """SELECT id FROM tasks WHERE (id='{0}' OR task_title='{0}') AND project_id='{1}'"""
CHECK_TASK_EXISTS_MIN = """SELECT id FROM tasks WHERE (id='{0}' OR task_title='{0}')"""
CHECK_COMMENT_IS_USER = "SELECT id FROM comments WHERE id='{0}' AND user_id='{1}'"
CHECK_STATUS_EXISTS = """SELECT id FROM statuses WHERE id='{0}' AND project_id='{1}'"""
CHECK_USER_EXISTS = """SELECT id FROM users WHERE email='{0}'"""
CHECK_USER_EXIST_BY_ID = """SELECT id FROM users WHERE id='{0}'"""
CHECK_USER_INSIDE_PROJECT = """SELECT id FROM tasks WHERE project_id={0} AND assigned_id={1} LIMIT 1"""
CHECK_PROJECT_EXIST = """SELECT id FROM projects WHERE project_name='{0}' AND domain_id={1}"""
CREATE_USER = "INSERT INTO users(email, password, name, created_at) VALUES ('{0}', '{1}', '{2}', NOW())"
CREATE_DOMAIN = "INSERT INTO domains(domain_desc, creator_id, created_at) VALUES ('{0}', {1}, NOW())"
CREATE_ACCESS_ADMIN = "INSERT INTO accesses(creator_id, domain_id, privilege_id, granted_to, created_at) VALUES ({0}, (SELECT id FROM domains WHERE domain_desc='{1}'), 1, {0}, NOW());"
CREATE_ACCESS_TO_DOMAIN = "INSERT INTO accesses(creator_id, domain_id, privilege_id, granted_to, created_at) VALUES ({3}, {1}, '{2}', (SELECT id FROM users WHERE name='{0}' OR email='{0}' OR id='{0}'), NOW());"
CREATE_ACCESS_DOMAIN = """INSERT INTO accesses(creator_id, domain_id, privilege_id, granted_to, created_at) VALUES 
                                ({3}, (SELECT id FROM domains WHERE domain_desc='{1}'), 
                                (SELECT id FROM privileges WHERE privilege='{2}'), 
                                {0}, NOW());"""
CREATE_ACCESS_PROJECT_KIEROWNIK = """INSERT INTO accesses(creator_id, domain_id, project_id, privilege_id, granted_to, created_at) VALUES ({0}, {1}, {2}, 2, {0}, NOW());"""
CREATE_PERMISSION = """INSERT INTO accesses(creator_id, granted_to, domain_id, privilege_id, created_at) VALUES ('{0}' ,'{2}' ,(SELECT id FROM domains WHERE domain_desc='{3}') ,(SELECT id FROM privileges WHERE privilege='{1}') , NOW());"""
CREATE_PROJECT = """INSERT INTO projects(creator_id, project_name, domain_id, created_at) VALUES ({0}, '{2}', {1}, NOW())"""
CREATE_STATUS = """INSERT INTO statuses(status_desc, project_id, created_at) VALUES ('{0}', '{1}', NOW())"""
CREATE_TASK_WITH_USER = """INSERT INTO tasks(project_id, creator_id, task_title, status_id, created_at, assigned_id) VALUES ('{0}', '{1}', '{2}', '{3}', NOW(), '{4}')"""
CREATE_TASK_WITHOUT_USER = """INSERT INTO tasks(project_id, creator_id, task_title, status_id, created_at) VALUES ('{0}', '{1}', '{2}', '{3}', NOW())"""
GENERATE_PASSWORD = "UPDATE users SET password='{0}' WHERE id='{1CHECK_USER_EXIST}' OR email='{1}' OR name='{1}'"
GET_ALL_USERS_FROM_TASK = """SELECT DISTINCT x.id, x.name FROM (
    SELECT u.id, u.name FROM tasks
    INNER JOIN users u on tasks.assigned_id = u.id
    WHERE tasks.id='{0}'
UNION ALL
SELECT u2.id, u2.name FROM comments
    INNER JOIN users u2 on comments.user_id = u2.id
    WHERE task_id='{0}') x"""
GET_COMMENTS_FROM_TASK = """SELECT id, comment_desc, user_id, created_at FROM comments WHERE task_id='{0}'"""
GET_CREATED_STATUS_ID = "SELECT id FROM statuses WHERE project_id={0} AND status_desc='{1}'"
GET_DOMAINS = "SELECT DISTINCT d.id, d.domain_desc FROM accesses acc INNER JOIN domains d on acc.domain_id = d.id WHERE acc.granted_to='{0}' ORDER BY d.id DESC;"
GET_DOMAIN = "SELECT id FROM domains WHERE creator_id={0} AND domain_desc='{1}'"
GET_DOMAIN_ID = "SELECT id FROM domains WHERE domain_desc='{0}' LIMIT 1"
GET_DOMAIN_NAME_BY_ID = """SELECT domain_desc FROm domains WHERE id={0}"""
GET_FULL_TASK_INFO = """SELECT t.id, task_title, task_desc, deadline, t.created_at, p.priority_desc, assigned_id FROM tasks t
    LEFT JOIN priorities p on t.priority_id = p.id
    WHERE t.id='{0}'"""
GET_NOTIFICATIONS = """SELECT id, notify_desc, created_at, is_readed FROM notifications WHERE user_id=(SELECT id FROM users 
                        WHERE id='{0}' OR email='{0}') AND is_readed=false ORDER BY id DESC;"""
GET_LAST_PROJECT_ID = """SELECT id FROM projects WHERE creator_id={0} AND domain_id={1} AND project_name='{2}'"""
GET_LAST_TASK_ADDED = """SELECT id FROM tasks WHERE project_id='{0}' AND task_title='{1}' AND status_id='{2}' ORDER BY id DESC LIMIT 1"""
GET_TOKEN_BY_USER_ID = """SELECT token FROM loginactions WHERE user_id={0} ORDER BY id DESC LIMIT 1"""
GET_USER = '''SELECT id, password FROM users WHERE email="{0}"'''
GET_USER_AFTER_CREATE = """SELECT id FROM users WHERE email='{0}' AND password='{1}' AND name='{2}'"""
GET_USER_INFO_ALL = """SELECT usr.name, usr.email, usr.created_at FROM users usr WHERE id={0}"""
GET_USERS_FROM_PROJECT = ""
GET_USERS_FROM_TASK = ""
GET_PROJECT_LIST_TO_ALL = """SELECT x.project_name, x.id, x.creator_id, x.created_at FROM
    (SELECT project_name, id, creator_id, created_at FROM projects WHERE domain_id={0}) as x;"""
GET_PROJECT_LIST_TO_MINE = """SELECT x.project_name, x.id, x.creator_id, x.created_at FROM
    (SELECT project_name, id, creator_id, created_at FROM projects WHERE id={0}) as x;"""
GET_USER_COUNT_INSIDE_PROJECT = """SELECT COUNT(DISTINCT assigned_id) FROM tasks WHERE project_id={0} AND assigned_id IS NOT NULL;"""
GET_TASKS_FROM_PROJECT = """SELECT tasks.id, task_title, tasks.created_at, deadline, p.priority_desc, status_id, assigned_id FROM tasks LEFT JOIN priorities p on tasks.priority_id = p.id WHERE project_id={0};"""
GET_STATUSES_FROM_DOMAIN_WHERE_PROJECTS = """SELECT id, status_desc FROM statuses WHERE id in (SELECT status_id FROM tasks WHERE tasks.project_id IN (SELECT id FROM projects WHERE domain_id={0}))"""
GET_STATUSES_FROM_PROJECT_WHERE_PROJECTS = """SELECT id, status_desc FROM statuses WHERE id in (SELECT status_id FROM tasks WHERE tasks.project_id IN (SELECT id FROM projects WHERE id={0}))"""
GET_ALL_USERS_INSIDE_DOMAIN = """SELECT DISTINCT creator_id, granted_to, u.name, CASE WHEN privilege_id=2 THEN True ELSE False END FROM accesses INNER JOIN users u ON accesses.granted_to = u.id WHERE domain_id={0}"""
GET_ALL_USERS_INSIDE_PROJECT = """SELECT DISTINCT creator_id, granted_to, u.name, CASE WHEN privilege_id=2 THEN True ELSE False END FROM accesses INNER JOIN users u ON accesses.granted_to = u.id WHERE project_id={0}"""
GET_USERNAME_BY_ID = """SELECT name FROM users WHERE id={0}"""
GET_USERS_FROM_DOMAIN = """SELECT usr.id, usr.name, priv.privilege FROM accesses ac
                                INNER JOIN privileges priv on ac.privilege_id = priv.id
                                INNER JOIN users usr on ac.granted_to = usr.id
                                WHERE ac.creator_id={0} AND granted_to !={0}
                                ORDER BY usr.id DESC;"""
GET_USERS_FROM_DOMAIN_KIEROWNIK = """SELECT DISTINCT x.id, x.name, x.privilege FROM (SELECT usr.id, usr.name, priv.privilege FROM accesses ac
    INNER JOIN privileges priv on ac.privilege_id = priv.id
    INNER JOIN users usr on ac.granted_to = usr.id
    WHERE ac.domain_id=(SELECT acc.domain_id FROM accesses acc WHERE acc.granted_to={0} AND acc.domain_id=(SELECT id FROM domains WHERE domain_desc='{1}') LIMIT 1)
    ORDER BY usr.id DESC) as x;"""
CHECK_USER_ADDED_TO_PROJECT = """SELECT id FROM accesses WHERE granted_to={0} AND project_id={1}"""
GET_PROJECT_USERS_PRIVILEGES = """SELECT DISTINCT acc.granted_to, usr.name, pr.privilege FROM accesses acc
    INNER JOIN users usr ON acc.granted_to=usr.id 
    INNER JOIN privileges pr ON acc.privilege_id=pr.id 
    WHERE project_id={0}"""
GET_TASKS_ALL_INFO = """SELECT t.id, CONCAT('#', t.id, ' ', t.task_title) title, t.assigned_id, t.created_at, t.deadline, p.priority_desc, (SELECT COUNT(*) FROM comments WHERE task_id=t.id) count FROM tasks t
    LEFT JOIN priorities p on t.priority_id = p.id WHERE project_id={0} AND status_id={1} ORDER BY t.id;"""
GET_PROJECT_STATUSES = """SELECT x.id, x.status_desc FROM (SELECT DISTINCT s.id, s.status_desc FROM tasks
    Right JOIN statuses s on tasks.status_id = s.id
    WHERE s.project_id={0}) x ORDER BY x.id;"""
GET_PROJECT_MIN_INFO = """SELECT project_name FROM projects WHERE id={0}"""
GET_PROJECT_FROM_DOMAIN_MIN = """SELECT id FROM projects WHERE id='{0}' AND domain_id=(SELECT id FROM domains WHERE domain_desc='{1}')"""
GET_PRIVILEGE = """SELECT 'Not granted'
                        FROM accesses ac
                        where not exists (SELECT p.privilege FROM accesses ac
                            INNER JOIN privileges p on ac.privilege_id = p.id
                            WHERE ac.granted_to={0})
                        Union
                        SELECT p.privilege FROM accesses ac
                            INNER JOIN privileges p on ac.privilege_id = p.id
                            WHERE ac.granted_to={0}"""
GET_STATUS_AVAILABLE = "SELECT id, status_desc FROM statuses WHERE project_id={0} AND status_desc='{1}'"
GET_PRIVILEGE_ON_DOMAIN = """SELECT 'Not granted'
                        FROM accesses ac
                        where not exists (SELECT p.privilege FROM accesses ac
                            INNER JOIN privileges p on ac.privilege_id = p.id
                            WHERE ac.granted_to=(SELECT id FROM users WHERE name='{0}' OR email='{0}') AND ac.domain_id=(SELECT id FROM domains WHERE domain_desc='{1}'))
                        Union
                        SELECT p.privilege FROM accesses ac
                            INNER JOIN privileges p on ac.privilege_id = p.id
                            WHERE ac.granted_to=(SELECT id FROM users WHERE name='{0}' OR email='{0}') AND ac.domain_id=(SELECT id FROM domains WHERE domain_desc='{1}')"""
GET_USERNAME = "SELECT id, name FROM users WHERE id='{0}' OR email='{0}'"
GET_USER_INFO = """SELECT x.id, x.name, x.email, y.privilege, z.domain_desc FROM accesses w
                        RIGHT JOIN privileges y ON w.privilege_id=y.id
                        RIGHT JOIN users x ON w.granted_to=x.i
                        d
                        RIGHT JOIN domains z on w.domain_id = z.id
                        WHERE w.granted_to='{0}'"""
IS_USER_ADDED_TO_PROJECT = """SELECT id FROM accesses WHERE project_id={0} AND granted_to={1}"""
MARK_NOTIFY_AS_READED = "UPDATE notifications SET is_readed=true WHERE id={0} AND user_id=(SELECT id FROM users WHERE email='{1}' OR id='{1}')"
SAVE_ERROR_LOGS_TO_DB = """INSERT INTO errorlogs(class, args, msg, created_at) VALUES ('{}', '{}', '{}', NOW())"""
SET_USER_REMOVE_TASK = "UPDATE tasks SET assigned_id=NULL WHERE id='{0}';"
REMOVE_COMMENT = "DELETE FROM comments WHERE id='{0}'"
REMOVE_COMMENTS_FROM_TASK = "DELETE FROM comments WHERE task_id='{0}'"
REMOVE_PERMISSION = "DELETE FROM accesses WHERE granted_to='{1}' AND domain_id=(SELECT id FROM domains WHERE domain_desc='{0}')"
REMOVE_STATUS = """DELETE FROM statuses WHERE project_id='{0}' AND id='{1}'"""
REMOVE_TASK = """DELETE FROM tasks WHERE id='{0}'"""
REMOVE_USER_FROM_PROJECT = """DELETE FROM accesses WHERE project_id={0} AND granted_to={1}"""
UPDATE_COMMENT = """UPDATE comments SET comment_desc='{2}' WHERE id='{0}' AND user_id='{1}';"""
UPDATE_PERMISSION = "UPDATE accesses SET privilege_id=(SELECT id FROM privileges WHERE privilege='{1}'), creator_id='{0}' WHERE granted_to='{2}' AND domain_id=(SELECT id FROM domains WHERE domain_desc='{3}')"
UPDATE_STATUS = """UPDATE statuses SET status_desc='{2}' WHERE project_id='{0}' AND id='{1}'"""
UPDATE_TASK_TASKNAME = """UPDATE tasks SET task_title='{0}' WHERE id='{1}'"""
UPDATE_TASK_TASKDESC = """UPDATE tasks SET task_desc='{0}' WHERE id='{1}'"""
UPDATE_TASK_STATUSID = """UPDATE tasks SET status_id='{0}' WHERE id='{1}'"""
UPDATE_TASK_ASSIGNEDTO = """UPDATE tasks SET assigned_id='{0}' WHERE id='{1}'"""
UPDATE_TASK_DEADLINE = """UPDATE tasks SET deadline='{0}' WHERE id='{1}'"""
UPDATE_TASK_PRIORITY_DESC = """UPDATE tasks SET priority_id=(SELECT id FROM priorities WHERE priority_desc='{0}') WHERE id='{1}'"""
UPDATE_USERS_STATUS = """UPDATE tasks SET status_id='{1}' WHERE id='{0}' AND assigned_id='{2}'"""
UPDATE_NULL_TASK_TASKNAME = """UPDATE tasks SET task_title=NULL WHERE id='{0}'"""
UPDATE_NULL_TASK_TASKDESC = """UPDATE tasks SET task_desc=NULL WHERE id='{0}'"""
UPDATE_NULL_TASK_STATUSID = """UPDATE tasks SET status_id=NULL WHERE id='{0}'"""
UPDATE_NULL_TASK_ASSIGNEDTO = """UPDATE tasks SET assigned_id=NULL WHERE id='{0}'"""
UPDATE_NULL_TASK_DEADLINE = """UPDATE tasks SET deadline=NULL WHERE id='{0}'"""
UPDATE_NULL_TASK_PRIORITY_DESC = """UPDATE tasks SET priority_id=NULL WHERE id='{0}'"""
# keys['user_id'], keys['privilege'], keys['granted_id'], keys['domain']
# QUERIES - project

