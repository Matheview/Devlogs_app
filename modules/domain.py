from flask.views import MethodView
from flask import request, json, Response
from connectors.dbconfig import DBConnector
from modules.responses import *


class DomainsRegister(MethodView, Responses):

    def __init__(self):
        super(DomainsRegister, self).__init__()
        self.db = DBConnector()

    def get(self):
        return self.method_not_allowed('DomainsRegister', 'get')

    def put(self):
        return self.method_not_allowed('DomainsRegister', 'put')

    def delete(self):
        return self.method_not_allowed('DomainsRegister', 'delete')

    def post(self):
        self.logs.save_msg("Method not allowed", localisation="Register.post", args=self.keys)
        return self.response(405, success=False, msg="Method not allowed: saved")


