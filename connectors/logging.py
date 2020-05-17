from datetime import datetime
from settings.config import *
import sys
import pymysql


class Logging:

    def __init__(self):
        self.err_msg = ""
        self.local = []
        self.conn = pymysql.connect(host=DB_HOST, user=DB_DBNAME, password=DB_PASSWD, db=DB_DBNAME)

    def save_msg(self, msg, localisation=None, args=None):
        self.local = "[{}]".format(localisation) if not None else ""
        self.err_msg = msg
        with open(PROJECT_DIR+"/logs/Devslog_{}.log".format(datetime.now().strftime('%Y-%m-%d')), "a") as myfile:
            myfile.write("{0} {1}[{2}] {3}\r".format(datetime.now().strftime('%Y-%m-%d %H:%M:%S'), localisation, args,
                                               self.err_msg))
        self.make_query("""INSERT INTO errorlogs(class, args, msg, created_at) VALUES ('{}', '{}', '{}', NOW())""".format(str(localisation).replace("'", '"'), str(args).replace("'", '"'), str(self.err_msg).replace("'", '"')))

    def make_query(self, query):
        try:
            with self.conn.cursor() as cur:
                cur.execute(query)
                self.conn.commit()
                return cur
        except pymysql.ProgrammingError as e:
            print("SQL exception: ", e)
        except Exception as e:
            print("Exception: ", e)