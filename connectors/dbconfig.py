import pymysql
from settings.config import *
from connectors.logging import *
import sys
from hashlib import md5
from datetime import datetime
from modules.responses import *


class DBConnector(Responses):

    def __init__(self):
        super(DBConnector, self).__init__()
        self.logs = Logging()
        self.conn = pymysql.connect(host=DB_HOST, user=DB_DBNAME, password=DB_PASSWD, db=DB_DBNAME)
        # self.cur = self.conn.cursor()

    def __del__(self):
        # self.cur.close()
        self.conn.close()

    @staticmethod
    def hash_data(data):
        return str(md5(data.encode('utf-8')).hexdigest())

    @staticmethod
    def get_token():
        return str(md5("Projekt_Devslog_{0}".format(datetime.now()).encode('utf-8')).hexdigest())

    def query(self, query):
        try:
            with self.conn.cursor() as cur:
                cur.execute(query)
                self.conn.commit()
                return cur
        except pymysql.ProgrammingError as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.query[{0}]".format(exc_tb.tb_lineno), args=query)
            return [False, 500, 'SQL syntax error']
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.query[{0}]".format(exc_tb.tb_lineno), args=query)
            return [False, 500, 'Unexpected DB Exception']

    def login_user(self, keys):
        try:
            get_user = self.query(GET_USER.format(keys['email'], keys['domain'])).fetchone()
            if get_user is None:
                return [False, 202, "Email or password validation error"]
            elif get_user[1] == self.hash_data(keys['password']):
                return [True, 200, {"user_id": get_user[0]}]
            elif get_user[1] != self.hash_data(keys['password']):
                return [False, 202, "Email or password validation error"]
            else:
                return [False, 202, "Email or password validation error"]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.login_user[{0}]".format(exc_tb.tb_lineno), args=keys)
            return [False, 500, "Unexpected exception on {}".format(datetime.now())]

    def get_name(self, user):
        try:
            user = self.query(GET_USERNAME.format(user)).fetchone()
            if not user:
                return [False, "", ""]
            return [True, user[0], user[1]]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.get_name[{0}]".format(exc_tb.tb_lineno), args=keys)
            return [False, "", ""]

    def save_logging(self, user_id, token):
        self.query(ADD_LOG_ACTION.format(user_id, token))

    def create_admin(self, keys):
        try:
            result = self.query(CREATE_USER.format(keys['email'], self.hash_data(keys['password']), keys['name'])).fetchone()
            return [True, result[0]]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.create_admin[{0}]".format(exc_tb.tb_lineno), args=keys)
            return [False, 0]

    def create_domain(self, domain, user_id):
        try:
            available = self.query(CHECK_DOMAIN_AVAILABLE.format(domain)).fetchall()
            if len(available) == 0:
                self.query(CREATE_DOMAIN.format(domain, user_id))
                result = self.query(GET_DOMAIN.format(user_id, domain)).fetchone()
                self.query(CREATE_ACCESS_ADMIN.format(user_id, domain))
                return [True, result[0]]
            return [False, available[0]]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.create_domain[{0}]".format(exc_tb.tb_lineno), args=domain)
            return [False, 0]

    # def add_to_domain(self, domain, user):
    #     try:
    #         self.query(CREATE_ACCESS_ADMIN.format(user, domain)).fetchone()
    #         return True
    #     except Exception as e:
    #         _, _, exc_tb = sys.exc_info()
    #         self.logs.save_msg(e, localisation="DBConnector.add_to_domain[{0}]".format(exc_tb.tb_lineno), args=domain)
    #         return False

    def add_kierownik_to_project(self, domain, user, kierownik):
        try:
            priv = self.query(GET_PRIVILEGE_ON_DOMAIN.format(kierownik, domain)).fetchone()
            if priv[0] != 'Kierownik':
                return False
            self.query(CREATE_ACCESS_TO_DOMAIN.format(user, domain, kierownik, 2))
            self.query(ADD_NOTIFY_CREATE_ACCESS_TO_DOMAIN.format(keys['email'], keys['domain'], 2))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.add_to_domain[{0}]".format(exc_tb.tb_lineno), args=domain)
            return False

    def create_user(self, keys):
        try:
            self.query(CREATE_USER.format(keys['email'], self.hash_data(keys['password']), keys['username']))
            result = self.query(GET_USER_AFTER_CREATE.format(keys['email'], self.hash_data(keys['password']), keys['username'])).fetchone()
            self.query(CREATE_ACCESS_DOMAIN.format(result[0], keys['domain'], keys['privilege'], keys['user_id']))
            self.query(ADD_NOTIFY_CREATE_USER.format(keys['email'], keys['domain']))
            return [True, result[0]]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.create_user[{0}]".format(exc_tb.tb_lineno), args=keys)
            return [False, 0]
        
    def check_user_exists(self, email):
        try:
            result = self.query(CHECK_USER_EXISTS.format(email)).fetchone()
            if not result:
                return True
            return False
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.create_user[{0}]".format(exc_tb.tb_lineno), args=keys)
            return False

    def check_user_exists_change_passwd(self, email):
        try:
            result = self.query(CHECK_USER_EXISTS.format(email)).fetchone()
            if not result:
                return [False, 0]
            return [True, result[0]]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.check_user_exists_change_passwd[{0}]".format(exc_tb.tb_lineno), args=keys)
            return [False, 0]

    def checktoken(self, token):
        try:
            user = token.split("@")
            result = self.query(CHECK_TOKEN.format(user[0])).fetchone()
            if not result:
                return False
            print(token, result[0])
            if token == result[0]:
                return True
            return False
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.checktoken[{0}]".format(exc_tb.tb_lineno), args=token)
            return False

    def generate_password(self, password, user):
        try:
            self.query(GENERATE_PASSWORD.format(self.hash_data(password), user))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.generate_password[{0}]".format(exc_tb.tb_lineno),
                               args=[password, user])
            return False

    def check_privileges(self, privilege):
        try:
            priv = self.query(CHECK_PRIV.format(privilege)).fetchone()
            if priv is None:
                return [False, 0]
            return [True, priv[0]]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.check_privileges[{0}]".format(exc_tb.tb_lineno),
                               args=privilege)
            return [False, 0]

    def check_user(self, user):
        try:
            is_user = self.query(CHECK_USER_EXIST.format(user)).fetchone()
            if not is_user:
                raise Exception("User not found")
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.check_user[{0}]".format(exc_tb.tb_lineno),
                               args=user)
            return False
        
    def check_user_by_id(self, user_id):
        try:
            is_user = self.query(CHECK_USER_EXIST_BY_ID.format(user_id)).fetchone()
            if not is_user:
                raise Exception("User not found")
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.check_user[{0}]".format(exc_tb.tb_lineno),
                               args=user)
            return False
        
    def update_permission(self, keys):
        try:
            perm_exist = self.query(CHECK_PERMISSION.format(keys['granted_to'], keys['domain'])).fetchone()
            if not perm_exist:
                self.query(CREATE_PERMISSION.format(keys['user_id'], keys['privilege'], keys['granted_id'], keys['domain']))
            else:
                self.query(UPDATE_PERMISSION.format(keys['user_id'], keys['privilege'], keys['granted_id'], keys['domain']))
            self.query(ADD_NOTIFY_PERMISSION_GRANTED.format(keys['domain'], keys['granted_to'], keys['user_id'], keys['privilege']))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.update_permission[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return False

    def get_projects_plus_user_counts(self, user_id):
        try:
            self.query(REMOVE_PERMISSION.format(keys['domain'], keys['granted_id'], keys['user_id']))
            self.query(ADD_NOTIFY_PERMISSION_GRANTED.format(keys['domain'], keys['granted_id'], keys['user_id']))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.remove_permission[{0}]".format(exc_tb.tb_lineno),
                               args=user_id)
            return False

    def get_projects_users(self, data):
        try:
            result = self.query(GET_PROJECTS_PLUS_USER_COUNTS.format(data)).fetchall()
            if not result:
                return [True, [0]]
            return [True, result]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.get_projects_users[{0}]".format(exc_tb.tb_lineno),
                               args=data)
            return [False, 0]

    def remove_permission(self, keys):
        try:
            self.query(REMOVE_PERMISSION.format(keys['domain'], keys['granted_to'], keys['user_id']))
            self.query(ADD_NOTIFY_PERMISSION_GRANTED.format(keys['domain'], keys['granted_to'], keys['user_id']))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.remove_permission[{0}]".format(exc_tb.tb_lineno),
                               args=user)
            return False

    def get_user_info(self):
        return False
        # try:
        #
        #     results = self.query(GET_USER_INFO.format(user_id)).fetchall()
        #     if not result:
        #
        #     return [True, {"name": , "email": ,"domains": [], ""}]
        # except Exception as e:
        #     _, _, exc_tb = sys.exc_info()
        #     self.logs.save_msg(e, localisation="DBConnector.get_user_info[{0}]".format(exc_tb.tb_lineno),
        #                        args=user)
        #     return [False, {}]

    def check_admin(self, user):
        try:
            is_user = self.query(CHECK_ADMIN_EXIST.format(user)).fetchone()
            if not is_user:
                raise Exception("Permission Denied")
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.check_admin[{0}]".format(exc_tb.tb_lineno),
                               args=user)
            return False

    def check_kierownik(self, user):
        try:
            is_user = self.query(CHECK_KIEROWNIK_EXIST.format(user)).fetchone()
            if not is_user:
                raise Exception("Permission Denied")
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.check_kierownik[{0}]".format(exc_tb.tb_lineno),
                               args=user)
            return False

    def add_project(self, keys):
        try:
            self.query(CREATE_PROJECT.format(keys['user_id'], keys['domain_id'], keys['project_name']))
            project_id = self.query(GET_LAST_PROJECT_ID.format(keys['user_id'], keys['domain_id'], keys['project_name'])).fetchone()
            self.query(CREATE_ACCESS_PROJECT_KIEROWNIK.format(keys['user_id'], keys['domain_id'], project_id[0]))
            return [True, project_id]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.add_project[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return [False, 0]

    def change_password(self, token, passwd):
        try:
            user_id = token.split("@")
            if len(user_id) != 2:
                return [False, 'Token expired']
            get_token = self.query(GET_TOKEN_BY_USER_ID.format(user_id[0])).fetchone()
            if token.replace("@", "##") != get_token[0]:
                return [False, 'Token expired']
            self.query(CHANGE_PASSWORD.format(self.hash_data(passwd), user_id[0])).fetchone()
            return [True, "Password changed"]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.change_password[{0}]".format(exc_tb.tb_lineno),
                               args=[token, passwd])
            return [False, "Unexpected exception: reported"]
