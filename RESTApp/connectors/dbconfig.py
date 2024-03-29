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
                               args=user_id)
            return False
        
    def update_permission(self, keys):
        try:
            perm_exist = self.query(CHECK_PERMISSION.format(keys['granted_to'], keys['domain'])).fetchone()
            if not perm_exist:
                self.query(CREATE_PERMISSION.format(keys['user_id'], keys['privilege'], keys['granted_to'], keys['domain']))
            else:
                self.query(UPDATE_PERMISSION.format(keys['user_id'], keys['privilege'], keys['granted_to'], keys['domain']))
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
            return [True, project_id[0]]
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

    def get_all_users_from_project(self, keys):
        users = []
        try:
            result = self.query(GET_PROJECT_USERS_PRIVILEGES.format(keys['project_id'])).fetchall()
            for res in result:
                shortcut = res[1]
                if len(shortcut.split(" ")) == 1:
                    shortcut = shortcut[0]
                else:
                    shortcut = shortcut.split(" ")
                    shortcut = shortcut[0][0] + shortcut[1][0]
                users.append({"user_id": res[0], "name": res[1], "shortcut": shortcut.upper()})
            return users
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.get_all_users_from_project[{0}]".format(exc_tb.tb_lineno),
                               args=[token, passwd])
            return []

    def get_project_info(self, keys):
        try:
            project_info = self.query(GET_PROJECT_MIN_INFO.format(keys['project_id'])).fetchone()
            return [True if project_info else False, project_info[0] if project_info else None]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.get_project_info[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return [False, None]

    def get_statuses_from_project(self, keys):
        statuses = []
        try:
            result_stat = self.query(GET_PROJECT_STATUSES.format(keys['project_id'])).fetchall()
            for res in result_stat:
                tasks = []
                result_tasks = self.query(GET_TASKS_ALL_INFO.format(keys['project_id'], res[0])).fetchall()
                for rt in result_tasks:
                    tasks.append({"task_id": rt[0], "task_name": rt[1], "granted_to": rt[2],
                                  "created_at": rt[3], "deadline": rt[4], "priority": rt[5],
                                  "comments_count": rt[6]})
                statuses.append({"status_id": res[0], "status": res[1], "tasks": tasks})
            return statuses
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.get_statuses_from_project[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return []

    def create_status(self, keys):
        try:
            self.query(CREATE_STATUS.format(keys['status_desc'], keys['project_id']))
            status = self.query(GET_CREATED_STATUS_ID.format(keys['project_id'], keys['status_desc'])).fetchone()
            return status[0]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.create_status[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return 0

    def update_status(self, keys):
        try:
            self.query(UPDATE_STATUS.format(keys['project_id'], keys['status_id'], keys['status_desc']))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e,
                               localisation="DBConnector.update_status[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return False

    def remove_status(self, keys):
        try:
            self.query(REMOVE_STATUS.format(keys['project_id'], keys['status_id']))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e,
                               localisation="DBConnector.remove_status[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return False

    def create_task(self, keys):
        try:
            if keys['assigned_to']:
                self.query(CREATE_TASK_WITH_USER.format(keys['project_id'], keys['creator_id'], keys['task_name'],
                                                        keys['status_id'], keys['assigned_to']))
            else:
                self.query(CREATE_TASK_WITHOUT_USER.format(keys['project_id'], keys['creator_id'], keys['task_name'],
                                                           keys['status_id']))
            result = self.query(GET_LAST_TASK_ADDED.format(keys['project_id'], keys['task_name'], keys['status_id'])).fetchone()
            if not result:
                return result
            return result[0]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e,
                               localisation="DBConnector.create_task[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return None

    def delete_task(self, keys):
        try:
            self.query(REMOVE_COMMENTS_FROM_TASK.format(keys['task_id']))
            self.query(REMOVE_TASK.format(keys['task_id']))
            result = self.query(CHECK_TASK_EXISTS.format(keys['task_id'])).fetchone()
            if not result:
                return True
            return False
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e,
                               localisation="DBConnector.delete_task[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return False

    def update_task(self, keys):
        try:
            queries = {'task_name': UPDATE_TASK_TASKNAME,
                       'task_desc': UPDATE_TASK_TASKDESC,
                       'status_id': UPDATE_TASK_STATUSID,
                       'assigned_to': UPDATE_TASK_ASSIGNEDTO,
                       "deadline": UPDATE_TASK_DEADLINE,
                       "priority_desc": UPDATE_TASK_PRIORITY_DESC}
            queries_null = {'task_name': UPDATE_NULL_TASK_TASKNAME,
                       'task_desc': UPDATE_NULL_TASK_TASKDESC,
                       'status_id': UPDATE_NULL_TASK_STATUSID,
                       'assigned_to': UPDATE_NULL_TASK_ASSIGNEDTO,
                       "deadline": UPDATE_NULL_TASK_DEADLINE,
                       "priority_desc": UPDATE_NULL_TASK_PRIORITY_DESC}
            for key in ['task_name', 'task_desc', 'status_id', 'assigned_to', "deadline", "priority_desc"]:
                if keys[key] != -100:
                    if keys[key] is None:
                        self.query(queries_null[key].format(keys['task_id']))
                    elif keys[key] is not None:
                        self.query(queries[key].format(keys[key], keys['task_id']))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.update_task[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return False

    def add_comment(self, keys):
        try:
            self.query(ADD_COMMENT.format(keys['task_id'], keys['user_id'], keys['comment_desc']))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.update_task[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return False

    def update_comment(self, keys):
        try:
            self.query(UPDATE_COMMENT.format(keys['comment_id'], keys['user_id'], keys['comment_desc']))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.update_task[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return False

    def delete_comment(self, keys):
        try:
            self.query(REMOVE_COMMENT.format(keys['comment_id']))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.update_task[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return False

    def get_task_full_info(self, keys):
        try:
            result = self.query(GET_FULL_TASK_INFO.format(keys['task_id'])).fetchone()
            info = {'task_id': result[0], 'task_name': result[1], 'task_desc': result[2],'deadline': result[3],
                    'created_at': result[4], 'priority_desc': result[5], 'assigned_to': result[6]}
            return info
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.get_task_full_info[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return None

    def get_users_from_task(self, keys):
        users = []
        try:
            result = self.query(GET_ALL_USERS_FROM_TASK.format(keys['task_id'])).fetchall()
            for res in result:
                user = res[1]
                shortcut = str(user)[:1]
                if " " in str(user):
                    shortcut = "{0}{1}".format(shortcut, str(user).split(" ")[1][:1])
                users.append({'user_id': res[0], 'name': res[1], 'shortcut': shortcut})
            return users
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.get_users_from_task[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return []

    def get_comments_from_task(self, keys):
        comments = []
        try:
            result = self.query(GET_COMMENTS_FROM_TASK.format(keys['task_id'])).fetchall()
            for res in result:
                comments.append({'comment_id': res[0], "comment_desc": res[1], "user_id": res[2], "created_at": res[3]})
            return comments
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.get_comments_from_task[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return []

    def update_user_status(self, keys):
        try:
            self.query(UPDATE_USERS_STATUS.format(keys['task_id'], keys['status_id'], keys['user_id']))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.update_user_status[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return False

    def add_user_to_project(self, keys):
        try:
            domain_id = self.query(GET_DOMAIN_ID.format(keys['domain'])).fetchone()
            self.query(ADD_USER_TO_PROJECT.format(keys['user_id'], domain_id[0], keys['project_id'], keys['assigned_to']))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.add_user_to_project[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return False

    def remove_user_from_project(self, keys):
        try:
            result = self.query(CHECK_USER_INSIDE_PROJECT.format(keys['project_id'], keys['assigned_to'])).fetchone()
            if result:
                return False
            self.query(REMOVE_USER_FROM_PROJECT.format(keys['project_id'], keys['assigned_to']))
            return True
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DBConnector.remove_user_from_project[{0}]".format(exc_tb.tb_lineno),
                               args=keys)
            return False
