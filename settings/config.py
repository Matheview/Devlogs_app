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
ADD_LOG_ACTION = "INSERT INTO login_actions(user_id, token, created_at) VALUES ({0}, '{1}', NOW())"
CREATE_ADMIN = "INSERT INTO user(email, password, created_at) VALUES ('{}', '{}', NOW()) RETURNING id"
CREATE_DOMAIN = "INSERT INTO domains(domain_desc, creator_id, created_at) VALUES ('{0}', {1}, NOW()) RETURNING id"
CREATE_ACCESS_ADMIN = "INSERT INTO accesses(creator_id, domain_id, privilege_id, granted_to, created_at) VALUES ({0}, {1}, 1, 1, NOW());"


# QUERIES - project

