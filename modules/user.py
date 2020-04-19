from flask.views import MethodView
from flask import request, json, Response
from connectors.dbconfig import DBConnector
from modules.responses import *
from modules.gettery import *
import re
import secrets
import string


class GeneratePassword(MethodView, Responses):

    def __init__(self):
        super(GeneratePassword, self).__init__()
        self.db = DBConnector()

    def get(self):
        return self.method_not_allowed("GenerateAdmin.get", 'get')

    def put(self):
        return self.method_not_allowed("GenerateAdmin.get", 'put')

    def delete(self):
        return self.method_not_allowed("GenerateAdmin.get", 'delete')

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


class Register(MethodView, Responses):

    def __init__(self):
        super(Register, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.data = ""
        self.token = ""

    def get(self):
        return self.method_not_allowed("GenerateAdmin.get", 'get')

    def put(self):
        return self.method_not_allowed("GenerateAdmin.get", 'put')

    def delete(self):
        return self.method_not_allowed("GenerateAdmin.get", 'delete')

    def post(self):
        try:
            # if request.headers.get('token') == SECRET_HASH:
                self.data = request.json
                for key in ['email', 'password', 'domain', 'privilege', 'name']:
                    self.keys[key] = request.json[key]
                    if not self.keys[key] or self.keys[key] is None:
                        return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                    elif len(self.keys[key]) == 0 or self.keys[key] == "":
                        return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                    elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                        return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
                self.keys['privilege'] = self.keys['privilege'].capitalize()
                privileges = self.db.check_privileges(self.keys['privilege'])
                if not privileges[0]:
                    self.logs.save_msg("Privilege failed: self.keys['privilege']", localisation="Register.post", args=self.keys)
                    return self.response(202, success=False, msg="Email validation error")
                if not re.search(EMAIL_REGEX, self.keys['email']):
                    self.logs.save_msg("Email or password validation error", localisation="Register.post", args=self.keys)
                    return self.response(202, success=False, msg="Email validation error")
                account = self.create_user(self.keys)
                if not account[0]:
                    return self.response(500, success=False, msg="Wystapil blad podczas tworzenia konta")
                return self.response(200, success=True, msg="Utworzono konto")
            # else:
            #     self.logs.save_msg("Permission denied", localisation="Register.post[{0}]".format(exc_tb.tb_lineno),
            #                        args=request.headers.get('token'))
            #     return self.response(403, success=False, msg="Permission denied")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="Register.post[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")


class Login(MethodView, Responses):

    def __init__(self):
        super(Login, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.getter = GettersUsers()
        self.hash = self.db.get_token()
        self.data = ""
        self.token = ""

    def get(self):
        return self.method_not_allowed('Login.get', 'get')

    def put(self):
        return self.method_not_allowed('Login.put', 'put')

    def delete(self):
        return self.method_not_allowed('Login.delete', 'delete')

    def post(self):
        try:
            self.data = request.json
            for key in ['email', 'password', 'domain']:
                # self.keys[key] = request.form.get(key)
                self.keys[key] = request.json[key]
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
        user = self.db.get_name(query[2]['user_id'])
        return self.response(query[1], self.token, success=query[0], msg="Zalogowano",
                             privilege=self.getter.get_privileges(query[2]['user_id']), user_id=user[1], username=user[2])


# tutaj potrzebny jest token
class GenerateAdmin(MethodView, Responses):

    def __init__(self):
        super(GenerateAdmin, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.data = ""
        self.token = ""

    def get(self):
        return self.method_not_allowed("GenerateAdmin.get", 'get')

    def put(self):
        return self.method_not_allowed("GenerateAdmin.get", 'put')

    def delete(self):
        return self.method_not_allowed("GenerateAdmin.get", 'delete')

    def post(self):
        if request.headers.get('token') == SECRET_HASH:
            self.data = request.json
            for key in ['email', 'password', 'domain', 'name']:
                request.form.get(key)
                self.keys[key] = request.json[key]
            account = self.create_admin(self.keys)
            if not account[0]:
                return self.response(500, self.token, success=False, msg="Wystapil blad podczas tworzenia konta")
            if not self.create_domain(self.keys['domain'], account[1]):
                return self.response(500, self.token, success=False, msg="Wystapil blad podczas tworzenia domeny")
            return self.response(200, success=True, msg="Admin created")
        else:
            self.logs.save_msg("Permission denied", localisation="GenerateAdmin.post[{0}]".format(exc_tb.tb_lineno), args=request.headers.get('token'))
            return self.response(403, success=False, msg="Permission denied")



