# coding=utf-8
from flask.views import MethodView
from flask import request, json, Response, render_template, make_response
from connectors.dbconfig import DBConnector
from modules.responses import *
import re
from datetime import datetime
from random import randint


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
                self.arr.append({'user_count': i[0], 'project_id': i[1], 'project_name': "#{0} {1}".format(i[1], i[2]), 'domain_name': i[3], 'privilege': i[4]})
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
            if not self.data['user_id']:
                self.logs.save_msg("KeyError 'user_id' not found", localisation="GettersUsers.get[]", args=self.data)
                return self.response(202, success=False, msg="Key 'user_id' not found")
            users = self.db.query(GET_USERS_FROM_DOMAIN.format(self.data['user_id'])).fetchall()
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


class GettersUsersFromProject(MethodView, Responses):

    def __init__(self):
        super(GettersUsersFromProject, self).__init__()
        self.keys = []
        self.db = DBConnector()
        self.data = {}
        self.token = ""

    def get(self):
        try:
            for key in ['user_id', 'domain']:
                if request.data != b'':
                    self.data[key] = request.json[key]
                else:
                    self.data[key] = request.args.get(key)
            if not self.data['user_id'] or not self.data['domain']:
                self.logs.save_msg(
                    "KeyError '{0}' not found".format("user_id" if not self.data['user_id'] else "domain"),
                    localisation="GettersUsersFromProject.get[]", args=self.data)
                return self.response(202, success=False, msg="KeyError '{0}' not found".format(
                    "user_id" if not self.data['user_id'] else "domain"))
            user = self.db.check_kierownik(self.data['user_id'])
            if not user:
                return self.response(202, success=False, msg="Not found privileges as Kierownik", users=[])
            users = self.db.query(GET_USERS_FROM_DOMAIN_KIEROWNIK.format(self.data['user_id'], self.data['domain'])).fetchall()
            for user in users:
                shortcut = str(user[1])[:1]
                if " " in str(user[1]):
                    shortcut = "{0}{1}".format(shortcut, str(user[1]).split(" ")[1][:1])
                self.keys.append({"id": user[0], "user": user[1], "privilege": user[2], 'shortcut': shortcut.upper()})
            return self.response(200, success=True, msg="Users({0})".format(len(users)), users=self.keys)

        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="GettersUsersFromProject.get[{0}]".format(exc_tb.tb_lineno), args=self.data)
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


class GetTasksFromProject(MethodView, Responses):

    def __init__(self):
        super(GetTasksFromProject, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.domains = []
        self.data = ""
        self.token = ""
        self.users = []
        self.statuses = []

    def get(self):
        try:
            for key in ['project_id']: #, 'user_id']:
                # request.form.get(key)
                self.keys[key] = request.args.get(key)
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            project_info = self.db.get_project_info(self.keys)
            if not project_info[0]:
                return self.response(202, success=True, msg="Project not found", project_id="",
                                     project_name="",
                                     users=[], statuses=[])
            self.users = self.db.get_all_users_from_project(self.keys)
            self.statuses = self.db.get_statuses_from_project(self.keys)
            return self.response(200, success=True, msg="Project tasks found", project_id=int(self.keys['project_id']),
                                 project_name="#{0} {1}".format(self.keys['project_id'], project_info[1]),
                                 users=self.users, statuses=self.statuses)
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="GetTasksFromProject.get[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def post(self):
        return self.method_not_allowed("GettersUserWorker.post", 'post')

    def put(self):
        return self.method_not_allowed("GettersUserWorker.put", 'put')

    def delete(self):
        return self.method_not_allowed("GettersUserWorker.delete", 'delete')


class GeneratePDFReport(MethodView, Responses):

    def __init__(self):
        super(GeneratePDFReport, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.domains = []
        self.data = ""
        self.token = ""
        self.users = []
        self.statuses = []

    def get(self):
        try:

            render = render_template('docs.html')
            pdf = pfdkit.from_string(render, False)
            response = make_response(pdf)
            response.headers['Content-Type'] = 'application/pdf'
            response.headers['Content-Disposition'] = 'inline; filename=devslog_{}.pdf'.format(datetime.now().strftime("%Y%m%d%H%M%S"))
            return response
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="GeneratePDFReport.get[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def post(self):
        return self.method_not_allowed("GettersUserWorker.post", 'post')

    def put(self):
        return self.method_not_allowed("GettersUserWorker.put", 'put')

    def delete(self):
        return self.method_not_allowed("GettersUserWorker.delete", 'delete')


class GenerateRaport(MethodView, Responses):

    def __init__(self):
        super(GenerateRaport, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.domains = []
        self.data = {}
        self.token = ""
        self.users = []
        self.statuses = []

    def post(self):
        return self.method_not_allowed("GettersUserWorker.post", 'post')

    def get(self):
        try:
            for key in ['user_id', 'type', 'domain']:
                self.keys[key] = request.args.get(key)
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            for key in ['type', 'params', 'project_id']:
                try:
                    if key == 'type':
                        self.keys[key] = int(self.keys[key])
                    else:
                        self.keys[key] = request.args.get(key)
                except:
                    self.keys[key] = None
            if 0 < int(self.keys['type']) < 4:
                domain = self.db.query(CHECK_DOMAIN_EXIST.format(self.keys['domain'])).fetchone()
                if not domain:
                    return self.response(202, success=False, msg="Domain not found")
                user = self.db.check_kierownik(self.keys['user_id'])
                if not user:
                    return self.response(202, success=False, msg="Not found privileges as Kierownik")
                else:
                    if self.keys['type'] == 1 and self.keys['params'] in ['all', 'mine']:
                        count = 0
                        projects = self.db.query(GET_PROJECT_LIST_TO_ALL.format(self.keys['domain'])).fetchall()
                        self.data['projects'] = []
                        for project in projects:
                            if (self.keys['params'] == 'mine' and project[2] == self.keys['user_id']) or self.keys['params'] == 'all':
                                count += 1
                            user_count = self.db.query(GET_USER_COUNT_INSIDE_PROJECT.format(project[1])).fetchone()
                            tasks = self.db.query(GET_TASKS_FROM_PROJECT.format(project[1])).fetchall()
                            self.data['projects'].append({'project_name': project[0],
                                                          'project_id': project[1],
                                                          'project_creator_id': project[2],
                                                          'project_users_count': 0 if not user_count else user_count[0],
                                                          'projectCreateDate': project[3],
                                                          'tasks_count': len(tasks) if tasks else 0,
                                                          'tasks':[]})
                        self.data['projects_count'] = count
                        self.data['statuses'] = []
                        statuses = self.db.query(GET_STATUSES_FROM_DOMAIN_WHERE_PROJECTS.format(self.keys['domain'])).fetchall()
                        for status in statuses:
                            self.data['statuses'].append({'status_id': status[0], "status_desc": status[1]})
                        self.data['users'] = []
                        users = self.db.query(GET_ALL_USERS_INSIDE_DOMAIN.format(self.keys['domain'])).fetchall()
                        for user in users:
                            shortcut = str(user[2])[:1]
                            if " " in str(user[2]):
                                shortcut = "{0}{1}".format(shortcut, str(user[2]).split(" ")[1][:1])
                            if (self.keys['params'] == 'mine' and users[0] == self.keys['user_id']) or self.keys['params'] == 'all':
                                add = True
                                for user_copy in self.data['users']:
                                    if user[1] == user_copy['granted_id']:
                                        add = False
                                if add:
                                    self.data['users'].append({
                                                          "granted_id": user[1],
                                                          "userName": user[2],
                                                          "userIsBoss": bool(user[3]),
                                                          "user_shortcut": shortcut})
                        return self.response(200, success=True, msg="Report found", data=self.data)
                    elif self.keys['type'] in (2, 3) and self.keys['project_id']:
                        count = 1
                        projects = self.db.query(GET_PROJECT_LIST_TO_MINE.format(self.keys['project_id'])).fetchall()
                        self.data['projects'] = []
                        for project in projects:
                            user_count = self.db.query(GET_USER_COUNT_INSIDE_PROJECT.format(self.keys['project_id'])).fetchone()
                            tasks = self.db.query(GET_TASKS_FROM_PROJECT.format(self.keys['project_id'])).fetchall()
                            task_dict = []
                            for task in tasks:
                                task_dict.append({
                                    "task_id": task[0],
                                    "task_name": task[1],
                                    "task_create_date": task[2],
                                    "task_deadline": task[3],
                                    "task_priority": task[4],
                                    "status_id": task[5],
                                    "user_assigned_id": task[6],
                                })
                            self.data['projects'].append({'project_name': project[0],
                                                          'project_id': project[1],
                                                          'project_creator_id': project[2],
                                                          'project_users_count': 0 if not user_count else user_count[0],
                                                          'projectCreateDate': project[3],
                                                          'tasks_count': len(tasks) if tasks else 0,
                                                          'tasks': task_dict})
                        self.data['projects_count'] = count
                        self.data['statuses'] = []
                        statuses = self.db.query(
                            GET_STATUSES_FROM_PROJECT_WHERE_PROJECTS.format(self.keys['project_id'])).fetchall()
                        for status in statuses:
                            self.data['statuses'].append({'status_id': status[0], "status_desc": status[1]})
                        self.data['users'] = []
                        users = self.db.query(GET_ALL_USERS_INSIDE_PROJECT.format(self.keys['project_id'])).fetchall()
                        for user in users:
                            shortcut = str(user[2])[:1]
                            if " " in str(user[2]):
                                shortcut = "{0}{1}".format(shortcut, str(user[2]).split(" ")[1][:1])
                                self.data['users'].append({
                                    "granted_id": user[1],
                                    "userName": user[2],
                                    "userIsBoss": bool(user[3]),
                                    "user_shortcut": shortcut})
                        return self.response(200, success=True, msg="Report found", data=self.data)
                    else:
                        return self.response(202, success=False, msg="Report type not supported")
            else:
                return self.response(202, success=False, msg="Report type not supported")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="GenerateRaport.post[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def put(self):
        return self.method_not_allowed("GettersUserWorker.put", 'put')

    def delete(self):
        return self.method_not_allowed("GettersUserWorker.delete", 'delete')
    
    
class GenerateRaportTemplate(MethodView, Responses):

    def __init__(self):
        super(GenerateRaportTemplate, self).__init__()
        self.keys = {}
        self.db = DBConnector()
        self.domains = []
        self.data = {}
        self.token = ""
        self.users = []
        self.statuses = []

    def post(self):
        return self.method_not_allowed("GettersUserWorker.post", 'post')

    def get(self):
        try:
            for key in ['user_id', 'type', 'domain']:
                self.keys[key] = request.args.get(key)
                if not self.keys[key] or self.keys[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys[key] == 0 or self.keys[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys[key] is None or self.keys[key] == "None" or self.keys[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            for key in ['type', 'params', 'project_id']:
                try:
                    if key == 'type':
                        self.keys[key] = int(self.keys[key])
                    else:
                        self.keys[key] = request.args.get(key)
                except:
                    self.keys[key] = None
            if 0 < int(self.keys['type']) < 4:
                domain = self.db.query(CHECK_DOMAIN_EXIST.format(self.keys['domain'])).fetchone()
                if not domain:
                    return self.response(202, success=False, msg="Domain not found")
                user = self.db.check_kierownik(self.keys['user_id'])
                if not user:
                    return self.response(202, success=False, msg="Not found privileges as Kierownik")
                else:
                    if self.keys['type'] == 1 and self.keys['params'] in ['all', 'mine']:
                        count = 0
                        projects = self.db.query(GET_PROJECT_LIST_TO_ALL.format(self.keys['domain'])).fetchall()
                        name = self.db.query(GET_USERNAME_BY_ID.format(self.keys["user_id"])).fetchone()
                        self.data['kierownik_name'] = name[0]
                        self.data['projects'] = []
                        link_tasks = """render?user_id={0}&type={1}&domain={2}&project_id={3}"""
                        for project in projects:
                            if (self.keys['params'] == 'mine' and project[2] == int(self.keys['user_id'])) or self.keys['params'] == 'all':
                                count += 1
                                user_count = self.db.query(GET_USER_COUNT_INSIDE_PROJECT.format(project[1])).fetchone()
                                tasks = self.db.query(GET_TASKS_FROM_PROJECT.format(project[1])).fetchall()
                                self.data['projects'].append({'project_name': project[0],
                                                              'project_id': project[1],
                                                              'project_creator_id': project[2],
                                                              'project_users_count': 0 if not user_count else user_count[0],
                                                              'projectCreateDate': project[3],
                                                              'tasks_count': len(tasks) if tasks else 0,
                                                              'project_url': link_tasks.format(self.keys['user_id'], 2, self.keys['domain'], project[1]),
                                                              'tasks':[]})
                        self.data['projects_count'] = count
                        self.data['statuses'] = []
                        statuses = self.db.query(GET_STATUSES_FROM_DOMAIN_WHERE_PROJECTS.format(self.keys['domain'])).fetchall()
                        for status in statuses:
                            self.data['statuses'].append({'status_id': status[0], "status_desc": status[1]})
                        self.data['users'] = []
                        users = self.db.query(GET_ALL_USERS_INSIDE_DOMAIN.format(self.keys['domain'])).fetchall()
                        for user in users:
                            shortcut = str(user[2])[:1]
                            if " " in str(user[2]):
                                shortcut = "{0}{1}".format(shortcut, str(user[2]).split(" ")[1][:1])
                            if (self.keys['params'] == 'mine' and users[0] == self.keys['user_id']) or self.keys['params'] == 'all':
                                add = True
                                for user_copy in self.data['users']:
                                    if user[1] == user_copy['granted_id']:
                                        add = False
                                if add:
                                    self.data['users'].append({
                                                          "granted_id": user[1],
                                                          "userName": user[2],
                                                          "userIsBoss": bool(user[3]),
                                                          "user_shortcut": shortcut})
                        link_projects = """render?user_id={0}&type={1}&domain={2}&params={3}"""
                        return render_template('raports.html', data=self.data,
                                               link_all=link_projects.format(self.keys['user_id'], self.keys['type'], self.keys['domain'], "all"),
                                               link_mine=link_projects.format(self.keys['user_id'], self.keys['type'], self.keys['domain'], "mine"),
                                               all=True if self.keys['params'] == "all" else False)
                    elif self.keys['type'] in (2, 3) and self.keys['project_id']:
                        count = 1
                        projects = self.db.query(GET_PROJECT_LIST_TO_MINE.format(self.keys['project_id'])).fetchall()
                        self.data['projects'] = []
                        domain_name = self.db.query(GET_DOMAIN_NAME_BY_ID.format(self.keys['domain'])).fetchone()
                        name = self.db.query(GET_USER_INFO_ALL.format(projects[0][2])).fetchone()
                        for project in projects:
                            user_count = self.db.query(GET_USER_COUNT_INSIDE_PROJECT.format(self.keys['project_id'])).fetchone()
                            tasks = self.db.query(GET_TASKS_FROM_PROJECT.format(self.keys['project_id'])).fetchall()
                            task_dict = []
                            for task in tasks:
                                try:
                                    if task[6]:
                                        name_user_task = self.db.query(GET_USER_INFO_ALL.format(task[6])).fetchone()
                                    else:
                                        name_user_task = [""]
                                except:
                                    name_user_task = [""]
                                task_dict.append({
                                    "task_id": task[0],
                                    "task_name": task[1],
                                    "task_create_date": task[2],
                                    "task_deadline": task[3],
                                    "task_priority": task[4],
                                    "status_id": task[5],
                                    "user_assigned_id": task[6],
                                    "user_assigned_name": name_user_task[0]
                                })
                            self.data['projects'].append({'project_name': project[0],
                                                          'domain_name': domain_name[0],
                                                          'project_id': project[1],
                                                          'project_creator_id': project[2],
                                                          'project_creator_name': name[0],
                                                          'project_users_count': 0 if not user_count else user_count[0],
                                                          'projectCreateDate': project[3],
                                                          'tasks_count': len(tasks) if tasks else 0,
                                                          'tasks': task_dict})
                        self.data['projects_count'] = count
                        self.data['statuses'] = []
                        self.data['statuses_arr'] = []
                        statuses = self.db.query(
                            GET_STATUSES_FROM_PROJECT_WHERE_PROJECTS.format(self.keys['project_id'])).fetchall()
                        for status in statuses:
                            self.data['statuses_arr'].append(status[1])
                            self.data['statuses'].append({'status_id': status[0], "status_desc": status[1]})
                        self.data['users'] = []
                        users = self.db.query(GET_ALL_USERS_INSIDE_PROJECT.format(self.keys['project_id'])).fetchall()
                        for user in users:
                            shortcut = str(user[2])[:1]
                            if " " in str(user[2]):
                                shortcut = "{0}{1}".format(shortcut, str(user[2]).split(" ")[1][:1])
                                self.data['users'].append({
                                    "granted_id": user[1],
                                    "userName": user[2],
                                    "userIsBoss": bool(user[3]),
                                    "user_shortcut": shortcut})
                        link_projects = """render?user_id={0}&type={1}&domain={2}&params={3}"""
                        link_tasks = """render?user_id={0}&type={1}&domain={2}&project_id={3}"""
                        if self.keys['type'] == 2:
                            return render_template('generalraport.html', data=self.data,
                                               link_general=link_tasks.format(self.keys['user_id'], 2,
                                                                             self.keys['domain'], self.keys['project_id']),
                                               link_detailed=link_tasks.format(self.keys['user_id'], 3,
                                                                              self.keys['domain'], self.keys['project_id']),
                                               back_link=link_projects.format(self.keys['user_id'], 1, self.keys['domain'], "all"))
                        elif self.keys['type'] == 3:
                            self.data['statuses_arr_count'] = []
                            self.data['statuses_arr_bgcolor'] = []
                            self.data['statuses_arr_bordercolor'] = []
                            for status in self.data['statuses']:
                                count = 0
                                for task in self.data['projects'][0]['tasks']:
                                    if status['status_id'] == task['status_id']:
                                        count += 1
                                self.data['statuses_arr_count'].append(count)
                                self.data['statuses_arr_bgcolor'].append("rgba({0}, {1}, {2}, 0.2)".format(randint(0, 255), randint(0, 255), randint(0, 255)))
                                self.data['statuses_arr_bordercolor'].append(self.data['statuses_arr_bgcolor'][len(self.data['statuses_arr_bgcolor'])-1].replace("0.2)", "1)"))
                            return render_template('detailedraport.html', data=self.data,
                                               link_general=link_tasks.format(self.keys['user_id'], 2,
                                                                             self.keys['domain'], self.keys['project_id']),
                                               link_detailed=link_tasks.format(self.keys['user_id'], 3,
                                                                              self.keys['domain'], self.keys['project_id']),
                                               back_link=link_projects.format(self.keys['user_id'], 1,
                                                                                  self.keys['domain'], "all"))
                        else:
                            return self.response(202, success=False, msg="Report type not supported")
                    else:
                        return self.response(202, success=False, msg="Report type not supported")
            else:
                return self.response(202, success=False, msg="Report type not supported")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="GenerateRaport.post[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def put(self):
        return self.method_not_allowed("GettersUserWorker.put", 'put')

    def delete(self):
        return self.method_not_allowed("GettersUserWorker.delete", 'delete')
