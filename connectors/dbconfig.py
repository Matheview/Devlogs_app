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

    def create_domain(self, domain):
        try:
            available = self.query(CHECK_DOMAIN_AVAILABLE.format(domain)).fetchone()
            if not available:
                result = self.query(CREATE_DOMAIN.format(domain))
                return [True, result[0]]
            return [False, available[0]]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.create_domain[{0}]".format(exc_tb.tb_lineno), args=domain)
            return [False, 0]

    def add_to_domain(self, domain, user):
        try:
            self.query(CREATE_ACCESS_ADMIN.format(user, domain)).fetchone()
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.add_to_domain[{0}]".format(exc_tb.tb_lineno), args=domain)
            return False

    def add_kierownik_to_project(self, domain, user, kierownik):
        try:
            priv = self.query(GET_PRIVILEGE_ON_DOMAIN.format(kierownik, domain)).fetchone()
            if priv[0] != 'Kierownik':
                return False
            self.query(CREATE_ACCESS_TO_DOMAIN.format(user, domain, kierownik, 2))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.add_to_domain[{0}]".format(exc_tb.tb_lineno), args=domain)
            return False

    def create_user(self, keys):
        try:
            result = self.query(CREATE_USER.format(keys['email'], self.hash_data(keys['password']), keys['name'])).fetchone()
            self.query(CREATE_ACCESS_DOMAIN.format(result[0], keys['domain'], keys['privilege']))
            return [True, result[0]]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.create_user[{0}]".format(exc_tb.tb_lineno), args=keys)
            return [False, 0]

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
            is_user = self.db.query(CHECK_USER_EXIST.format(user)).fetchone()
            if not is_user:
                raise Exception("User not found")
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.check_user[{0}]".format(exc_tb.tb_lineno),
                               args=privilege)
            return False

    def check_admin(self, user):
        try:
            is_user = self.db.query(CHECK_ADMIN_EXIST.format(user)).fetchone()
            if not is_user:
                raise Exception("Permission Denied")
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.check_admin[{0}]".format(exc_tb.tb_lineno),
                               args=privilege)
            return False

    def check_kierownik(self, user):
        try:
            is_user = self.db.query(CHECK_KIEROWNIK_EXIST.format(user)).fetchone()
            if not is_user:
                raise Exception("Permission Denied")
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.check_kierownik[{0}]".format(exc_tb.tb_lineno),
                               args=privilege)
            return False

