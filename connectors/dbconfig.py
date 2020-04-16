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
                return [False, 202, "User with this email not found"]
            elif get_user[1] == self.hash_data(keys['password']):
                return [True, 200, {"user_id": get_user[0]}]
            elif get_user[1] != self.hash_data(keys['password']):
                return [False, 202, "Email or password validation error"]
            else:
                return [False, 202, "Unexpected sequence {}".format(datetime.now())]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.login_user[{0}]".format(exc_tb.tb_lineno), args=keys)
            return [False, 500, "Unexpected exception on {}".format(datetime.now())]

    def save_logging(self, user_id, token):
        self.query(ADD_LOG_ACTION.format(user_id, token))

    def create_admin(self, keys):
        try:
            result = self.query(CREATE_ADMIN.format(keys['email'], self.hash_data(keys['password'])))
            return [True, result[0]]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.login_user[{0}]".format(exc_tb.tb_lineno), args=keys)
            return [False, 0]

    def create_domain(self, domain):
        try:
            result = self.query(CREATE_DOMAIN.format(domain))
            return [True, result[0]]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.login_user[{0}]".format(exc_tb.tb_lineno), args=domain)
            return [False, 0]