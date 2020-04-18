from flask.views import MethodView
from flask import request, json, Response
from connectors.dbconfig import DBConnector
from modules.responses import *


class DomainsRegister(MethodView, Responses):

    def __init__(self):
        super(DomainsRegister, self).__init__()
        self.db = DBConnector()
        self.data = {}
        self.keys = {}

    def get(self):
        return self.method_not_allowed('DomainsRegister', 'get')

    def put(self):
        return self.method_not_allowed('DomainsRegister', 'put')

    def delete(self):
        return self.method_not_allowed('DomainsRegister', 'delete')

    def post(self):
        try:
            self.data = request.json
            for key in ['domain', 'project', 'kierownik', 'user_id']:
                self.keys[key] = request.json[key]
            if not self.keys['user_id']:
                return self.response(202, success=False, msg="Key 'user_id' required for check admin privileges")
            elif self.keys['domain'] and not self.keys['project'] and not self.keys['kierownik']:
                if not self.check_user(self.keys['user_id']):
                    return self.response(403, success=False, msg="Permission denied")
                domain = self.db.create_domain(self.keys['domain'])
                if not domain[0]:
                    return self.response(409, success=False, msg="Domain already exists")
                return self.response(200, success=True, msg="Domain created")
            elif self.keys['domain'] and self.keys['project'] and self.keys['kierownik']:
                if not self.check_user(self.keys['user_id']):
                    return self.response(403, success=False, msg="Permission denied")
                if not self.db.check_kierownik(self.keys['kierownik']):
                    return self.response(409, success=False, msg="User have not properly privileges")
                if not self.db.add_kierownik_to_project(self.keys['domain'], self.keys['kierownik']):
                    return self.response(409, success=False, msg="User not ")
                domain = self.db.create_domain(self.keys['domain'])
                self.db.add_kierownik_to_project(domain[1], self.keys['user_id'])
                return self.response(200, success=True, msg="Kierownik added to project")
            else:
                if not self.keys['domain']:
                    return self.response(202, success=False, msg="Key 'domain' required")
                elif self.keys['domain'] and not self.keys['project'] and self.keys['kierownik']:
                    return self.response(202, success=False, msg="When key 'kierownik' added key 'project' required")
                elif self.keys['domain'] and self.keys['project'] and not self.keys['kierownik']:
                    return self.response(202, success=False, msg="When key 'project' added key 'kierownik' required")
            self.logs.save_msg("Method not allowed", localisation="DomainsRegister.post", args=self.keys)
            return self.response(403, success=False, msg="Unexpected sequence")


