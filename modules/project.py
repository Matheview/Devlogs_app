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

    def get(self):
        return self.method_not_allowed("AddProject.get", 'get')

    def put(self):
        return self.method_not_allowed("AddProject.get", 'put')

    def delete(self):
        return self.method_not_allowed("AddProject.get", 'delete')

    def post(self):
        if not request.json['user_id']:
            return self.response(202, success=False, msg="Key 'user_id' not found")
        user = self.db.check_user(request.json['user_id'])
        if not user:
            return self.response(202, success=False, msg="User not found")
        alphabet = string.ascii_letters + string.digits
        password = str(''.join(secrets.choice(alphabet) for i in range(12)))
        self.db.generate_password(password, request.json['user_id'])
        return self.response(200, success=True, msg="Password generated", password=password)