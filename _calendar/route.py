from flask import Blueprint, session
from flask import request
from ext import db
from models import Schedule, Category
import json
import time

calendar_bp = Blueprint(name="calendar", import_name="__name__", static_folder="static", template_folder="template")


@calendar_bp.route("/add", methods=['POST'])
def add_schedule():
    req_str = request.get_data(as_text=True)
    try:
        status = 0
        req_dict = json.loads(req_str)
        if session.get('user') is not None:
            user = session.get('user')
            uid = req_dict['uuid']
            name = req_dict['name']
            category = req_dict['category']
            start = req_dict['start']
            start_datetime = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime(start))
            end = req_dict['end']
            end_datetime = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime(end))
            schedule = Schedule(uuid=uid, user=user, name=name, category=category, start=start_datetime, end=end_datetime)
            db.session.add(schedule)
            db.session.commit()
            status = 1
        else:
            status = 100
        return json.dumps({'status': status})
    except Exception as e:
        print(e)
        print("Error when phasing request json string!")
        return json.dumps({'status': 999})


@calendar_bp.route("/remove", methods=['POST'])
def remove_schedule():
    req_str = request.get_data(as_text=True)
    try:
        status = 0
        req_dict = json.loads(req_str)
        if session.get('user') is not None:
            user = session.get('user')
            uid = req_dict['uuid']
            schedule_item = db.session.query(Schedule).filter_by(uuid=uid, user=user).first()
            db.session.delete(schedule_item)
            db.session.commit()
            status = 1
        else:
            status = 100
        return json.dumps({'status': status})
    except Exception as e:
        print(e)
        print("Error when phasing request json string!")
        return json.dumps({'status': 999})


@calendar_bp.route("/fetch", methods=['GET'])
def fetch_schedule():
    if session.get('user') is not None:
        status = 0
        user = session.get('user')
        schedule_item = db.session.query(Schedule).filter_by(user=user).all()
        schedule_list = []
        for schedule in schedule_item:
            category = db.session.query(Category).filter_by(id=schedule.category).first()
            schedule_list.append({'uuid': schedule.uuid, 'name': schedule.name, 'category': {'id': category.id, 'name': category.name},
                                  'start': schedule.start.timestamp(), 'end': schedule.end.timestamp()})
        status = 1
        if status == 1:
            return json.dumps({'status': status, 'schedule': schedule_list}, ensure_ascii=False)
        else:
            return json.dumps({'status': status})
    else:
        return json.dumps({'status': 100})


@calendar_bp.route("/category", methods=['GET'])
def get_category():
    if session.get("user") is not None:
        category_item = db.session.query(Category).all()
        category_list = []
        for category in category_item:
            category_list.append({'id': category.id, 'name': category.name})
        return json.dumps({'status': 1, 'category': category_list}, ensure_ascii=False)
    else:
        return json.dumps({'status': 100, 'category': []})
