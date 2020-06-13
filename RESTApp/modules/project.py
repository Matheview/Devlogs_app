from flask.views import MethodView
from flask import request, json, Response
from connectors.dbconfig import DBConnector
from modules.responses import *
from modules.gettery import *
import re
import secrets
import string


class AddProject(MethodView, Responses):

    def __init__(self):
        super(AddProject, self).__init__()
        self.db = DBConnector()
        self.data = ""
        self.keys = {}

    def get(self):
        return self.method_not_allowed("AddProject.get", 'get')

    def put(self):
        return self.method_not_allowed("AddProject.get", 'put')

    def delete(self):
        return self.method_not_allowed("AddProject.get", 'delete')

    def post(self):
        self.data = request.json
        for key in ['user_id', 'project_name', 'domain_id']:
            self.keys[key] = request.json[key]
            if not self.keys[key] or self.keys[key] is None:
                return self.response(202, success=False, msg="Key '{0}' not found".format(key))
            elif self.keys[key] == 0 or self.keys[key] == "":
                return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
            elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
        user = self.db.check_kierownik(self.keys['user_id'])
        if not user:
            return self.response(202, success=False, msg="Not found privileges as Kierownik")
        domain = self.db.query(CHECK_DOMAIN_EXIST.format(self.keys['domain_id'])).fetchone()
        if not domain:
            return self.response(202, success=False, msg="Domain not found")
        project_available = self.db.query(CHECK_PROJECT_EXIST.format(self.keys['project_name'], self.keys['domain_id'])).fetchone()
        if project_available:
            return self.response(202, success=False, msg="Project name already exist inside this domain")
        result = self.db.add_project(self.keys)
        if not result[0]:
            return self.response(202, success=False, msg="Unexpected exception: reported")
        return self.response(200, success=True, msg="Project created", project_id=result[1], project_name=self.keys['project_name'])


class AddUserToProject(MethodView, Responses):

    def __init__(self):
        super(AddUserToProject, self).__init__()
        self.db = DBConnector()
        self.data = ""
        self.keys = {}

    def get(self):
        return self.method_not_allowed("AddUserToProject.get", 'get')

    def put(self):
        return self.method_not_allowed("AddUserToProject.put", 'put')

    def delete(self):
        return self.method_not_allowed("AddUserToProject.delete", 'delete')

    def post(self):
        try:
            self.data = request.json
            for key in ['user_id', 'project_id', 'domain', 'assigned_to']:
                self.keys[key] = request.json[key]
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            user = self.db.check_kierownik(self.keys['user_id'])
            if not user:
                return self.response(202, success=False, msg="Not found privileges as Kierownik")
            domain = self.db.query(CHECK_DOMAIN_EXIST_BY_NAME.format(self.keys['domain'])).fetchone()
            if not domain:
                return self.response(202, success=False, msg="Domain not found")
            project_info = self.db.get_project_info(self.keys)
            if not project_info[0]:
                return self.response(202, success=False, msg="Project not found")
            is_user = self.db.check_user_by_id(self.keys['assigned_to'])
            if not is_user:
                return self.response(202, success=False, msg="User not found")
            user = self.db.query(CHECK_USER_ADDED_TO_PROJECT.format(self.keys['assigned_to'], self.keys['project_id'])).fetchone()
            if user:
                return self.response(202, success=False, msg="User already added to project")
            result = self.db.add_user_to_project(self.keys)
            if not result:
                return self.response(202, success=False, msg="Unexpected exception: reported")
            return self.response(200, success=True, msg="User added to project")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="AddUserToProject.post[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")


class RemoveUserFromProject(MethodView, Responses):

    def __init__(self):
        super(RemoveUserFromProject, self).__init__()
        self.db = DBConnector()
        self.data = ""
        self.keys = {}

    def get(self):
        return self.method_not_allowed("RemoveUserFromProject.get", 'get')

    def put(self):
        return self.method_not_allowed("RemoveUserFromProject.put", 'put')

    def post(self):
        return self.method_not_allowed("RemoveUserFromProject.post", 'post')

    def delete(self):
        try:
            self.data = request.json
            for key in ['user_id', 'project_id', 'assigned_to']:
                self.keys[key] = request.json[key]
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            user = self.db.check_kierownik(self.keys['user_id'])
            if not user:
                return self.response(202, success=False, msg="Not found privileges as Kierownik")
            project_info = self.db.get_project_info(self.keys)
            if not project_info[0]:
                return self.response(202, success=False, msg="Project not found")
            is_user = self.db.check_user_by_id(self.keys['assigned_to'])
            if not is_user:
                return self.response(202, success=False, msg="User not found")
            user = self.db.query(
                CHECK_USER_ADDED_TO_PROJECT.format(self.keys['assigned_to'], self.keys['project_id'])).fetchone()
            if not user:
                return self.response(202, success=False, msg="User not found inside project")
            result = self.db.remove_user_from_project(self.keys)
            if not result:
                return self.response(202, success=False, msg="User cannot be removed from project, because he have assigned tasks")
            return self.response(200, success=True, msg="User removed from project")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="AddUserToProject.post[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")
