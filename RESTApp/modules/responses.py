from flask import request, json, Response
from settings.config import *
from connectors.logging import *


class Responses:

    def __init__(self):
        self.header = {"Content-Type": 'application/json'}
        self.body = {}
        self.status = 404
        self.logs = Logging()

    def response(self, *args, **kwargs):
        self.status = args[0]
        for key, value in kwargs.items():
            self.body[key] = value
        self.to_json()
        resp = Response(response=self.body, status=self.status)
        if len(args) > 1:
            resp.headers['token'] = args[1]
        resp.headers["Content-Type"] = 'application/json'
        return resp

    def method_not_allowed(self, place, method):
        self.logs.save_msg("Method not allowed", localisation="{0}[{1}]".format(place, method.upper()))
        return self.response(405, success=False, msg="Method [{0}] not allowed: log saved".format(method.upper()))

    def to_json(self):
        self.header = json.dumps(self.header)
        self.body = json.dumps(self.body)
