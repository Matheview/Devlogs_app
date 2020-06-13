from flask import Flask, json, render_template
from flask_mail import Mail, Message
from modules.user import *
from modules.domain import *
from modules.task import *
from modules.notification import *
from modules.project import *
from modules.gettery import *
from modules.responses import *

app = Flask(__name__)
app.config['MAIL_SERVER'] = 'nazwaszymons.nazwa.pl'
app.config['MAIL_PORT'] = 465
app.config['MAIL_USE_TLS'] = False
app.config['MAIL_USE_SSL'] = True
app.config['MAIL_USERNAME'] = 'automat@devslog.pl'
app.config['MAIL_DEFAULT_SENDER'] = 'automat@devslog.pl'
app.config['MAIL_PASSWORD'] = 'AutomatDevslog16'
mail = Mail(app)
app.add_url_rule('/users/login', view_func=Login.as_view('login'))
app.add_url_rule('/users/register', view_func=Register.as_view('registerUser'))
app.add_url_rule('/users/notifications', view_func=NotificationGet.as_view('notificationGet'))
app.add_url_rule('/users/permission', view_func=UpdatePermission.as_view('updatePermission'))
app.add_url_rule('/statuses/config', view_func=StatusActions.as_view('statusActions'))
app.add_url_rule('/tasks/config', view_func=TasksActions.as_view('tasksActions'))
app.add_url_rule('/tasks/update', view_func=UpdateStatusUser.as_view('updateStatusUser'))
app.add_url_rule('/comments/config', view_func=CommentActions.as_view('commentActions'))
app.add_url_rule('/changepassword', view_func=ChangePasswdLink.as_view('changePasswdLink'))
app.add_url_rule('/domains/register', view_func=DomainsRegister.as_view('registerDomain'))
app.add_url_rule('/projects/register', view_func=AddProject.as_view('addProject'))
app.add_url_rule('/projects/adduser', view_func=AddUserToProject.as_view('addUserToProject'))
app.add_url_rule('/project/users', view_func=GettersUsersFromProject.as_view('gettersUsersFromProject'))
app.add_url_rule('/project/removeuser', view_func=RemoveUserFromProject.as_view('removeUserFromProject'))
app.add_url_rule('/users/reset', view_func=GeneratePassword.as_view('generatePassword'))
app.add_url_rule('/getinfo/tasks', view_func=GetFullTaskInfo.as_view('getFullTaskInfo'))
app.add_url_rule('/getinfo/domains', view_func=GettersDomains.as_view('getterDomains'))
app.add_url_rule('/getinfo/projects', view_func=GettersProjects.as_view('getterProjects'))
app.add_url_rule('/getinfo/projects/tasks', view_func=GetTasksFromProject.as_view('getTasksFromProject'))
app.add_url_rule('/getinfo/users', view_func=GettersUsers.as_view('getterUsers'))
app.add_url_rule('/getinfo/user', view_func=GettersUserInfo.as_view('getterUserInfo'))
app.add_url_rule('/getinfo/users/admin', view_func=GettersUserAdmin.as_view('getterUserAdmin'))
app.add_url_rule('/getinfo/users/kierownik', view_func=GettersUserKierownik.as_view('getterUserKierownik'))
app.add_url_rule('/getinfo/users/worker', view_func=GettersUserWorker.as_view('getterUserWorker'))
app.add_url_rule('/docs', view_func=GettersDocs.as_view('getterDocs'))
app.add_url_rule('/reports/getdata', view_func=GenerateRaport.as_view('generateRaport'))
app.add_url_rule('/reports/render', view_func=GenerateRaportTemplate.as_view('generateRaportTemplate'))
#app.add_url_rule('/projects/register', view_func=DomainsRegister.as_view('registerDomain'))
#app.add_url_rule('/domains/register', view_func=DomainRegister.as_view('register'))


@app.route("/users/changepasswd", methods=['POST'])
def index():
    print("here")
    keys = {}
    db = DBConnector()
    hash = db.get_token()
    data = ""
    token = ""
    try:
        for key in ['email']:
            keys[key] = request.json[key]
    except Exception as e:
        resp = Response(response=json.dumps({"success": False, "msg": "Key errors"}), status=202)
        resp.headers["Content-Type"] = 'application/json'
        return resp
    account = db.check_user_exists_change_passwd(keys['email'])
    if not account[0]:
        resp = Response(response=json.dumps({"success": False, "msg": "Email not found"}), status=202)
        resp.headers["Content-Type"] = 'application/json'
        return resp
    else:
        token = "{0}##{1}".format(account[1], hash)
        db.save_logging(account[1], token)
        msg = Message("[Devslog] Zmiana hasła", recipients=[keys['email']])
        msg.html = render_template("mail_change_password.html", ip_addres=request.remote_addr, token="http://devslog.pl:4742/changepassword?token={0}".format(token.replace("##", "@")))
        mail.send(msg)
    resp = Response(response=json.dumps({"success": True, "msg": "Email sended"}), status=200)
    resp.headers["Content-Type"] = 'application/json'
    return resp


if __name__ == '__main__':
    app.run(threaded=True, host='0.0.0.0', port=4742)
