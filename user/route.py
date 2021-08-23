from flask import Blueprint, session
from flask import request
from models import User, Occupation, Major, School
from ext import db
import json
import time

user_bp = Blueprint(name="user", import_name="__name__", static_folder="static", template_folder="template")


@user_bp.route("/login", methods=['POST'])
def login():
    req_str = request.get_data(as_text=True)
    try:
        status = 0
        req_dict = json.loads(req_str)
        username = req_dict['username']
        password = req_dict['password']
        user_item = db.session.query(User).filter_by(username=username).all()
        if len(user_item) > 0:
            if password == user_item[0].password:
                status = 1  # Login succeeded
                session['user'] = user_item[0].id
            else:
                status = 2  # Wrong password
        else:
            status = 3  # Username doesn't exists
        return json.dumps({'status': status})
    except Exception as e:
        print(e)
        print("Error when phasing request json string!")
        return json.dumps({'status': 999})


@user_bp.route("/register", methods=['POST'])
def register():
    req_str = request.get_data(as_text=True)
    try:
        status = 0
        req_dict = json.loads(req_str)
        username = req_dict['username']
        password = req_dict['password']
        phone = req_dict['phone']
        if username == "" and password == "" and phone == "":
            status = 3  # Empty info is forbidden
        else:
            user_item = db.session.query(User).filter_by(username=username).all()
            if len(user_item) > 0:
                status = 2  # Username has already been occupied
            else:
                regtime = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
                user = User(username=username, password=password, phone=phone, nickname=username, regtime=regtime)
                db.session.add(user)
                db.session.commit()
                status = 1  # Login succeeded
        return json.dumps({'status': status})
    except Exception as e:
        print(e)
        print("Error when phasing request json string!")
        return json.dumps({'status': 999})


@user_bp.route("/info", methods=['GET'])
def get_info():
    status = 0
    if session.get("user") is not None:
        user = session.get("user")
        user_item = db.session.query(User).filter_by(id=user).first()
        status = 1
        return json.dumps({'status': status, 'info': {'username': user_item.username, 'nickname': user_item.nickname}})
    else:
        status = 100
        return json.dumps({'status': status})


@user_bp.route("/occupation", methods=['GET'])
def get_occupation():
    status = 0
    if session.get("user") is not None:
        occupation_item = db.session.query(Occupation).all()
        occupation_list = []
        for occupation in occupation_item:
            occupation_list.append({'id': occupation.id, 'name': occupation.name})
        status = 1
        return json.dumps({'status': status, 'occupation': occupation_list}, ensure_ascii=False)
    else:
        status = 100
        return json.dumps({'status': status})


@user_bp.route("/major", methods=['GET'])
def get_major():
    status = 0
    if session.get("user") is not None:
        major_item = db.session.query(Major).all()
        major_list = []
        for major in major_item:
            major_list.append({'id': major.id, 'name': major.name})
        status = 1
        return json.dumps({'status': status, 'major': major_list}, ensure_ascii=False)
    else:
        status = 100
        return json.dumps({'status': status})


@user_bp.route("/school", methods=['GET'])
def get_school():
    status = 0
    if session.get("user") is not None:
        school_item = db.session.query(School).all()
        school_list = []
        for school in school_item:
            school_list.append({'id': school.id, 'name': school.name})
        status = 1
        return json.dumps({'status': status, 'school': school_list}, ensure_ascii=False)
    else:
        status = 100
        return json.dumps({'status': status})
