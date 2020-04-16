from flask import Flask, json
from modules.user import *
from modules.domain import *
from modules.task import *
from modules.notification import *
from modules.project import *

app = Flask(__name__)

app.add_url_rule('/users/login', view_func=Login.as_view('login'))
app.add_url_rule('/users/register', view_func=Register.as_view('registerUser'))
app.add_url_rule('/domains/register', view_func=DomainsRegister.as_view('registerDomain'))
#app.add_url_rule('/projects/register', view_func=DomainsRegister.as_view('registerDomain'))
#app.add_url_rule('/domains/register', view_func=DomainRegister.as_view('register'))


if __name__ == '__main__':
    app.run(threaded=True, host='0.0.0.0', port=4742)
