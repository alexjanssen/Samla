from flask import Flask, request, send_file
from flask_restful import Resource, Api
from sqlalchemy import create_engine
from sqlalchemy.engine import ResultProxy
from json import dumps
from flask_jsonpify import jsonify
import os

db_connect = create_engine("sqlite:///samla.db")
app = Flask(__name__)
api = Api(app)

userdict = dict()

def write_user_log():
    print(os.path.abspath("user.log"))
    with open("user.log", "w") as userlog:
        userlog.write("Userlog: \n\n")
        for user in userdict.keys():
            userlog.write(f"Nutzer {user}: {userdict[user]} Positionen erhalten\n\n")

class AllLocations(Resource):
    def get(self):
        conn = db_connect.connect()
        result: ResultProxy = conn.execute("SELECT * FROM locations")
        return jsonify({"locations": [dict(zip(result.keys(), r)) for r in result.fetchall()]})

    def post(self):
        conn = db_connect.connect()
        print(request.json)
        user = request.json["user"]
        latitude = request.json["latitude"]
        longitude = request.json["longitude"]
        timestamp = request.json["timestamp"]
        query = conn.execute(f"insert into locations (user, latitude, longitude, timestamp) values('{user}', {latitude}, {longitude}, {timestamp})")
        if user not in userdict:
            userdict[user] = 1
        else:
            userdict[user] += 1
        write_user_log()
        return {"status":"success"}

class UserLocation(Resource):
    def get(self, user):
        conn = db_connect.connect()
        result: ResultProxy = conn.execute(f"SELECT * FROM locations WHERE user = '{user}'")
        return jsonify({"locations": [dict(zip(result.keys()[1:], r[1:])) for r in result.fetchall()]})

class UserLog(Resource):
    def get(self):
        return send_file("user.log", attachment_filename="user.log")


class ReInit(Resource):
    def get(self):
        conn = db_connect.connect()
        conn.execute("DROP TABLE locations")
        conn.execute("CREATE TABLE locations (user VARCHAR(255), latitude DOUBLE, longitude DOUBLE, timestamp LONG)")
        return {"status":"reset"}

api.add_resource(AllLocations, "/locations")
api.add_resource(UserLocation, "/locations/<user>")
api.add_resource(UserLog, "/locations/user.log")
api.add_resource(ReInit, "/reset")

if __name__ == '__main__':
    db_connect.connect().execute("CREATE TABLE IF NOT EXISTS locations (user VARCHAR(255), latitude DOUBLE, longitude DOUBLE, timestamp LONG)")
    print(os.path.abspath("."))
    with open("user.log", "w") as userlog:
        userlog.write("Userlog: \n\n")
    app.run(port=31337)
