from datetime import datetime
from settings.config import PROJECT_DIR
import sys


class Logging:

    def __init__(self):
        self.err_msg = ""
        self.local = []

    def save_msg(self, msg, localisation=None, args=None):
        self.local = "[{}]".format(localisation) if not None else ""
        self.err_msg = msg
        with open(PROJECT_DIR+"/logs/Devslog_{}.log".format(datetime.now().strftime('%Y-%m-%d')), "a") as myfile:
            myfile.write("{0} {1}[{2}] {3}\r".format(datetime.now().strftime('%Y-%m-%d %H:%M:%S'), localisation, args,
                                               self.err_msg))