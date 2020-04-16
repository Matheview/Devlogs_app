from flask.views import MethodView
from flask import request, json, Response
from connectors.dbconfig import DBConnector
from modules.responses import *
import re


class Register(MethodView, Responses):

    def __init__(self):
        super(Register, self).__init__()
        self.db = DBConnector()

    def get(self):
        return self.method_not_allowed('Register.get', 'get')

    def put(self):
        return self.method_not_allowed('Register.put', 'put')

    def delete(self):
        return self.method_not_allowed('Register.delete', 'delete')

    def post(self):
        self.logs.save_msg("Method not allowed", localisation="Register.post", args=self.keys)
        return self.response(405, success=False, msg="Method not allowed: saved")


class Login(MethodView, Responses):

    def __init__(self):
        super(Login, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.hash = self.db.get_token()
        self.data = ""
        self.token = ""

    def get_data(self, request):
        try:
            self.data = request.data
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="Login.get_data[{0}]".format(exc_tb.tb_lineno), args=self.data)
            self.data = None

    def get(self):
        return self.method_not_allowed('Login.get', 'get')

    def put(self):
        return self.method_not_allowed('Login.put', 'put')

    def delete(self):
        return self.method_not_allowed('Login.delete', 'delete')

    def post(self):
        try:
            self.data = request.data
            for key in ['email', 'password', 'domain']:
                self.keys[key] = request.form.get(key)
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif len(self.keys[key]) == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            if not re.search(EMAIL_REGEX, self.keys['email']):
                self.logs.save_msg("Email or password validation error", localisation="Login.post", args=self.data)
                return self.response(202, success=False, msg="Email validation error")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="Login.post[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")
        query = self.db.login_user(self.keys)
        if not query[0]:
            self.logs.save_msg("Login error", localisation="Login.post", args=self.keys)
            return self.response(query[1], success=query[0], msg=query[2])
        self.token = "{0}##{1}".format(query[2]['user_id'], self.hash)
        self.db.save_logging(query[2]['user_id'], self.token)
        return self.response(query[1], self.token, success=query[0], msg="Zalogowano")


# tutaj potrzebny jest token
class GenerateAdmin(MethodView, Responses):

    def __init__(self):
        super(GenerateAdmin, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.hash = self.get_hash()

    def get(self):
        return self.method_not_allowed("GenerateAdmin.get", 'get')

    def put(self):
        return self.method_not_allowed("GenerateAdmin.get", 'put')

    def delete(self):
        return self.method_not_allowed("GenerateAdmin.get", 'delete')

    def post(self):
        if request.headers.get('token') == SECRET_HASH:
            for key in ['email', 'password', 'domain']:
                self.keys[key] = request.form.get(key)
            account = self.create_admin(self.keys)
            if not self.create_admin(self.keys):
                return self.response(500, self.token, success=False, msg="Wystapil blad podczas tworzenia konta")
            if not self.create_domain(self.keys['domain']):
                return self.response(500, self.token, success=False, msg="Wystapil blad podczas tworzenia domeny")
            return Response(status=403)
        return Response(status=403)
