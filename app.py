from flask import Flask, json
from modules.user import *
from modules.domain import *
from modules.task import *
from modules.notification import *
from modules.project import *
from modules.gettery import *

app = Flask(__name__)

app.add_url_rule('/users/login', view_func=Login.as_view('login'))
app.add_url_rule('/users/register', view_func=Register.as_view('registerUser'))
app.add_url_rule('/domains/register', view_func=DomainsRegister.as_view('registerDomain'))
app.add_url_rule('/getinfo/domains', view_func=GettersDomains.as_view('getterDomains'))
app.add_url_rule('/getinfo/projects', view_func=GettersProjects.as_view('getterProjects'))
app.add_url_rule('/getinfo/users', view_func=GettersUsers.as_view('getterUsers'))
app.add_url_rule('/getinfo/user', view_func=GettersUser.as_view('getterUser'))
app.add_url_rule('/getinfo/users/admin', view_func=GettersUserAdmin.as_view('getterUserAdmin'))
app.add_url_rule('/getinfo/users/kierownik', view_func=GettersUserKierownik.as_view('getterUserKierownik'))
app.add_url_rule('/getinfo/users/worker', view_func=GettersUserWorker.as_view('getterUserWorker'))
app.add_url_rule('/docs', view_func=GettersDocs.as_view('getterDocs'))
#app.add_url_rule('/projects/register', view_func=DomainsRegister.as_view('registerDomain'))
#app.add_url_rule('/domains/register', view_func=DomainRegister.as_view('register'))


if __name__ == '__main__':
    app.run(threaded=True, host='0.0.0.0', port=4742)
