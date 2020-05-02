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
GET_PROJECTS_PLUS_USER_COUNTS = """SELECT COUNT(u.name), p.id, p.project_name, d.domain_desc,
       (SELECT pr.privilege FROM accesses ac INNER JOIN privileges pr ON ac.privilege_id = pr.id WHERE ac.project_id=p.id AND ac.granted_to='{0}')
    FROM accesses acc
    RIGHT JOIN users u ON acc.granted_to=u.id
    RIGHT JOIN projects p on acc.project_id = p.id
    RIGHT JOIN domains d on acc.domain_id = d.id
    RIGHT JOIN privileges pr ON acc.privilege_id=pr.id
    WHERE acc.project_id IN (SELECT DISTINCT(project_id) FROM accesses ac WHERE ac.granted_to='{0}' AND ac.project_id IS NOT NULL)
    GROUP BY p.id;"""
CHECK_PRIV = "SELECT id FROM privileges WHERE privilege='{0}'"
CHECK_DOMAIN_AVAILABLE = "SELECT id FROM domains WHERE domain_desc='{0}'"
CHECK_USER_EXIST = "SELECT id FROM users WHERE id='{0}' OR name='{0}' OR email='{0}'"
CHECK_ADMIN_EXIST = "SELECT id, granted_to FROM accesses WHERE granted_to=(SELECT id FROM users WHERE id='{0}' OR name='{0}' OR email='{0}') AND privilege_id=1"
CHECK_KIEROWNIK_EXIST = "SELECT id FROM accesses WHERE granted_to=(SELECT id FROM users WHERE id='{0}' OR email='{0}' or name='{0}') AND privilege_id=1"
CHECK_TOKEN = "SELECT token FROM loginactions WHERE user_id='{}' ORDER BY created_at DESC LIMIT 1"
CHECK_PERMISSION = "SELECT id FROM accesses WHERE granted_to='{0}' AND domain_id=(SELECT id FROM domains WHERE domain_desc='{1}')"
CHECK_USER_EXISTS = """SELECT id FROM users WHERE email='{0}'"""
CHECK_USER_EXIST_BY_ID = """SELECT id FROM users WHERE id='{0}'"""
CREATE_USER = "INSERT INTO users(email, password, name, created_at) VALUES ('{0}', '{1}', '{2}', NOW())"
CREATE_DOMAIN = "INSERT INTO domains(domain_desc, creator_id, created_at) VALUES ('{0}', {1}, NOW())"
CREATE_ACCESS_ADMIN = "INSERT INTO accesses(creator_id, domain_id, privilege_id, granted_to, created_at) VALUES ({0}, (SELECT id FROM domains WHERE domain_desc='{1}'), 1, {0}, NOW());"
CREATE_ACCESS_TO_DOMAIN = "INSERT INTO accesses(creator_id, domain_id, privilege_id, granted_to, created_at) VALUES ({3}, {1}, '{2}', (SELECT id FROM users WHERE name='{0}' OR email='{0}' OR id='{0}'), NOW());"
CREATE_ACCESS_DOMAIN = """INSERT INTO accesses(creator_id, domain_id, privilege_id, granted_to, created_at) VALUES 
                                ({3}, (SELECT id FROM domains WHERE domain_desc='{1}'), 
                                (SELECT id FROM privileges WHERE privilege='{2}'), 
                                {0}, NOW());"""
CREATE_PERMISSION = """INSERT INTO accesses(creator_id, granted_to, domain_id, privilege_id, created_at) VALUES ('{0}' ,'{2}' ,(SELECT id FROM domains WHERE domain_desc='{3}') ,(SELECT id FROM privileges WHERE privilege_id='{1}') , NOW());"""
GENERATE_PASSWORD = "UPDATE users SET password='{0}' WHERE id='{1CHECK_USER_EXIST}' OR email='{1}' OR name='{1}'"
GET_DOMAINS = "SELECT id, domain_desc FROM domains WHERE id IN (SELECT domain_id FROM accesses WHERE granted_to={0}) ORDER BY id DESC;"
GET_DOMAIN = "SELECT id FROM domains WHERE creator_id={0} AND domain_desc='{1}'"
GET_NOTIFICATIONS = """SELECT id, notify_desc, created_at, is_readed FROM notifications WHERE user_id=(SELECT id FROM users 
                        WHERE id='{0}' OR email='{0}') AND is_readed=false ORDER BY id DESC;"""
GET_USER = '''SELECT id, password FROM users WHERE email="{0}"'''
GET_USER_AFTER_CREATE = """SELECT id FROM users WHERE email='{0}' AND password='{1}' AND name='{2}'"""
GET_USERS_FROM_PROJECT = ""
GET_USERS_FROM_TASK = ""
GET_USERS_FROM_DOMAIN = """SELECT usr.id, usr.name, priv.privilege FROM accesses ac
                                INNER JOIN privileges priv on ac.privilege_id = priv.id
                                INNER JOIN users usr on ac.granted_to = usr.id
                                WHERE ac.creator_id={0} AND granted_to !={0}
                                ORDER BY usr.id DESC;"""
GET_PRIVILEGE = """SELECT 'Not granted'
                        FROM accesses ac
                        where not exists (SELECT p.privilege FROM accesses ac
                            INNER JOIN privileges p on ac.privilege_id = p.id
                            WHERE ac.granted_to={0})
                        Union
                        SELECT p.privilege FROM accesses ac
                            INNER JOIN privileges p on ac.privilege_id = p.id
                            WHERE ac.granted_to={0}"""
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
                        RIGHT JOIN users x ON w.granted_to=x.id
                        RIGHT JOIN domains z on w.domain_id = z.id
                        WHERE w.granted_to='{0}'"""
MARK_NOTIFY_AS_READED = "UPDATE notifications SET is_readed=true WHERE id={0} AND user_id=(SELECT id FROM users WHERE email='{1}' OR id='{1}')"
REMOVE_PERMISSION = "DELETE FROM accesses WHERE granted_to='{1}' AND domain_id=(SELECT id FROM domains WHERE domain_desc='{0}')"
UPDATE_PERMISSION = "UPDATE accesses SET privilege_id=(SELECT id FROM privileges WHERE privilege_id='{1}'), creator_id='{0}' WHERE granted_to='{2}' AND domain_id=(SELECT id FROM domains WHERE domain_desc='{3}')"
# keys['user_id'], keys['privilege'], keys['granted_id'], keys['domain']
# QUERIES - project

