from flask.views import MethodView
from flask import request, json, Response
from connectors.dbconfig import DBConnector
from modules.responses import *


class DomainsRegister(MethodView, Responses):

    def __init__(self):
        super(DomainsRegister, self).__init__()
        self.db = DBConnector()
        self.data = {}
        self.keys = {}

    def get(self):
        return self.method_not_allowed('DomainsRegister', 'get')

    def put(self):
        return self.method_not_allowed('DomainsRegister', 'put')

    def delete(self):
        return self.method_not_allowed('DomainsRegister', 'delete')

    def post(self):
        try:
#            print(request.data)
            self.data = request.json
            for key in ['domain', 'user_id']:
                if key not in request.json and 'user_id' not in request.json:
                    self.logs.save_msg("Key 'user_id' required for check admin privileges", localisation="DomainsRegister.post[]", args=self.data)
                    return self.response(202, success=False, msg="Key '{0}' required for check admin privileges".format(key))
                if key not in request.json and 'domain' not in request.json:
                    self.logs.save_msg("Key 'domain' required",
                                       localisation="DomainsRegister.post[]", args=self.data)
                    return self.response(202, success=False, msg="Key '{0}' required".format(key))
                self.keys[key] = request.json[key]
            if self.keys['domain']:
                is_user = self.db.check_admin(self.keys['user_id'])
                if not is_user:
                    return self.response(403, success=False, msg="Permission denied")
                domain = self.db.create_domain(self.keys['domain'], self.keys['user_id'])
                if not domain[0]:
                    return self.response(202, success=False, msg="Domain already exists")
                # self.db.add_to_domain(domain[1], is_user[1])
                return self.response(200, success=True, msg="Domain created")
            else:
                self.logs.save_msg("Unexpected sequence", localisation="DomainsRegister.post", args=self.keys)
                return self.response(403, success=False, msg="Unexpected sequence")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="DomainsRegister.post[{0}]".format(exc_tb.tb_lineno), args=self.keys)
            return self.response(403, success=False, msg="Unexpected error: reported")


