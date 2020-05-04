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