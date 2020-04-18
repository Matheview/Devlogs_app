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
GET_USER = '''SELECT id, password FROM users WHERE email="{0}"'''
CHECK_PRIV = "SELECT id FROM privileges WHERE privilege='{0}'"
ADD_LOG_ACTION = "INSERT INTO login_actions(user_id, token, created_at) VALUES ({0}, '{1}', NOW())"
CREATE_USER = "INSERT INTO user(email, password, name, created_at) VALUES ('{0}', '{1}', '{2}', NOW()) RETURNING id"
CREATE_DOMAIN = "INSERT INTO domains(domain_desc, creator_id, created_at) VALUES ('{0}', {1}, NOW()) RETURNING id"
CREATE_ACCESS_ADMIN = "INSERT INTO accesses(creator_id, domain_id, privilege_id, granted_to, created_at) VALUES ({0}, {1}, 1, {0}, NOW());"
CREATE_ACCESS_TO_DOMAIN = "INSERT INTO accesses(creator_id, domain_id, privilege_id, granted_to, created_at) VALUES ({0}, {1}, {2}, (SELECT id FROM users WHERE name='{3}' OR email='{3}'), NOW());"
CREATE_ACCESS_DOMAIN = """INSERT INTO accesses(creator_id, domain_id, privilege_id, granted_to, created_at) VALUES 
                                ({0}, (SELECT id FROM domains WHERE domain_desc='{1}'), 
                                (SELECT id FROM privileges WHERE privilege='{2}'), 
                                {0}, NOW());"""
GET_DOMAINS = "SELECT id, domain_desc FROM domains WHERE id IN (SELECT domain_id FROM accesses WHERE granted_to={0}) ORDER BY id DESC;"
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
CHECK_DOMAIN_AVAILABLE = "SELECT id FROM domains WHERE domain_desc='{0}'"
CHECK_USER_EXIST = "SELECT id FROM users WHERE id={0}"
CHECK_ADMIN_EXIST = "SELECT id FROM accesses WHERE granted_to={0} AND privilege_id=1"
CHECK_KIEROWNIK_EXIST = "SELECT id FROM accesses WHERE granted_to=(SELECT id FROM users WHERE id='{0}' OR email='{0}' or name='{0}') AND privilege_id=1"
GET_USERS_FROM_PROJECT = ""
GET_USERS_FROM_TASK = ""


# QUERIES - project

