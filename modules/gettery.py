# coding=utf-8
from flask.views import MethodView
from flask import request, json, Response, render_template
from connectors.dbconfig import DBConnector
from modules.responses import *
import re


class GettersDomains(MethodView, Responses):

    def __init__(self):
        super(GettersDomains, self).__init__()
        self.keys = []
        self.db = DBConnector()
        self.data = ""
        self.token = ""

    def get(self):
        try:
            # if request.headers.get('token') == SECRET_HASH:
            self.data = request.json
            domains = self.db.query(GET_DOMAINS.format(request.json['user_id'])).fetchall()
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


class GettersProjects(MethodView, Responses):

    def __init__(self):
        super(GettersProjects, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.data = ""
        self.token = ""

    def get(self):
        return self.method_not_allowed("GettersUsers.get", 'get')

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
        self.data = ""
        self.token = ""

    def get(self):
        try:
            # if request.headers.get('token') == SECRET_HASH:
            self.data = request.json
            users = self.db.query(GET_USERS_FROM_DOMAIN.format(request.json['user_id'])).fetchall()
            if users is None or len(users) == 0:
                return self.response(200, success=True, msg="Not found users", users=[])
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