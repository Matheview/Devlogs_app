from flask.views import MethodView
from flask import request, json, Response, render_template
from connectors.dbconfig import DBConnector
from modules.responses import *
import re
from datetime import datetime


class TasksActions(MethodView, Responses):

    def __init__(self):
        super(TasksActions, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.data = ""
        self.token = ""

    def get(self):
        return self.method_not_allowed("TasksActions.get", 'get')

    def post(self):
        try:
            for key in ['domain', 'project_id', 'task_name', 'creator_id', 'status_id']:  # , 'user_id']:
                self.keys[key] = request.json[key]
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            try:
                self.keys['assigned_to'] = request.json['assigned_to']
            except:
                self.keys['assigned_to'] = None
            domain = self.db.query(CHECK_DOMAIN_AVAILABLE.format(self.keys['domain'])).fetchone()
            if not domain:
                return self.response(202, success=False, msg="Domain not found")
            project_available = self.db.query(
                GET_PROJECT_FROM_DOMAIN_MIN.format(self.keys['project_id'], self.keys['domain'])).fetchone()
            if not project_available:
                return self.response(202, success=False, msg="Project not found inside this domain")
            user = self.db.check_kierownik(self.keys['creator_id'])
            if not user:
                return self.response(202, success=False, msg="Not found privileges as Kierownik")
            task_exists = self.db.query(
                CHECK_TASK_EXISTS.format(self.keys['task_id'])).fetchone()
            if not task_exists:
                return self.response(202, success=False, msg="Task not exists in this project")
            result = self.db.create_task(self.keys)
            if not result:
                return self.response(202, success=False, msg="Unexpected exception: reported")
            return self.response(200, success=True, msg="Status created", task_id=result,
                                 task_fullname="#{0} {1}".format(result, self.keys['task_name']),
                                 task_title=self.keys['task_name'])

        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="TasksActions.post[{0}]".format(exc_tb.tb_lineno), args=self.keys)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def put(self):
        try:
            for key in ['domain', 'project_id', 'creator_id', "task_id"]:  # , 'user_id']:
                self.keys[key] = request.json[key]
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            for key in ['task_name', 'task_desc', 'status_id', 'assigned_to', "deadline", "priority_desc"]:
                try:
                    self.keys[key] = request.json[key]
                    if key == "task_name" and self.keys[key] is None:
                        return self.response(202, success=False, msg="Task name cannot be null")
                    if key == "status_id" and self.keys[key] is None:
                        return self.response(202, success=False, msg="Status cannot be null")
                except:
                    self.keys[key] = -100
            domain = self.db.query(CHECK_DOMAIN_AVAILABLE.format(self.keys['domain'])).fetchone()
            if not domain:
                return self.response(202, success=False, msg="Domain not found")
            project_available = self.db.query(
                GET_PROJECT_FROM_DOMAIN_MIN.format(self.keys['project_id'], self.keys['domain'])).fetchone()
            if not project_available:
                return self.response(202, success=False, msg="Project not found inside this domain")
            user = self.db.check_kierownik(self.keys['creator_id'])
            if not user:
                return self.response(202, success=False, msg="Not found privileges as Kierownik")
            task_exists = self.db.query(
                CHECK_TASK_EXISTS.format(self.keys['task_id'])).fetchone()
            if not task_exists:
                return self.response(202, success=False, msg="Task not exists in this project")
            print(self.keys['assigned_to'])
            if self.keys['assigned_to'] != -100 and self.keys['assigned_to'] is not None:
                is_user = self.db.check_user_by_id(self.keys['assigned_to'])
                if not is_user:
                    return self.response(202, success=False, msg="User not found")
            if self.keys['status_id'] != -100:
                status_exists = self.db.query(
                    CHECK_STATUS_EXISTS.format(self.keys['status_id'], self.keys['project_id'])).fetchone()
                if not status_exists:
                    return self.response(202, success=False, msg="Status id not exists in this project")
            result = self.db.update_task(self.keys)
            if not result:
                return self.response(202, success=False, msg="Unexpected exception: reported")
            return self.response(200, success=True, msg="Task updated")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="TasksActions.put[{0}]".format(exc_tb.tb_lineno), args=self.keys)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def delete(self):
        try:
            for key in ['project_id', 'task_id', 'creator_id', 'domain']:  # , 'user_id']:
                self.keys[key] = request.json[key]
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            domain = self.db.query(CHECK_DOMAIN_AVAILABLE.format(self.keys['domain'])).fetchone()
            if not domain:
                return self.response(202, success=False, msg="Domain not found")
            project_available = self.db.query(
                GET_PROJECT_FROM_DOMAIN_MIN.format(self.keys['project_id'], self.keys['domain'])).fetchone()
            if not project_available:
                return self.response(202, success=False, msg="Project not found inside this domain")
            user = self.db.check_kierownik(self.keys['creator_id'])
            if not user:
                return self.response(202, success=False, msg="Not found privileges as Kierownik")
            task_exists = self.db.query(
                CHECK_TASK_EXISTS.format(self.keys['task_id'])).fetchone()
            if not task_exists:
                return self.response(202, success=False, msg="Task not exists in this project")
            result = self.db.delete_task(self.keys)
            if not result:
                return self.response(202, success=False, msg="Unexpected exception: reported")
            return self.response(200, success=True, msg="Task removed")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="TasksActions.post[{0}]".format(exc_tb.tb_lineno), args=self.keys)
            return self.response(202, success=False, msg="Unexpected exception: reported")


class StatusActions(MethodView, Responses):

    def __init__(self):
        super(StatusActions, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.data = ""
        self.token = ""

    def get(self):
        return self.method_not_allowed("StatusActions.get", 'get')

    def post(self):
        try:
            for key in ['domain', 'project_id', 'status_desc', 'creator_id']:  # , 'user_id']:
                self.keys[key] = request.json[key]
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            domain = self.db.query(CHECK_DOMAIN_AVAILABLE.format(self.keys['domain'])).fetchone()
            if not domain:
                return self.response(202, success=False, msg="Domain not found")
            project_available = self.db.query(GET_PROJECT_FROM_DOMAIN_MIN.format(self.keys['project_id'], self.keys['domain'])).fetchone()
            if not project_available:
                return self.response(202, success=False, msg="Project not found inside this domain")
            user = self.db.check_kierownik(self.keys['creator_id'])
            if not user:
                return self.response(202, success=False, msg="Not found privileges as Kierownik")
            status_available = self.db.query(
                GET_STATUS_AVAILABLE.format(self.keys['project_id'], self.keys['status_desc'])).fetchone()
            if status_available:
                return self.response(202, success=False, msg="Status description already exists")
            status = self.db.create_status(self.keys)
            if status == 0:
                return self.response(202, success=False, msg="Unexpected exception: reported")
            return self.response(200, success=True, msg="Status created", status_id=status)
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="StatusActions.post[{0}]".format(exc_tb.tb_lineno), args=self.keys)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def put(self):
        try:
            for key in ['domain', 'project_id', 'status_desc', 'creator_id', 'status_id']:  # , 'user_id']:
                self.keys[key] = request.json[key]
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            domain = self.db.query(CHECK_DOMAIN_AVAILABLE.format(self.keys['domain'])).fetchone()
            if not domain:
                return self.response(202, success=False, msg="Domain not found")
            project_available = self.db.query(GET_PROJECT_FROM_DOMAIN_MIN.format(self.keys['project_id'], self.keys['domain'])).fetchone()
            if not project_available:
                return self.response(202, success=False, msg="Project not found inside this domain")
            user = self.db.check_kierownik(self.keys['creator_id'])
            if not user:
                return self.response(202, success=False, msg="Not found privileges as Kierownik")
            status_available = self.db.query(GET_STATUS_AVAILABLE.format(self.keys['project_id'], self.keys['status_desc'])).fetchone()
            if status_available:
                if status_available[0] == self.keys['status_id']:
                    return self.response(202, success=False, msg="Status description not changed")
                return self.response(202, success=False, msg="Status description already exists")
            status_exists = self.db.query(
                CHECK_STATUS_EXISTS.format(self.keys['status_id'], self.keys['project_id'])).fetchone()
            if not status_exists:
                return self.response(202, success=False, msg="Status id not exists in this project")
            status = self.db.update_status(self.keys)
            if not status:
                return self.response(202, success=False, msg="Unexpected exception: reported")
            return self.response(200, success=True, msg="Status updated")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="StatusActions.put[{0}]".format(exc_tb.tb_lineno), args=self.keys)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def delete(self):
        try:
            for key in ['domain', 'project_id', 'creator_id', 'status_id']:  # , 'user_id']:
                self.keys[key] = request.json[key]
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            domain = self.db.query(CHECK_DOMAIN_AVAILABLE.format(self.keys['domain'])).fetchone()
            if not domain:
                return self.response(202, success=False, msg="Domain not found")
            project_available = self.db.query(GET_PROJECT_FROM_DOMAIN_MIN.format(self.keys['project_id'], self.keys['domain'])).fetchone()
            if not project_available:
                return self.response(202, success=False, msg="Project not found inside this domain")
            user = self.db.check_kierownik(self.keys['creator_id'])
            if not user:
                return self.response(202, success=False, msg="Not found privileges as Kierownik")
            status_available = self.db.query(
                CHECK_CAN_REMOVE_STATUS.format(self.keys['project_id'], self.keys['status_id'])).fetchone()
            if status_available:
                return self.response(202, success=False,
                                     msg="Cannot remove status with tasks inside - move your tasks to another status to continue")
            removed = self.db.remove_status(self.keys)
            if not removed:
                return self.response(202, success=False, msg="Unexpected exception: reported")
            return self.response(200, success=True, msg="Status removed")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="StatusActions.put[{0}]".format(exc_tb.tb_lineno), args=self.keys)
            return self.response(202, success=False, msg="Unexpected exception: reported")


class CommentActions(MethodView, Responses):

    def __init__(self):
        super(CommentActions, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.data = ""
        self.token = ""

    def get(self):
        return self.method_not_allowed("CommentActions.get", 'get')

    def post(self):
        try:
            for key in ['user_id', 'task_id', 'comment_desc']:
                self.keys[key] = request.json[key]
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            is_user = self.db.check_user_by_id(self.keys['user_id'])
            if not is_user:
                return self.response(202, success=False, msg="User not found")
            task_exists = self.db.query(
                CHECK_TASK_EXISTS.format(self.keys['task_id'])).fetchone()
            if not task_exists:
                return self.response(202, success=False, msg="Task not exists in this project")
            removed = self.db.add_comment(self.keys)
            if not removed:
                return self.response(202, success=False, msg="Unexpected exception: reported")
            return self.response(200, success=True, msg="Comment added")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="CommentActions.post[{0}]".format(exc_tb.tb_lineno), args=self.keys)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def put(self):
        try:
            for key in ['task_id', 'comment_id', 'user_id', 'comment_desc']:
                self.keys[key] = request.json[key]
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            is_user = self.db.check_user_by_id(self.keys['user_id'])
            if not is_user:
                return self.response(202, success=False, msg="User not found")
            task_exists = self.db.query(
                CHECK_TASK_EXISTS.format(self.keys['task_id'])).fetchone()
            if not task_exists:
                return self.response(202, success=False, msg="Task not exists in this project")
            task_is_user = self.db.query(
                CHECK_COMMENT_IS_USER.format(self.keys['comment_id'], self.keys['user_id'])).fetchone()
            if not task_is_user:
                return self.response(202, success=False, msg="Comment not allow to user")
            removed = self.db.update_comment(self.keys)
            if not removed:
                return self.response(202, success=False, msg="Unexpected exception: reported")
            return self.response(200, success=True, msg="Comment updated")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="CommentActions.put[{0}]".format(exc_tb.tb_lineno), args=self.keys)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def delete(self):
        try:
            for key in ['task_id', 'comment_id', 'user_id']:
                self.keys[key] = request.json[key]
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            is_user = self.db.check_user_by_id(self.keys['user_id'])
            if not is_user:
                return self.response(202, success=False, msg="User not found")
            task_exists = self.db.query(
                CHECK_TASK_EXISTS.format(self.keys['task_id'])).fetchone()
            if not task_exists:
                return self.response(202, success=False, msg="Task not exists in this project")
            task_is_user = self.db.query(
                CHECK_COMMENT_IS_USER.format(self.keys['comment_id'], self.keys['user_id'])).fetchone()
            if not task_is_user:
                return self.response(202, success=False, msg="Comment not allow to user")
            removed = self.db.delete_comment(self.keys)
            if not removed:
                return self.response(202, success=False, msg="Unexpected exception: reported")
            return self.response(200, success=True, msg="Comment deleted")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="CommentActions.delete[{0}]".format(exc_tb.tb_lineno), args=self.keys)
            return self.response(202, success=False, msg="Unexpected exception: reported")


class GetFullTaskInfo(MethodView, Responses):

    def __init__(self):
        super(GetFullTaskInfo, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.data = ""
        self.token = ""
        self.users = []
        self.comment = []
        self.info = {}

    def get(self):
        try:
            for key in ['task_id']:
                self.keys[key] = request.args.get(key)
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            task_exists = self.db.query(
                CHECK_TASK_EXISTS.format(self.keys['task_id'])).fetchone()
            if not task_exists:
                return self.response(202, success=False, msg="Task not exists in this project")
            self.info = self.db.get_task_full_info(self.keys)
            self.users = self.db.get_users_from_task(self.keys)
            self.comment = self.db.get_comments_from_task(self.keys)
            return self.response(200, success=True, msg="Task found", task_id=self.info['task_id'], task_name=self.info['task_name'],
                                 task_desc=self.info['task_desc'], deadline=self.info['deadline'], created_at=self.info['created_at'],
                                 priority_desc=self.info['priority_desc'], assigned_to=self.info['assigned_to'],
                                 users=self.users, comments=self.comment)
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="GetFullTaskInfo.get[{0}]".format(exc_tb.tb_lineno), args=self.keys)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def post(self):
        return self.method_not_allowed("GetFullTaskInfo.post", 'post')

    def put(self):
        return self.method_not_allowed("GetFullTaskInfo.put", 'put')

    def delete(self):
        return self.method_not_allowed("GetFullTaskInfo.delete", 'delete')


class UpdateStatusUser(MethodView, Responses):

    def __init__(self):
        super(UpdateStatusUser, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.data = ""
        self.token = ""

    def get(self):
        return self.method_not_allowed("UpdateStatusUser.get", 'get')

    def post(self):
        try:
            for key in ['task_id', 'status_id', 'user_id']:
                self.keys[key] = request.args.get(key)
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            task_exists = self.db.query(
                CHECK_TASK_EXISTS.format(self.keys['task_id'])).fetchone()
            if not task_exists:
                return self.response(202, success=False, msg="Task not exists in this project")
            task_is_user = self.db.query(
                CHECK_COMMENT_IS_USER.format(self.keys['comment_id'], self.keys['user_id'])).fetchone()
            if not task_is_user:
                return self.response(202, success=False, msg="Comment not allow to user")
            result = self.db.update_user_status(self.keys)
            if not result:
                return self.response(202, success=False, msg="Unexpected exception: reported")
            return self.response(200, success=True, msg="Comment updated")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="GetFullTaskInfo.get[{0}]".format(exc_tb.tb_lineno), args=self.keys)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def put(self):
        return self.method_not_allowed("UpdateStatusUser.put", 'put')

    def delete(self):
        return self.method_not_allowed("UpdateStatusUser.delete", 'delete')