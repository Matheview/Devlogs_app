# coding=utf-8
from flask.views import MethodView
from flask import request, json, Response, render_template
from connectors.dbconfig import DBConnector
from modules.responses import *
from datetime import datetime
import re


class NotificationGet(MethodView, Responses):

    def __init__(self):
        super(NotificationGet, self).__init__()
        self.keys = []
        self.keys_disct = {}
        self.db = DBConnector()
        self.data = {}
        self.token = ""
        self.non_readed = 0

    def get(self):
        try:
            if request.data != b'':
            # if request.headers.get('token') == SECRET_HASH:
                self.data['user_id'] = request.json['user_id']
            else:
                self.data['user_id'] = request.args.get('user_id')
            notifictions = self.db.query(GET_NOTIFICATIONS.format(self.data['user_id'])).fetchall()
            if notifictions is None or len(notifictions) == 0:
                return self.response(200, success=True, msg="Not found notifications(0)", notifications=[])
            for notify in notifictions:
                if not notify[3]:
                    self.non_readed += 1
                self.keys.append({"id": notify[0], "description": notify[1], "readed": bool(notify[3]), "created_at": datetime.strftime(notify[2], '%Y-%m-%d %H:%M:%S')})
            return self.response(200, success=True, msg="Notifications({0})".format(len(notifictions)),
                                 notifications=self.keys, qty=self.non_readed)
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="NotificationGet.get[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def post(self):
        return self.method_not_allowed("GettersDomains.post", 'post')

    def put(self):
        try:
            self.data = request.json
            for key in ['user_id', 'notify_id']:
                # self.keys[key] = request.form.get(key)
                self.keys_disct[key] = request.json[key]
                if not self.keys_disct[key] or self.keys_disct[key] is None:
                    return self.response(202, success=False, msg="Key '{0}' not found".format(key))
                elif self.keys_disct[key] == 0 or self.keys_disct[key] == "":
                    return self.response(202, success=False, msg="Value '{0}' cannot be empty".format(key))
                elif self.keys_disct[key] is None or self.keys_disct[key] == "None" or self.keys_disct[key] == "null":
                    return self.response(202, success=False, msg="Value'{0}' cannot be null".format(key))
            self.db.query(MARK_NOTIFY_AS_READED.format(self.keys_disct['notify_id'], self.keys_disct['user_id']))
            return self.response(200, success=True, msg="Notification marked")
        except Exception as e:
            _, _, exc_tb = sys.exc_info()
            self.logs.save_msg(e, localisation="NotificationGet.put[{0}]".format(exc_tb.tb_lineno), args=self.data)
            return self.response(202, success=False, msg="Unexpected exception: reported")

    def delete(self):
        return self.method_not_allowed("GettersDomains.delete", 'delete')