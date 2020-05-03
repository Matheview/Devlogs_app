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
                for key in ['email', 'password', 'domain', 'privilege', 'username', 'user_id']:
                    self.keys[key] = request.json[key]
                    if not self.keys[key] or self.keys[key] is None:
                        return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                    elif self.keys[key] == 0 or self.keys[key] == "":
                        return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                    elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                        return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
                is_user = self.db.check_admin(self.keys['user_id'])
                if not is_user:
                    return self.response(403, success=False, msg="Permission denied")
                self.keys['privilege'] = self.keys['privilege'].capitalize()
                privileges = self.db.check_privileges(self.keys['privilege'])
                if not privileges[0]:
                    self.logs.save_msg("Privilege failed: self.keys['privilege']", localisation="Register.post", args=self.keys)
                    return self.response(202, success=False, msg="Email validation error")
                if not re.search(EMAIL_REGEX, self.keys['email']):
                    self.logs.save_msg("Email or password validation error", localisation="Register.post", args=self.keys)
                    return self.response(202, success=False, msg="Email validation error")
                user_exists = self.db.check_user_exists(self.keys['email'])
                if not user_exists:
                    return self.response(202, success=False, msg="Email already exists")
                domain = self.db.query(CHECK_DOMAIN_AVAILABLE.format(self.keys['domain'])).fetchone()
                if not domain:
                    return self.response(202, success=False, msg="Domain not found")
                account = self.db.create_user(self.keys)
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
                             privilege=self.getter.get_privileges(query[2]['user_id']), user_id=user[1], username=user[2],
                             domain=self.keys['domain'])


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
            for key in ['user_id', 'privilege']:
                #request.form.get(key)
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


class UpdatePermission(MethodView, Responses):

    def __init__(self):
        super(UpdatePermission, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.data = ""
        self.token = ""

    def get(self):
        return self.method_not_allowed("UpdatePermission.get", 'get')

    def put(self):
        try:
            self.data = request.json
            for key in ['user_id', 'privilege', 'domain', 'granted_to']:
                # request.form.get(key)
                self.keys[key] = request.json[key]
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            is_admin = self.db.check_admin(self.keys['user_id'])
            if not is_admin:
                self.logs.save_msg("Permission failed: self.keys['privilege']", localisation="UpdatePermission.put",
                                   args=self.keys)
                return self.response(403, success=False, msg="Permission denied")
            self.keys['privilege'] = self.keys['privilege'].capitalize()
            privileges = self.db.check_privileges(self.keys['privilege'])
            if not privileges[0]:
                self.logs.save_msg("Privilege failed: self.keys['privilege']", localisation="UpdatePermission.put",
                                   args=self.keys)
                return self.response(202, success=False, msg="Privileges not found")
            domain = self.db.query(CHECK_DOMAIN_AVAILABLE.format(self.keys['domain'])).fetchone()
            if not domain:
                return self.response(202, success=False, msg="Domain not found")
            is_user = self.db.check_user_by_id(self.keys['user_id'])
            if not is_user:
                return self.response(202, success=False, msg="User not found")
            self.db.update_permission(self.keys)
            return self.response(200, success=True, msg="Privileges granted")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="UpdatePermission.put[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def delete(self):
        try:
            self.data = request.json
            for key in ['user_id', 'domain', 'granted_to']:
                # request.form.get(key)
                self.keys[key] = request.json[key]
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            is_admin = self.db.check_admin(self.keys['user_id'])
            if not is_admin:
                self.logs.save_msg("Permission failed: self.keys['privilege']", localisation="UpdatePermission.delete",
                                   args=self.keys)
                return self.response(403, success=False, msg="Permission denied")
            domain = self.db.query(CHECK_DOMAIN_AVAILABLE.format(self.keys['domain'])).fetchone()
            if not domain:
                return self.response(202, success=False, msg="Domain not found")
            is_user = self.db.check_user_by_id(self.keys['granted_to'])
            if not is_user:
                return self.response(202, success=False, msg="User not found")
            self.db.remove_permission(self.keys)
            return self.response(200, success=True, msg="Privileges removed")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="UpdatePermission.delete[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def post(self):
        return self.method_not_allowed("UpdatePermission.post", 'post')


class ChangePasswd(MethodView, Responses):

    def __init__(self):
        super(ChangePasswd, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.hash = self.db.get_token()
        self.data = ""
        self.token = ""

    def get(self):
        return self.method_not_allowed("ChangePasswd.get", 'get')

    def put(self):
        return self.method_not_allowed("ChangePasswd.put", 'put')

    def delete(self):
        return self.method_not_allowed("ChangePasswd.delete", 'delete')

    def post(self):
        try:
            self.data = request.json
            for key in ['email']:
                self.keys[key] = request.json[key]
        except Exception as e:
            return self.response(202, self.token, success=False, msg="Key errors")
        account = self.db.check_user_exists_change_passwd(self.keys['email'])
        if not account[0]:
            return self.response(202, self.token, success=False, msg="Email not found")
        self.token = "{0}##{1}".format(account[1], self.hash)
        self.db.save_logging(account[1], self.token)
        return self.response(200, success=True, msg="Link generated", link="http://ssh-vps.nazwa.pl:4742/changepassword?token={0}".format(self.token.replace("##", "@")))


class ChangePasswdLink(MethodView, Responses):

    def __init__(self):
        super(ChangePasswdLink, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.data = ""
        self.token = ""

    def get(self):
        self.data = request.args.get('token')
        print(self.data)
        if not self.data:
            return render_template("notexists.html", msg="Not found param token")
        if "@" not in self.data:
            return render_template("notexists.html", msg="Invalid token ##")
        token = self.db.checktoken(self.data.replace("@", "##"))
        if token:
            return render_template("passchanger.html", token=self.data.replace("##", "@"), hidden='''style="display:none"''')
        return render_template("notexists.html", msg="Invalid token")

    def put(self):
        return self.method_not_allowed("ChangePasswd.put", 'put')

    def delete(self):
        return self.method_not_allowed("ChangePasswd.delete", 'delete')

    def post(self):
        try:
            result = self.db.change_password(request.form['token'], request.form['new-password'])
            if result[0]:
                self.db.save_logging(request.form['token'].split("@")[0], request.form['token'])
                return render_template("passchanger.html", msg=result[1], style="message-panel")
            return render_template("passchanger.html", msg=result[1], style="message-panel-error")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="ChangePasswdLink.post[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return render_template("passchanger.html", msg="Unexpected exception: reported", style="message-panel-error")


