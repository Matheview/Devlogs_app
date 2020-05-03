# coding=utf-8
from flask.views import MethodView
from flask import request, json, Response, render_template
from connectors.dbconfig import DBConnector
from modules.responses import *
import re
from datetime import datetime


class GettersDomains(MethodView, Responses):

    def __init__(self):
        super(GettersDomains, self).__init__()
        self.keys = []
        self.db = DBConnector()
        self.data = {}
        self.token = ""

    def get(self):
        try:
            if request.data != b'':
            # if request.headers.get('token') == SECRET_HASH:
                self.data['user_id'] = request.json['user_id']
            else:
                self.data['user_id'] = request.args.get('user_id')
            domains = self.db.query(GET_DOMAINS.format(self.data['user_id'])).fetchall()
            if domains is None or len(domains) == 0:
                return self.response(200, success=True, msg="Not found domains", domains=[])
            for domain in domains:
                self.keys.append({"id": domain[0], "domain": domain[1]})
            return self.response(200, success=True, msg="Domains({0})".format(len(domains)), domains=self.keys)
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="GettersDomains.get[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def post(self):
        return self.method_not_allowed("GettersDomains.post", 'post')

    def put(self):
        return self.method_not_allowed("GettersDomains.put", 'put')

    def delete(self):
        return self.method_not_allowed("GettersDomains.delete", 'delete')


class GetProjects(MethodView, Responses):

    def __init__(self):
        super(GetProjects, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.data = ""
        self.token = ""

    def get(self):
        try:
            if request.data != b'':
                self.data['user_id'] = request.json['user_id']
            else:
                self.data['user_id'] = request.args.get('user_id')
            result = self.db.get_projects_plus_user_counts(self.data)
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="GettersDomains.get[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def post(self):
        return self.method_not_allowed("GetProjects.post", 'post')

    def put(self):
        return self.method_not_allowed("GetProjects.put", 'put')

    def delete(self):
        return self.method_not_allowed("GetProjects.delete", 'delete')


class GettersProjects(MethodView, Responses):

    def __init__(self):
        super(GettersProjects, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.data = ""
        self.token = ""
        self.arr = []

    def get(self):
        self.data = request.args.get('user_id')
        if not self.data:
            self.logs.save_msg("Key 'user_id' not found", localisation="GettersProjects.get[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Key 'user_id' not found")
        result = self.db.get_projects_users(self.data)
        if not result[0]:
            self.logs.save_msg("Nieoczekiwany bład DB", localisation="GettersProjects.get[]", args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")
        if result[1][0] == 0:
            return self.response(200, success=True, msg="Projects(0)".format(len(result[1])), projects=[])
        else:
            for i in result[1]:
                self.arr.append({'user_count': i[0], 'project_id': i[1], 'project_name': i[2], 'domain_name': i[3], 'privilege': i[4]})
            return self.response(200, success=True, msg="Projects({0})".format(len(result[1])), projects=self.arr)

    def post(self):
        return self.method_not_allowed("GettersProjects.post", 'post')

    def put(self):
        return self.method_not_allowed("GettersProjects.put", 'put')

    def delete(self):
        return self.method_not_allowed("GettersProjects.delete", 'delete')


class GettersUsers(MethodView, Responses):

    def __init__(self):
        super(GettersUsers, self).__init__()
        self.keys = []
        self.db = DBConnector()
        self.data = {}
        self.token = ""

    def get(self):
        try:
            # if request.headers.get('token') == SECRET_HASH:
            if request.data != b'':
                self.data['user_id'] = request.json['user_id']
            else:
                self.data['user_id'] = request.args.get('user_id')
            users = self.db.query(GET_USERS_FROM_DOMAIN.format(self.data['user_id'])).fetchall()
            if users is None or len(users) == 0:
                return selfresponse(200, success=True, msg="Not found users", users=[])
            for user in users:
                shortcut = str(user[1])[:1]
                if " " in str(user[1]):
                    shortcut = "{0}{1}".format(shortcut, str(user[1]).split(" ")[1][:1])
                self.keys.append({"id": user[0], "user": user[1], "privilege": user[2], 'shortcut': shortcut.upper()})
            return self.response(200, success=True, msg="Users({0})".format(len(users)), users=self.keys)
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="GettersUsers.get[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def get_privileges(self, user_id):
        try:
            result = self.db.query(GET_PRIVILEGE.format(user_id)).fetchone()
            return result[0]
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="GettersUsers.get_privileges[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return "Not granted"

    def post(self):
        return self.method_not_allowed("GettersUsers.post", 'post')

    def put(self):
        return self.method_not_allowed("GettersUsers.put", 'put')

    def delete(self):
        return self.method_not_allowed("GettersUsers.delete", 'delete')


class GettersUser(MethodView, Responses):

    def __init__(self):
        super(GettersUser, self).__init__()
        self.keys = []
        self.db = DBConnector()
        self.data = ""
        self.token = ""

    def get(self):
        try:
            # if request.headers.get('token') == SECRET_HASH:
            self.data = request.json
            users = self.db.query(GET_USERS_FROM_DOMAIN.format(request.json['user_id'])).fetchall()
            if users is None or len(users) == 0:
                return self.response(200, success=True, msg="Not found users", domains=[])
            for user in users:
                self.keys.append({"id": user[0], "users": user[1], "privilege": user[2]})
            return self.response(200, success=True, msg="Users({0})".format(len(users)), domains=self.keys)
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="GettersUser.get[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def post(self):
        return self.method_not_allowed("GettersUser.post", 'post')

    def put(self):
        return self.method_not_allowed("GettersUser.put", 'put')

    def delete(self):
        return self.method_not_allowed("GettersUser.delete", 'delete')


class GettersUserInfo(MethodView, Responses):

    def __init__(self):
        super(GettersUserInfo, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.domains = []
        self.data = ""
        self.token = ""

    def get(self):
        try:
            for key in ['user_id']:
                self.keys[key] = request.args.get('user_id')
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            user = self.db.query(GET_USER_INFO_ALL.format(self.keys['user_id'])).fetchone()
            if user is None or len(user) == 0:
                return self.response(202, success=False, msg="Not found user", domains=[])
            domains = self.db.query(GET_DOMAINS.format(self.keys['user_id'])).fetchall()
            for vals in domains:
                self.domains.append({"id": vals[0], "domain_desc": vals[1]})
            return self.response(200, success=True, msg="User found", name=user[0], email=user[1],
                                 created_at=user[2].strftime("%Y-%m-%d %H:%M:%S"), domains=self.domains)
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="GettersUserInfo.get[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def post(self):
        return self.method_not_allowed("GettersUserInfo.post", 'post')

    def put(self):
        return self.method_not_allowed("GettersUserInfo.put", 'put')

    def delete(self):
        return self.method_not_allowed("GettersUserInfo.delete", 'delete')


class GettersDocs(MethodView, Responses):

    def __init__(self):
        super(GettersDocs, self).__init__()

    def get(self):
        return render_template("docs.html", message="response")

    def post(self):
        return self.method_not_allowed("GettersDocs.post", 'post')

    def put(self):
        return self.method_not_allowed("GettersDocs.put", 'put')

    def delete(self):
        return self.method_not_allowed("GettersDocs.delete", 'delete')


class GettersUserAdmin(MethodView, Responses):

    def __init__(self):
        super(GettersUserAdmin, self).__init__()

    def get(self):
        return self.method_not_allowed("GettersUserAdmin.get", 'get')

    def post(self):
        return self.method_not_allowed("GettersUserAdmin.post", 'post')

    def put(self):
        return self.method_not_allowed("GettersUserAdmin.put", 'put')

    def delete(self):
        return self.method_not_allowed("GettersUserAdmin.delete", 'delete')


class GettersUserKierownik(MethodView, Responses):

    def __init__(self):
        super(GettersUserKierownik, self).__init__()

    def get(self):
        return self.method_not_allowed("GettersUserKierownik.get", 'get')

    def post(self):
        return self.method_not_allowed("GettersUserKierownik.post", 'post')

    def put(self):
        return self.method_not_allowed("GettersUserKierownik.put", 'put')

    def delete(self):
        return self.method_not_allowed("GettersUserKierownik.delete", 'delete')


class GettersUserWorker(MethodView, Responses):

    def __init__(self):
        super(GettersUserWorker, self).__init__()

    def get(self):
        return self.method_not_allowed("GettersUserWorker.get", 'get')

    def post(self):
        return self.method_not_allowed("GettersUserWorker.post", 'post')

    def put(self):
        return self.method_not_allowed("GettersUserWorker.put", 'put')

    def delete(self):
        return self.method_not_allowed("GettersUserWorker.delete", 'delete')
