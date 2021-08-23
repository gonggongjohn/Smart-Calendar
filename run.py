from flask import Flask
from flask_cors import CORS
import config
import user.route
import _calendar.route
from ext import db
from models import Category, Occupation, Major, School


def init_db():
    with app.app_context():
        create_flag = False
        if debug:
            db.drop_all()
            create_flag = True
        else:
            tables = db.session.execute('show tables').fetchall()
            if len(tables) == 0:
                create_flag = True

        if create_flag:
            db.create_all()
            category_list = ["其他", "课程", "备考", "自主学习", "工作", "运动", "出行", "节律"]
            for category in category_list:
                category_item = Category(name=category)
                db.session.add(category_item)
                db.session.commit()
            occupation_list = ["学生", "教师", "律师", "医生", "金融从业者", "计算机从业者", "建筑从业者", "制造业从业者", "基础学科从业者", "其他"]
            for occupation in occupation_list:
                occupation_item = Occupation(name=occupation)
                db.session.add(occupation_item)
                db.session.commit()
            major_file = open("data/major.txt", encoding='utf8')
            line = major_file.readline()
            while line:
                if line.strip('\n') != "":
                    major_item = Major(name=line.strip('\n'))
                    db.session.add(major_item)
                    db.session.commit()
                line = major_file.readline()
            major_file.close()
            school_file = open("data/school.txt", encoding='utf8')
            line = school_file.readline()
            while line:
                if line.strip('\n') != "":
                    school_item = School(name=line.strip('\n'))
                    db.session.add(school_item)
                    db.session.commit()
                line = school_file.readline()
            school_file.close()


if __name__ == '__main__':
    debug = False
    app = Flask(__name__)
    app.config.from_object(config)
    db.init_app(app)
    init_db()
    app.register_blueprint(user.route.user_bp, url_prefix="/user")
    app.register_blueprint(_calendar.route.calendar_bp, url_prefix="/calendar")
    CORS(app, supports_credentials=True)
    app.run(host='0.0.0.0', port=3000, debug=True)

