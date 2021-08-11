from flask import Flask
from flask_cors import CORS
import config
import user.route
import _calendar.route
from ext import db
from models import Category


if __name__ == '__main__':
    debug = False
    app = Flask(__name__)
    app.config.from_object(config)
    db.init_app(app)
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
            category_list = ["其他", "学习", "工作", "运动", "出行", "节律"]
            for category in category_list:
                category_item = Category(name=category)
                db.session.add(category_item)
                db.session.commit()
    app.register_blueprint(user.route.user_bp, url_prefix="/user")
    app.register_blueprint(_calendar.route.calendar_bp, url_prefix="/calendar")
    CORS(app, supports_credentials=True)
    app.run(host='0.0.0.0', port=5000, debug=True)
